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
    @DisplayName("스터디를 만듭니다. Study와 UserStudyJoin에 저장이 됩니다. ")
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
        //UserStudyJoin studyJoin = study1.getStudyJoin();
        //assertThat(studyJoin.getUser().getId()).isEqualTo(1l);
        //assertThat(study1.getStudyJoin().getStudy().getId()).isEqualTo(study_id);


    }

}