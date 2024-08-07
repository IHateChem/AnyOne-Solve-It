package syleelsw.anyonesolveit.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.aops.Timer;
import syleelsw.anyonesolveit.api.study.dto.SolvedProblemPages;
import syleelsw.anyonesolveit.api.study.dto.SolvedacItem;
import syleelsw.anyonesolveit.api.user.dto.*;
import syleelsw.anyonesolveit.domain.etc.BaekjoonInformation;
import syleelsw.anyonesolveit.domain.etc.BaekjoonInformationRepository;
import syleelsw.anyonesolveit.domain.study.Notice;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.study.Repository.NoticeRepository;
import syleelsw.anyonesolveit.domain.study.Repository.ParticipationRepository;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.etc.Locations;
import syleelsw.anyonesolveit.service.study.NoticeService;
import syleelsw.anyonesolveit.service.user.dto.RankAndSolvedProblem;
import syleelsw.anyonesolveit.service.validation.ValidationService;
import syleelsw.anyonesolveit.service.validation.dto.ValidateResponse;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static syleelsw.anyonesolveit.etc.StaticValidator.isValidArea;

@Service @RequiredArgsConstructor @Slf4j
public class UserService {
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final ParticipationRepository participationRepository;
    private final BaekjoonInformationRepository baekjoonInformationRepository;
    private final StudyRepository studyRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeService noticeService;
    @Qualifier("taskExecutor")
    private final Executor executor;

    public ResponseEntity searchUserByBjId(String bjId) {
        Optional<Long> idByBjName = userRepository.findIdByBjName(bjId);
        if(idByBjName.isEmpty()) {
            return new ResponseEntity( Map.of("userId", -1) , HttpStatus.OK);
        }else{
            return new ResponseEntity( Map.of("userId", idByBjName.get()) , HttpStatus.OK);
        }
    }


