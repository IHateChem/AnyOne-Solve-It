package syleelsw.anyonesolveit.service.study;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import syleelsw.anyonesolveit.api.study.dto.*;
import syleelsw.anyonesolveit.api.user.dto.NoticeDto;
import syleelsw.anyonesolveit.domain.join.UserStudyJoinRepository;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.study.Repository.ParticipationRepository;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.study.StudyProblemEntity;
import syleelsw.anyonesolveit.domain.study.enums.ParticipationStates;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.*;
import syleelsw.anyonesolveit.service.study.dto.StudyResponse;
import syleelsw.anyonesolveit.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest @Slf4j @Transactional
class StudyServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStudyJoinRepository userStudyJoinRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private StudyService studyService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ParticipationRepository participationRepository;
    @Value("${anyone.page}")
    private Integer page;
    public UserInfo mkUserInfo(Boolean isFirst, String bjName){
        return UserInfo.builder()
                .email("syleelsw@snu.ac.kr")
                .isFirst(isFirst)
                .bjname(bjName)
                .build();
    }
    private StudyDto studyBuilder(List<Long> members){
        return StudyDto.builder()
                .study_time("studyTime")
                .area(Locations.valueOf("서울"))
                .city("강남구")
                .description("알고리즘 스터디")
                .level(GoalTypes.valueOf("입문"))
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language(LanguageTypes.valueOf("PYTHON"))
                .members(members)
                .period("1주").build();
    }
    private StudyDto studyBuilder2(List<Long> members){ //설명에! 추가, level CONEPT으로 변경, freq 2번, Language JAVA
        return StudyDto.builder()
                .study_time("studyTime")
                .area(Locations.valueOf("서울")).
                description("알고리즘 스터디!")
                .city("강남구")
                .level(GoalTypes.valueOf("CONCEPT"))
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("2번").language(LanguageTypes.valueOf("JAVA"))
                .members(members)
                .period("1주").build();
    }
    private StudyDto studyBuilder2(List<Long> members, Locations locations, GoalTypes level, LanguageTypes language, String city){
        return StudyDto.builder()
                .study_time("studyTime")
                .area(locations).
                description("알고리즘 스터디")
                .city(city)
                .level(level)
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language(language)
                .members(members)
                .period("1주").build();
    }

    @DisplayName("스터디 문제 제안 테스트, 잘못된 문제가 들어오면 isExist = false, whoSolved = [], 푼사람이 있으면 푼사람들 전부 들어가야함. ")
    @Test
    void test(){
        //given

        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        String jwt2 = jwtTokenProvider.createJwt(savedUser2.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        Long studyId = response.getBody().getId();
        StudyResponse study = studyService.getStudy(jwt, studyId).getBody();
        //when
        ResponseEntity<SearchProblemDto> notExistResponse = studyService.getSearchProblem(studyId, 1000034214);
        ResponseEntity<SearchProblemDto> existResponse = studyService.getSearchProblem(studyId, 1000);

        //then
        assertThat(notExistResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notExistResponse.getBody().isExist()).isFalse();
        assertThat(notExistResponse.getBody().getWhoSolved()).hasSize(0);

        assertThat(existResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(existResponse.getBody().isExist()).isTrue();
        assertThat(existResponse.getBody().getWhoSolved()).hasSize(2);
    }
    @DisplayName("알림 테스트 타입 1 - 6")
    @Test
    void NoticeTest(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        UserInfo user3 = mkUserInfo(true, "syleelsj");
        UserInfo savedUser3 = userRepository.save(user3);

        List<Long> members = List.of(savedUser1.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        String jwt2 = jwtTokenProvider.createJwt(savedUser2.getId(),TokenType.ACCESS);
        String jwt3 = jwtTokenProvider.createJwt(savedUser3.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        Long studyId = Objects.requireNonNull(response.getBody()).getId();

        //when - then

        //type 5 누군가 스터디 신청 했습니다.
        ParticipationDTO succParticipationDTO = ParticipationDTO.builder().message("HI").studyId(studyId).build();
        String participationId = studyService.makeParticipation(jwt2, succParticipationDTO).getBody();
        NoticeDto type5 = Objects.requireNonNull(userService.getNotices(jwt).getBody()).getNotices().get(0);
        assertThat(type5.getNoticeType()).isEqualTo(5);
        assertThat(type5.getUsername()).isEqualTo(user2.getUsername());

        //type 1 내가 신청한 스터디 참가 승인 되었습니다.
        studyService.confirmParticipation(jwt, participationId, true);
        NoticeDto type1 = Objects.requireNonNull(userService.getNotices(jwt2).getBody()).getNotices().get(0);
        assertThat(type1.getNoticeType()).isEqualTo(1);

        //type 2 내가 신청한 스터디 참가 거절  되었습니다.
        String participationId2 = studyService.makeParticipation(jwt3, succParticipationDTO).getBody();
        studyService.confirmParticipation(jwt, participationId2, false);
        NoticeDto type2 = Objects.requireNonNull(userService.getNotices(jwt3).getBody()).getNotices().get(0);
        assertThat(type2.getNoticeType()).isEqualTo(2);


        //type 6 누군가 스터디를 나갔습니다.
        String participationId3 = studyService.makeParticipation(jwt3, succParticipationDTO).getBody();
        ResponseEntity responseEntity = studyService.confirmParticipation(jwt, participationId3, true);
        ResponseEntity responseEntity1 = studyService.studyOut(jwt3, studyId);
        NoticeDto type6 = Objects.requireNonNull(userService.getNotices(jwt).getBody()).getNotices().get(0);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(type6.getNoticeType()).isEqualTo(6);
        assertThat(type6.getUsername()).isEqualTo(user3.getUsername());

        //type 3 스터디 장이 변경되었을때 변경된사람(새로 스터디장 된사람에게)
        String participationId4 = studyService.makeParticipation(jwt3, succParticipationDTO).getBody();
        studyService.changeManger(jwt, studyId, user2.getId());
        List<NoticeDto> type3 = Objects.requireNonNull(userService.getNotices(jwt2).getBody()).getNotices();
        assertThat(type3).hasSize(7);
        assertThat(type3.get(0).getNoticeType()).isEqualTo(3);
        assertThat(type3.get(1).getNoticeType()).isEqualTo(5);

        //type 4 참가하고 있던 스터디가 삭제 되었습니다.
        ResponseEntity responseEntity2 = studyService.delStudy(jwt2, studyId);
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        NoticeDto type4_1 = Objects.requireNonNull(userService.getNotices(jwt).getBody()).getNotices().get(0);
        NoticeDto type4_2 = Objects.requireNonNull(userService.getNotices(jwt2).getBody()).getNotices().get(0);
        assertThat(type4_1.getNoticeType()).isEqualTo(4);
        assertThat(type4_2.getNoticeType()).isEqualTo(4);
    }
    @DisplayName("스터디장 변경을 테스트합니다. 권한없는 친구가 요청하면 400, 권한 있으면 200을 반환 합니다. 실제로 스터디장이 바뀌어야 합니다.")
    @Test
    void changeStudyManagerTest(){
        //given

        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        String jwt2 = jwtTokenProvider.createJwt(savedUser2.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        Long studyId = response.getBody().getId();
        //when
        ResponseEntity responseEntity = studyService.changeManger(jwt2, studyId, user2.getId());
        ResponseEntity responseEntity1 = studyService.changeManger(jwt, studyId, user2.getId());
        ResponseEntity<List<StudyResponse>> myStudySelf = studyService.getMyStudySelf(jwt2);
        //then

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(myStudySelf.getBody()).hasSize(1);
        assertThat(myStudySelf.getBody().get(0).getId()).isEqualTo(studyId);
    }
    @DisplayName("승인 테스트. 없는 참가신청아이디 이거 이미 승인 처리한 것이면 400오류, Repository에 요청의 confirm이 1이면 승인으로, 0이면 거절로 변경이 되어있어야한다.")
    @Test
    void 승인test(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        UserInfo user3 = mkUserInfo(true, "syleelsj");
        UserInfo savedUser3 = userRepository.save(user3);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        String jwt2 = jwtTokenProvider.createJwt(savedUser2.getId(),TokenType.ACCESS);
        String jwt3 = jwtTokenProvider.createJwt(savedUser3.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        Long studyId = response.getBody().getId();
        ParticipationDTO succParticipationDTO = ParticipationDTO.builder().message("HI").studyId(studyId).build();
        String succId = (String) studyService.makeParticipation(jwt2, succParticipationDTO).getBody();
        String succId2 = (String) studyService.makeParticipation(jwt3, succParticipationDTO).getBody();

        //when

        ResponseEntity noAuthResponse = studyService.confirmParticipation(jwt2, succId, true);
        ResponseEntity wrongIdResponse = studyService.confirmParticipation(jwt2, succId+"1234", true);
        ResponseEntity succResponse = studyService.confirmParticipation(jwt, succId, true);
        ResponseEntity alreadyDoneResponse = studyService.confirmParticipation(jwt, succId, true);
        ResponseEntity succResponse2 = studyService.confirmParticipation(jwt, succId2, false);

        Participation participation = participationRepository.findById(succId).get();
        Participation participation2 = participationRepository.findById(succId2).get();


        //then

        assertThat(noAuthResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(wrongIdResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(succResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(alreadyDoneResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(succResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(participation2.getState()).isEqualTo(ParticipationStates.거절);
        assertThat(participation.getState()).isEqualTo(ParticipationStates.승인);

    }
    @DisplayName("참가신청 취소 테스트 검색이 안되어야 한다. ")
    @Test
    void 참가취소test(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        Long studyId = response.getBody().getId();
        //when
        ParticipationDTO succParticipationDTO = ParticipationDTO.builder().message("HI").studyId(studyId).build();
        ResponseEntity<String> succResponse = studyService.makeParticipation(jwt, succParticipationDTO);
        ResponseEntity succDeleteResponse = studyService.deleteParticipation(jwt, studyId);

        //then
        assertThat(succResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(succDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String particpationId = succResponse.getBody();
        assertThat(participationRepository.findById(particpationId).isPresent()).isFalse();
    }
    @DisplayName("참가 신청 테스트. 존재하는 스터디가 아니면 400반환, 존재하는 스터디를 넣었을 경우에는 repository에 추가가 되어야 한다.")
    @Test
    void ParticipationTest(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        String jwt2 = jwtTokenProvider.createJwt(savedUser2.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        Long studyId = response.getBody().getId();
        //when
        ParticipationDTO failParticipationDTO = ParticipationDTO.builder().message("HI").studyId(studyId+100).build();
        ParticipationDTO succParticipationDTO = ParticipationDTO.builder().message("HI").studyId(studyId).build();
        ResponseEntity failResponse = studyService.makeParticipation(jwt, failParticipationDTO);
        ResponseEntity<String> authFailResponse = studyService.makeParticipation(jwt, succParticipationDTO);
        ResponseEntity<String> succResponse = studyService.makeParticipation(jwt2, succParticipationDTO);

        //then

        assertThat(failResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(succResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String particpationId = succResponse.getBody();
        assertThat(participationRepository.findById(particpationId).isPresent()).isTrue();
        assertThat(authFailResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @DisplayName("이문제 어때요 삭제를 테스트합니다. 스터디 아닌 사람이 요청시 401반환")
    @Test
    void DelHowAboutTestUnAuthorized(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        String jwt2 = jwtTokenProvider.createJwt(savedUser2.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        Long studyId = response.getBody().getId();

        ResponseEntity<ProblemResponse> valid_response1 = studyService.getStudyProblem(response.getBody().getId(), 1000);
        ResponseEntity<ProblemResponse> valid_response4 = studyService.getStudyProblem(response.getBody().getId(), 1001);
        ResponseEntity<ProblemResponse> valid_response5 = studyService.getStudyProblem(response.getBody().getId(), 1002);

        //when

        ResponseEntity responseEntity = studyService.deleteStudyProblem(jwt, studyId, 1000);
        ResponseEntity FailresponseEntity = studyService.deleteStudyProblem(jwt2, studyId, 1000);

        ResponseEntity<List<StudyProblemEntity>> suggestion = studyService.getSuggestion(studyId);



        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(FailresponseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(suggestion.getBody()).hasSize(2); //삭제 완료 여부 확인.
        assertThat(suggestion.getBody()).extracting("id").contains(studyId+"_" +1001, studyId+"_" +1002); //삭제 완료 여부 확인.
    }


    @DisplayName("이문제 어때요 삭제를 테스트합니다. 요청한적 없는 문제 삭제시 400반환 요청한적 있으면 200반환 ")
    @Test
    void DelHowAboutTestBadRequest(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        Long studyId = response.getBody().getId();

        ResponseEntity<ProblemResponse> valid_response1 = studyService.getStudyProblem(response.getBody().getId(), 1000);
        ResponseEntity<ProblemResponse> valid_response4 = studyService.getStudyProblem(response.getBody().getId(), 1001);
        ResponseEntity<ProblemResponse> valid_response5 = studyService.getStudyProblem(response.getBody().getId(), 1002);

        //when
        ResponseEntity successDeleteResponse = studyService.deleteStudyProblem(jwt, studyId, 1000);
        ResponseEntity failDeleteResponse = studyService.deleteStudyProblem(jwt, studyId, 1005);


        //then
        assertThat(successDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(failDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }



    @DisplayName("이문제 어때요 잘되는지(잘 db에 없는게 저장되는지, 기존에 있으면 잘 처리되는지) 확인 / 최신순으로 10개 나오는지 확인, deleteAll시 전부다 없어지는지 확인")
    @Test
    void suggestionTest(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        log.info("{}", response);
        ResponseEntity<ProblemResponse> valid_response1 = studyService.getStudyProblem(response.getBody().getId(), 1000);
        ResponseEntity<ProblemResponse> valid_response2 = studyService.getStudyProblem(response.getBody().getId(), 1000);
        ResponseEntity<ProblemResponse> valid_response3 = studyService.getStudyProblem(response.getBody().getId(), 1000);
        ResponseEntity<ProblemResponse> valid_response4 = studyService.getStudyProblem(response.getBody().getId(), 1001);
        ResponseEntity<ProblemResponse> valid_response5 = studyService.getStudyProblem(response.getBody().getId(), 1002);
        ResponseEntity<ProblemResponse> valid_response6 = studyService.getStudyProblem(response.getBody().getId(), 1003);
        ResponseEntity<ProblemResponse> valid_response7 = studyService.getStudyProblem(response.getBody().getId(), 1004);
        ResponseEntity<ProblemResponse> valid_response8 = studyService.getStudyProblem(response.getBody().getId(), 1005);
        ResponseEntity<ProblemResponse> valid_response9 = studyService.getStudyProblem(response.getBody().getId(), 1006);
        ResponseEntity<ProblemResponse> valid_response10 = studyService.getStudyProblem(response.getBody().getId(), 1007);
        ResponseEntity<ProblemResponse> valid_response11 = studyService.getStudyProblem(response.getBody().getId(), 1008);
        ResponseEntity<ProblemResponse> valid_response12 = studyService.getStudyProblem(response.getBody().getId(), 1009);

        //when
        ResponseEntity<List<StudyProblemResponse>> suggestion = studyService.getSuggestion(response.getBody().getId());

        //then
        assertThat(valid_response1.getBody().getProblemId()).isEqualTo(1000);
        assertThat(valid_response2.getBody().getProblemId()).isEqualTo(1000);
        assertThat(suggestion.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(suggestion.getBody().size()).isEqualTo(10);
        assertThat(suggestion.getBody().stream().map(t-> t.getProbNum()).collect(Collectors.toList())).isSorted();

        //when
        studyService.delAllSuggestion(jwt, response.getBody().getId());
        ResponseEntity<List<StudyProblemEntity>> suggestion0 = studyService.getSuggestion(response.getBody().getId());
        //then
        assertThat(suggestion0.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(suggestion0.getBody().size()).isEqualTo(0);

    }
    @DisplayName("지역이 잘 동작하는지 확인합니다. ")
    @Test
    void Locationtest(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = StudyDto.builder()
                .study_time("studyTime")
                .area(Locations.valueOf("서울"))
                .city("서초구")
                .description("알고리즘 스터디")
                .level(GoalTypes.valueOf("입문"))
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language(LanguageTypes.valueOf("PYTHON"))
                .members(members)
                .period("1주").build();


        StudyDto wrongStudyDto = StudyDto.builder()
                .study_time("studyTime")
                .area(Locations.valueOf("서울"))
                .city("귀염구")
                .description("알고리즘 스터디")
                .level(GoalTypes.valueOf("입문"))
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language(LanguageTypes.valueOf("PYTHON"))
                .members(members)
                .period("1주").build();

        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> studyResponse = studyService.createStudy(jwt, studyDto);
        ResponseEntity<StudyResponse> wrongStudyResponse = studyService.createStudy(jwt, wrongStudyDto);
        //when

        ResponseEntity<List<StudyResponse>> study1 = studyService.findStudy(1, 1, LanguageTypes.ALL, GoalTypes.ALL, "서울 강남구", null);
        ResponseEntity<List<StudyResponse>> study2 = studyService.findStudy(1, 1, LanguageTypes.ALL, GoalTypes.ALL, "서울 서초구", null);

        //then
        assertThat(studyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(wrongStudyResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(study1.getBody().size()).isEqualTo(0);
        assertThat(study2.getBody().size()).isEqualTo(1);
    }
    @DisplayName("Repository의 FindStudiesByMember을 테스트 합니다. 유저가 속한 스터디 모두가 담겨야 합니다.")
    @Test
    void FindStudyByMemberTest(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        String jwt2 = jwtTokenProvider.createJwt(savedUser2.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        StudyDto studyDto1 = studyBuilder2(List.of(savedUser1.getId()));
        ResponseEntity<StudyResponse> response2 = studyService.createStudy(jwt, studyDto1);
        //when
        ResponseEntity<List<StudyResponse>> myStudySelf = studyService.getMyStudy(jwt);
        ResponseEntity<List<StudyResponse>> myStudySelf2 = studyService.getMyStudy(jwt2);

        //then
        assertThat(myStudySelf.getBody()).extracting("id").containsExactlyInAnyOrder(response2.getBody().getId(), response.getBody().getId());
        assertThat(myStudySelf2.getBody()).extracting("id").containsExactlyInAnyOrder(response.getBody().getId());
    }

    @DisplayName("Repository의 FindAllByUser을 테스트 합니다. 유저가 만든 스터디 모두가 담겨야 합니다.")
    @Test
    void FindSelfMadeTest(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        StudyDto studyDto1 = studyBuilder2(List.of(savedUser1.getId()));
        ResponseEntity<StudyResponse> response2 = studyService.createStudy(jwt, studyDto1);
        //when
        ResponseEntity<List<StudyResponse>> myStudySelf = studyService.getMyStudySelf(jwt);

        //then
        assertThat(myStudySelf.getBody()).extracting("id").containsExactlyInAnyOrder(response2.getBody().getId(), response.getBody().getId());
    }
    @DisplayName("PUT시 바뀌어야 합니다.")
    @Test
    void testPutStudy(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        ResponseEntity<StudyResponse> response = studyService.createStudy(jwt, studyDto);
        Long StudyId = response.getBody().getId();
        StudyDto studyDto1 = studyBuilder2(List.of(savedUser1.getId()));

        studyService.putStudy(StudyId, studyDto1);
        //when
        StudyResponse studyResponse = studyService.getStudy(jwt, StudyId).getBody();
        assertThat(studyResponse.getDescription()).isEqualTo(studyDto1.getDescription());
        //then
    }
    @DisplayName("기본 검색시 시간 내림차순으로 리턴이 와야합니다.")
    @Test
    void testDesc(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        //given
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        ResponseEntity response = studyService.createStudy(jwt, studyDto);
        ResponseEntity response2 = studyService.createStudy(jwt, studyDto);
        ResponseEntity response3 = studyService.createStudy(jwt, studyDto);
        ResponseEntity response4 = studyService.createStudy(jwt, studyDto);
        ResponseEntity response5 = studyService.createStudy(jwt, studyDto);
        Study study = (Study) response.getBody();
        Long study_id = study.getId();
        //when
        ResponseEntity<List<StudyResponse>> study1 = studyService.findStudy(1, 1, LanguageTypes.ALL, GoalTypes.ALL, "ALL", null);
        //then
        assertThat(study1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(study1.getBody()).hasSize(page);
    }
    private boolean isSortedDescending(List<LocalDateTime> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).isBefore(list.get(i + 1))) {
                return false;
            }
        }
        return true;
    }
    @DisplayName("Java 설정후 검색시 자바만 매칭됩니다.")
    @Test
    void DefaultSearchJavaTest(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        //given
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder2(members, Locations.서울, GoalTypes.입문, LanguageTypes.PYTHON, "강남구");
        StudyDto studyDto2 = studyBuilder2(members, Locations.서울, GoalTypes.입문, LanguageTypes.JAVA, "강남구");
        StudyDto studyDto3 = studyBuilder2(members, Locations.서울, GoalTypes.입문, LanguageTypes.JAVA, "강남구");

        studyService.createStudy(jwt, studyDto);
        studyService.createStudy(jwt, studyDto2);
        studyService.createStudy(jwt, studyDto3);
        //when

        ResponseEntity<List<StudyResponse>> study1 = studyService.findStudy(1, 1, LanguageTypes.JAVA, GoalTypes.ALL, "ALL", null);
        //then
        assertThat(study1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(study1.getBody()).hasSize(2);
    }
    @DisplayName("page가 너무 크면 넘으면 빈배열이 나오거나 남은 만큼만 나온다. ")
    @Test
    void largePageDefaultSearchTest(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        //given
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        //when
        ResponseEntity<List<StudyResponse>> study1 = studyService.findStudy(1, 10, LanguageTypes.ALL, GoalTypes.ALL, "ALL", null);
        //then
        assertThat(study1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(study1.getBody()).hasSize(0);
    }
    @DisplayName("스터디 검색 orderBy1을 합니다. 가장 최근 스터디 N개가 반환 됩니다. 검색 파라미터는 전부 ALL로 해서 영향 없습니다. ")
    @Test
    void testDefaultSearchALL(){
        //given
        UserInfo user1 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser1 = userRepository.save(user1);
        //given
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        List<Long> members = List.of(savedUser1.getId(), savedUser2.getId());
        StudyDto studyDto = studyBuilder(members);
        String jwt = jwtTokenProvider.createJwt(savedUser1.getId(),TokenType.ACCESS);
        ResponseEntity response = studyService.createStudy(jwt, studyDto);
        ResponseEntity response2 = studyService.createStudy(jwt, studyDto);
        ResponseEntity response3 = studyService.createStudy(jwt, studyDto);
        ResponseEntity response4 = studyService.createStudy(jwt, studyDto);
        ResponseEntity response5 = studyService.createStudy(jwt, studyDto);
        Study study = (Study) response.getBody();
        Long study_id = study.getId();
        //when
        ResponseEntity<List<Study>> study1 = studyService.findStudy(1, 1, LanguageTypes.ALL, GoalTypes.ALL, "ALL", null);
        //then
        assertThat(study1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(study1.getBody()).hasSize(page);

    }
    //todo 문제를 제안하지 않았을때 테스트 케이스 만들기.
    @DisplayName("문제를 제안하면 5분간 제안을 가져올 수 있습니다. 문제를 제안하지 않으면..? ")
    @Test
    void getProblemSuggestionTest(){
        //given
        UserInfo user = mkUserInfo(true, "syleelsw");
        UserInfo savedUser = userRepository.save(user);
        //given
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        StudyDto studyDto = StudyDto.builder()
                .study_time("studyTime")
                .area(Locations.valueOf("서울")).
                description("알고리즘 스터디")
                .level(GoalTypes.valueOf("입문"))
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language(LanguageTypes.valueOf("PYTHON"))
                .members(List.of(savedUser2.getId(), savedUser.getId()))
                .period("1주").build();
        String jwt = jwtTokenProvider.createJwt(savedUser.getId(),TokenType.ACCESS);
        ResponseEntity response = studyService.createStudy(jwt, studyDto);
        Study study = (Study) response.getBody();
        Long study_id = study.getId();

        //when
        ResponseEntity<ProblemResponse> responseSuc = studyService.getStudyProblem(study_id, 1000);
        ResponseEntity<ProblemResponse> suggestion = studyService.getSuggestion(study_id);

        //then
        assertThat(suggestion.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProblemResponse body = suggestion.getBody();
        assertThat(body.getTitle()).isEqualTo("A+B");
        assertThat(body.getRank()).isNotNull();
        assertThat(body.getLink()).isEqualTo("https://www.acmicpc.net/problem/1000");
        assertThat(body.getTypes().size()).isGreaterThanOrEqualTo(1);
    }
    @DisplayName("문제를 제안합니다. 잘못된 문제를 찾으면 오류를 반환합니다.")
    @Test
    void getProblemTest(){
        //given
        UserInfo user = mkUserInfo(true, "syleelsw");
        UserInfo savedUser = userRepository.save(user);
        //given
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        StudyDto studyDto = StudyDto.builder()
                .study_time("studyTime")
                .area(Locations.valueOf("서울")).
                description("알고리즘 스터디")
                .level(GoalTypes.valueOf("입문"))
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language(LanguageTypes.valueOf("PYTHON"))
                .members(List.of(savedUser2.getId(), savedUser.getId()))
                .period("1주").build();
        String jwt = jwtTokenProvider.createJwt(savedUser.getId(),TokenType.ACCESS);
        ResponseEntity response = studyService.createStudy(jwt, studyDto);
        Study study = (Study) response.getBody();
        Long study_id = study.getId();

        //when
        ResponseEntity responseFail = studyService.getStudyProblem(study_id, 0);
        ResponseEntity<ProblemResponse> responseSuc = studyService.getStudyProblem(study_id, 1000);

        //then
        assertThat(responseFail.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseSuc.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProblemResponse body = responseSuc.getBody();
        //e답변 화인
        assertThat(body.getTitle()).isEqualTo("A+B");
        assertThat(body.getRank()).isNotNull();
        assertThat(body.getLink()).isEqualTo("https://www.acmicpc.net/problem/1000");
        assertThat(body.getTypes().size()).isGreaterThanOrEqualTo(1);
    }
    @DisplayName("삭제를 합니다. 삭제시 다른 아이디로 요청하면 Forbidden이 뜹니다. 같은 아이디면 삭제에 성공합니다. ")
    @Test
    void studyDeleteTest(){
        //given
        UserInfo user2 = mkUserInfo(true, "syleelsw");
        UserInfo savedUser = userRepository.save(user2);
        //given
        UserInfo user = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user);
        StudyDto studyDto = StudyDto.builder()
                .study_time("studyTime")
                .area(Locations.valueOf("서울")).
                description("알고리즘 스터디")
                .level(GoalTypes.valueOf("입문"))
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language(LanguageTypes.valueOf("PYTHON"))
                .members(List.of(savedUser2.getId(), savedUser.getId()))
                .period("1주").build();
        //when
        String jwt = jwtTokenProvider.createJwt(savedUser.getId(),TokenType.ACCESS);
        String jwt1 = jwtTokenProvider.createJwt(1l, TokenType.ACCESS);
        ResponseEntity response = studyService.createStudy(jwt, studyDto);


        Study study = (Study) response.getBody();
        log.info("Status: ", response);
        Long study_id = study.getId();
        ResponseEntity responseEntity = studyService.delStudy(jwt1, study_id);
        //포비든 뜨는지 확인
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        //성공하는 경우
        ResponseEntity responseEntity2 = studyService.delStudy(jwt, study_id);
        //포비든 뜨는지 확인
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        //실제로 삭제 되었는지 확인.
        assertThat(studyRepository.findById(study_id).isPresent()).isFalse();
        //then
    }


    @DisplayName("스터디를 만들면 조회가 가능해야한다.")
    @Test
    void getStudytest(){
        //given

        //when

        //then
    }
    @DisplayName("스터디를 만듭니다. Study와 UserStudyJoin에 저장이 됩니다. getStudy가 가능해집니다.")
    @Test
    void createStudyTest(){
        String jwt = jwtTokenProvider.createJwt(1l, TokenType.ACCESS);
        //given
        UserInfo userInfo = mkUserInfo(true, "syleelsw");
        UserInfo savedUser = userRepository.save(userInfo);
        //given
        UserInfo user2 = mkUserInfo(true, "igy2840");
        UserInfo savedUser2 = userRepository.save(user2);
        StudyDto studyDto = StudyDto.builder()
                .study_time("studyTime")
                .area(Locations.서울).
                description("알고리즘 스터디")
                .level(GoalTypes.valueOf("입문"))
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language(LanguageTypes.PYTHON)
                .members(List.of(savedUser2.getId(), savedUser.getId()))
                .period("1주").build();

        //when
        ResponseEntity response = studyService.createStudy(jwt, studyDto);
        Study study = (Study) response.getBody();
        Long study_id = study.getId();
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserInfo userInfo1 = userRepository.findById(1l).get();
        Study study1 = studyRepository.findById(study_id).get();
        assertThat(study1.getUser().getId()).isEqualTo(1l);
        log.info(study1.toString());

        ResponseEntity<StudyResponse> response1 = studyService.getStudy(jwt, study_id);
        assertThat(response1.getBody().getId()).isEqualTo(study_id);
        //UserStudyJoin studyJoin = study1.getStudyJoin();
        //assertThat(studyJoin.getUser().getId()).isEqualTo(1l);
        //assertThat(study1.getStudyJoin().getStudy().getId()).isEqualTo(study_id);


    }

}