package syleelsw.anyonesolveit.service.study;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;
import syleelsw.anyonesolveit.api.study.dto.ProblemResponse;
import syleelsw.anyonesolveit.api.study.dto.SolvedacItem;
import syleelsw.anyonesolveit.api.study.dto.StudyDto;
import syleelsw.anyonesolveit.domain.join.UserStudyJoin;
import syleelsw.anyonesolveit.domain.join.UserStudyJoinRepository;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.etc.TokenType;
import syleelsw.anyonesolveit.service.user.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    private JwtTokenProvider jwtTokenProvider;
    public UserInfo mkUserInfo(Boolean isFirst, String bjName){
        return UserInfo.builder()
                .email("syleelsw@snu.ac.kr")
                .isFirst(isFirst)
                .bjname(bjName)
                .build();
    }
    //todo 문제를 제안하지 않았을때 테스트 케이스 만들기.
    @DisplayName("문제를 제안하면 5분간 제안을 가져올 수 있습니다. 문제를 제안하지 않으면..? ")
    @Test
    void getProblemSuggestionTest(){
        //given
        UserInfo user = mkUserInfo(true, "syleelsw");
        UserInfo savedUser = userRepository.save(user);
        StudyDto studyDto = StudyDto.builder()
                .study_time("studyTime")
                .area("서울").
                description("알고리즘 스터디")
                .level("입문")
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language("python")
                .members(List.of("syleelsw", "igy2840"))
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
        StudyDto studyDto = StudyDto.builder()
                .study_time("studyTime")
                .area("서울").
                description("알고리즘 스터디")
                .level("입문")
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language("python")
                .members(List.of("syleelsw", "igy2840"))
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
        StudyDto studyDto = StudyDto.builder()
                .study_time("studyTime")
                .area("서울").
                description("알고리즘 스터디")
                .level("입문")
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language("python")
                .members(List.of("syleelsw", "igy2840"))
                .period("1주").build();
        //when
        String jwt = jwtTokenProvider.createJwt(savedUser.getId(),TokenType.ACCESS);
        String jwt1 = jwtTokenProvider.createJwt(1l, TokenType.ACCESS);
        ResponseEntity response = studyService.createStudy(jwt, studyDto);
        Study study = (Study) response.getBody();
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
        StudyDto studyDto = StudyDto.builder()
                .study_time("studyTime")
                .area("서울").
                description("알고리즘 스터디")
                .level("입문")
                .title("파이썬 알고리즘 스터디")
                .meeting_type("대면")
                .frequency("1번").language("python")
                .members(List.of("syleelsw", "igy2840"))
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

        ResponseEntity<Study> response1 = studyService.getStudy(study_id);
        assertThat(response1.getBody().getId()).isEqualTo(study_id);
        //UserStudyJoin studyJoin = study1.getStudyJoin();
        //assertThat(studyJoin.getUser().getId()).isEqualTo(1l);
        //assertThat(study1.getStudyJoin().getStudy().getId()).isEqualTo(study_id);


    }

}