    class Job implements Runnable{
        String url;
        Set set;
        CountDownLatch latch;
        public Job(String url, Set set, CountDownLatch latch){
            this.url = url;
            this.set = set;
            this.latch = latch;
        }
        @Override
        public void run() {
            RestTemplate restTemplate = new RestTemplate();
            try{
                ResponseEntity<SolvedProblemPages> response = restTemplate.getForEntity(url, SolvedProblemPages.class);
                for (SolvedacItem item : response.getBody().getItems()) {
                    set.add(item.getProblemId());
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                latch.countDown();
            }

        }
    }
    private String solved_dac_url = "https://solved.ac/api/v3";
    @Transactional
    public ResponseEntity putProfile(String access, UserProfileDto userProfile) {
        Long id = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(id).get();
        if(user.isFirst()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //todo 수정하기.
        return validBJAndUpdateUser(userProfile, user);

    }

    public ResponseEntity validBJAndUpdateUser(UserProfileDto userProfile, UserInfo user) {
        try{
            RankAndSolvedProblem rankAndProblems = getRankAndSolveProblem(userProfile.getBjname());
            if(!isValidArea(userProfile.getArea().toString() + " " + userProfile.getCity())){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            Integer rank = rankAndProblems.rank;
            SolvedProblemDto solvedProblem = rankAndProblems.solvedProblemDto;
            user.update(rank, solvedProblem, userProfile);
            log.info("업데이트 저장: {}", user);
            userRepository.save(user);
        }catch (IllegalStateException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public RankAndSolvedProblem getRankAndSolveProblem(String bjName){
        Integer rank = validationService.isValidateBJIdAndGetRank(bjName);
        if(rank==null){
            throw new IllegalStateException();
        }
        SolvedProblemDto solvedProblem = getSolvedProblem(bjName);
        return new RankAndSolvedProblem(rank, solvedProblem);
    }


    @Transactional
    public ResponseEntity setProfile(String Access, UserProfileDto userProfile){
        Long id = tokenProvider.getUserId(Access);
        UserInfo user = userRepository.findById(id).get();
        if(!user.isFirst()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return validBJAndUpdateUser(userProfile, user);
    }

    public SolvedProblemDto getSolvedProblem(String username) {
        RestTemplate restTemplate = new RestTemplate();
        String user_level_problem = restTemplate.getForEntity(solved_dac_url + "/user/problem_stats?handle=" + username, String.class).getBody();
        ResponseEntity<SolvedacUserInfoDto> bjUserInfo = restTemplate.getForEntity(solved_dac_url+ "/user/show?handle=" + username, SolvedacUserInfoDto.class);
        Long solvedCount = bjUserInfo.getBody().getSolvedCount();
        log.info("SolvedProblem: {}", solvedCount);
        // 걸리는 시간을 알아보기 위해 Timer를 추가한다.

        int pageCount = (int) (solvedCount / 50) + 1;
        CountDownLatch latch = new CountDownLatch(pageCount);

        List<Set<Long>> problemSetList = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            problemSetList.add(new HashSet<>());
        }

        long startTime = System.nanoTime();
        for (int i = 0; i < pageCount; i++) {
            String url = solved_dac_url + "/search/problem?query=@" + username + "&sort=level&page=" + (i + 1);
            Job task = new Job(url, problemSetList.get(i), latch);
            executor.execute(task);
        }

        try {
            latch.await(); // 모든 작업이 완료될 때까지 대기
            long endTime = System.nanoTime();
            List<Long> resultList = problemSetList.stream().flatMap(Set::stream).collect(Collectors.toList());
            log.info("쓰레드 작업 완료, 걸린시간: {}", (endTime - startTime) / 1_000_000 );
            return SolvedProblemDto.builder()
                    .solvedProblems(resultList)
                    .solved(user_level_problem)
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Tasks interrupted");
        }
        throw new RuntimeException();
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

    public ResponseEntity getMyPage(String access) {
        Long userId = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        List<Participation> suggestions = participationRepository.findMyParticipationsByUser(user).orElse(new ArrayList<>());
        List<Participation> participations = participationRepository.findAllByUser(user).orElse(new ArrayList<>());
        return new ResponseEntity(MyPageResponse.of(user, suggestions.size(), participations.size()), HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity createMyPage(String access, MyPageDto myPage) {
        Long userId = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        if(!validationService.isValidateBJId(myPage.getBjname())){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(!isValidArea(myPage.getArea()+ " "+myPage.getCity())){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        user.setFirst(false);
        setUserInformation(user, myPage);
        log.info("user: {}", user);
        return new ResponseEntity(HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity updateMyPage(String access, MyPageDto myPage) {
        Long userId = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        String bjname = myPage.getBjname();
        if(!validationService.isValidateBJId(bjname)){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(!isValidArea(myPage.getArea()+ " "+myPage.getCity())){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        //update bjInfo
        ResponseEntity<SolvedacUserInfoDto> response = validationService.getSolvedacUserInfoDtoResponseEntity(bjname);
        BaekjoonInformation baekjoonInformation = baekjoonInformationRepository.findById(bjname).get();
        user.setRank(response.getBody().getRank());
        user.setSolved(response.getBody().getSolvedCount());
        setUserInformation(user, myPage);
        return new ResponseEntity(HttpStatus.OK);
    }

    private void setUserInformation(UserInfo user, MyPageDto myPage) {
        //String[] split = myPage.getArea().split(" ");
        //user.setArea(Locations.valueOf(split[0]));
        // user.setCity(split[1]);
        user.setArea(Locations.valueOf(myPage.getArea()));
        user.setCity(myPage.getCity());
        user.setBjname(myPage.getBjname());
        user.setLanguage(myPage.getLanguage());
        user.setPrefer_type(myPage.getPrefer_type());

        BaekjoonInformation baekjoonInformation = baekjoonInformationRepository.findById(myPage.getBjname()).get();
        user.setRank(baekjoonInformation.getRank());
        user.setSolved(baekjoonInformation.getSolved());
    }

    public ResponseEntity<NoticeResponse> getNotices(String access) {
        Long userId = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        return noticeService.getNoticesByUser(user);
    }



    public ResponseEntity<NoticeResponse> mkTestNotices(String access) {
        Long userId = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Study studyOptional = studyRepository.findAllByUser(user).get(0);
        Notice notice = Notice.builder().user(user).toUser(user).noticeType(5).study(studyOptional).build();
        noticeRepository.save(notice);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity delNotices(String access, Long id) {
        Long userId = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Optional<Notice> byId = noticeService.findById(id);
        log.info("del info: {}", byId.get().toString());
        if(byId.isEmpty() || !byId.get().getToUser().equals(user)) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        noticeService.delById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity searchUser(String email) {
        Optional<UserInfo> userByEmail = userRepository.findUserByEmail(email);
        if(userByEmail.isEmpty()){
            return new ResponseEntity(ValidateResponse.builder()
                    .valid(false).build(),HttpStatus.OK);
        }else{

            return new ResponseEntity(ValidateResponse.builder()
                    .valid(true)
                    .userId(userByEmail.get().getId())
                    .bjname(userByEmail.get().getBjname())
                    .username(userByEmail.get().getName())
                    .build(), HttpStatus.OK);
        }
    }

    public ResponseEntity getInformation(String access) {
        Long userId = tokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        String username = user.getName();
        String picture = user.getPicture();
        String email = user.getEmail();
        int notices = noticeRepository.findAllByToUserOrderByModifiedDateTimeDesc(user).get().size();
        return new ResponseEntity<>(Map.of("username", username, "imageUrl", picture, "email", email, "isFirst", user.isFirst(), "notices" , notices), HttpStatus.OK);
    }
}
