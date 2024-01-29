package syleelsw.anyonesolveit.etc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.api.study.dto.SolvedProblemPages;
import syleelsw.anyonesolveit.api.study.dto.SolvedacPageItem;
import syleelsw.anyonesolveit.api.user.dto.SolvedProblemDto;
import syleelsw.anyonesolveit.api.user.dto.SolvedacUserInfoDto;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component @RequiredArgsConstructor @Slf4j
public class StudyUpdater {
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private static final int THREAD_POOL_SIZE = 5; // Adjust the pool size as needed
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void run() {
        log.info("Update Study...");
        List<Study> studies = studyRepository.findAll();
        for(Study study:studies){
            int rank = 0;
            float solved = 0;
            Set<UserInfo> members = study.getMembers();
            for(UserInfo member: members){
                rank = rank + member.getRank();
                solved += member.getSolved();
            }
            if(members.size()==0){
                continue;
            }
            study.setAvg_rank(rank/ members.size());
            study.setAvg_solved(solved/ members.size());
        }
    }
    @Transactional
    public Callable<Set<Integer>> createTask(String url) {
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

    @Async @Transactional
    public void updateUser(List<UserInfo> users) {
        String solved_dac_url = "https://solved.ac/api/v3";
        CopyOnWriteArrayList<UserInfo> userInfos = new CopyOnWriteArrayList<>();

        for (UserInfo user : users) {
            if(user.getModifiedDateTime().isAfter(LocalDateTime.now().minusHours(1))) continue;
            String username = user.getBjname();
            RestTemplate restTemplate = new RestTemplate();
            String user_level_problem = restTemplate.getForEntity(solved_dac_url + "/user/problem_stats?handle=" + username, String.class).getBody();
            ResponseEntity<SolvedacUserInfoDto> bjUserInfo = restTemplate.getForEntity(solved_dac_url + "/user/show?handle=" + username, SolvedacUserInfoDto.class);
            Long solvedCount = bjUserInfo.getBody().getSolvedCount();
            if(user.getSolved().equals(solvedCount)){continue;}
            user.setRank(bjUserInfo.getBody().getRank());
            user.setSolved(solvedCount);
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

            SolvedProblemDto solvedProblemDto = SolvedProblemDto.builder()
                    .solvedProblems(new ArrayList<>(problemSet))
                    .solved(user_level_problem)
                    .build();
            user.setSolvedProblem(solvedProblemDto.getSolvedProblems());
            userInfos.add(user);
            user.setModifiedDateTime(LocalDateTime.now());
        }
        System.out.println(users.stream().map(t-> t.getSolvedProblem().stream().collect(Collectors.toList()).contains(10172)).collect(Collectors.toList()));
        userRepository.saveAll(userInfos);
    }

    public void saveAll(List<UserInfo> userInfos){
        userRepository.saveAll(userInfos);
    }
}
