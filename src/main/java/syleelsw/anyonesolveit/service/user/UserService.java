package syleelsw.anyonesolveit.service.user;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.api.study.dto.SolvedProblemPages;
import syleelsw.anyonesolveit.api.study.dto.SolvedacItem;
import syleelsw.anyonesolveit.api.study.dto.SolvedacPageItem;
import syleelsw.anyonesolveit.api.user.dto.ParticipationResponse;
import syleelsw.anyonesolveit.api.user.dto.SolvedProblemDto;
import syleelsw.anyonesolveit.api.user.dto.SolvedacUserInfoDto;
import syleelsw.anyonesolveit.api.user.dto.UserProfileDto;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.study.Repository.ParticipationRepository;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.service.validation.ValidationService;

import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class UserService {
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final ParticipationRepository participationRepository;
    private static final int THREAD_POOL_SIZE = 5; // Adjust the pool size as needed
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);


    private String solved_dac_url = "https://solved.ac/api/v3";
    @Transactional
    public ResponseEntity putProfile(String access, UserProfileDto userProfile) {
        Long id = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(id).get();
        if(user.isFirst()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //todo 수정하기.
        Integer rank = validationService.isValidateBJIdAndGetRank(userProfile.getBjname());
        if(rank==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        SolvedProblemDto solvedProblem = getSolvedProblem(userProfile.getBjname());
        user.update(rank, solvedProblem, userProfile);
        return new ResponseEntity(HttpStatus.OK);

    }
    @Transactional
    public ResponseEntity setProfile(String Access, UserProfileDto userProfile){
        log.info("HI1");
        Long id = tokenProvider.getUserId(Access);
        UserInfo user = userRepository.findById(id).get();
        log.info("HI2");
        if(!user.isFirst()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        log.info("HI3");
        Integer rank = validationService.isValidateBJIdAndGetRank(userProfile.getBjname());
        if(rank==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        SolvedProblemDto solvedProblem = getSolvedProblem(userProfile.getBjname());
        user.update(rank, solvedProblem, userProfile);
        return new ResponseEntity(HttpStatus.OK);
    }

    public SolvedProblemDto getSolvedProblem(String username) {
        RestTemplate restTemplate = new RestTemplate();
        String user_level_problem = restTemplate.getForEntity(solved_dac_url + "/user/problem_stats?handle=" + username, String.class).getBody();
        ResponseEntity<SolvedacUserInfoDto> bjUserInfo = restTemplate.getForEntity(solved_dac_url+ "/user/show?handle=" + username, SolvedacUserInfoDto.class);
        Long solvedCount = bjUserInfo.getBody().getSolvedCount();
        log.info("SolvedProblem: {}", solvedCount);

        Set<Integer> problemSet = new HashSet<>();
        int pageCount = (int) (solvedCount / 50) + 1;

        List<Future<Set<Integer>>> futures = new ArrayList<>();

        for (int i = 0; i < pageCount; i++) {
            String url = solved_dac_url + "/search/problem?query=@" + username + "&sort=level&page=" + (i + 1);
            Callable<Set<Integer>> task = createTask(url);
            Future<Set<Integer>> future = executorService.submit(task);
            futures.add(future);
        }

        for (Future<Set<Integer>> future : futures) {
            try {
                problemSet.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                // Handle exceptions as needed
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        log.info("problemSet: {}", problemSet.size());
        return SolvedProblemDto.builder()
                .solvedProblems(problemSet.stream().toList())
                .solved(user_level_problem)
                .build();
    }

    private Callable<Set<Integer>> createTask(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return () -> {
            Set<Integer> set = new HashSet<>();
            ResponseEntity<SolvedProblemPages> response = restTemplate.getForEntity(url, SolvedProblemPages.class);
            for (SolvedacPageItem item : response.getBody().getItems()) {
                set.add(item.getProblemId());
            }
            return set;
        };
    }

    public ResponseEntity getMyApply(String access) {
        Long userId = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Optional<List<Participation>> optionalParticipations = participationRepository.findAllByUser(user);

        return getParticipationListResponseEntity(userId, optionalParticipations);
    }

    private ResponseEntity getParticipationListResponseEntity(Long userId, Optional<List<Participation>> optionalParticipations) {
        List<ParticipationResponse> ret;
        if(optionalParticipations.isEmpty()){
            ret = new ArrayList<>();
        }else{
            ret = optionalParticipations.get().stream().map(t-> new ParticipationResponse(t, userId)).collect(Collectors.toList());
        }
        return new ResponseEntity(ret, HttpStatus.OK);
    }

    public ResponseEntity getMyParticipation(String access) {
        Long userId = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Optional<List<Participation>> optionalParticipations = participationRepository.findMyParticipationsByUser(user);

        return getParticipationListResponseEntity(userId, optionalParticipations);
    }
}
