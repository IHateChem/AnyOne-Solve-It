package syleelsw.anyonesolveit.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import syleelsw.anyonesolveit.api.login.Provider;
import syleelsw.anyonesolveit.api.study.dto.ParticipationDTO;
import syleelsw.anyonesolveit.api.study.dto.StudyDto;
import syleelsw.anyonesolveit.api.user.dto.ParticipationResponse;
import syleelsw.anyonesolveit.api.user.dto.UserProfileDto;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.*;
import syleelsw.anyonesolveit.service.study.StudyService;
import syleelsw.anyonesolveit.service.study.dto.StudyResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider provider;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudyService studyService;
    UserProfileDto userProfileDto(String bjName){
        return UserProfileDto.builder()
                .bjname(bjName)
                .area(Locations.ALL)
                .email("s@a.com")
                .isFirst(true)
                .languages(List.of(LanguageTypes.JAVA))
                .prefer_type("대면")
                .build();
    }
    public UserInfo mkUserInfo(Boolean isFirst, String bjName){
        return UserInfo.builder()
                .email("syleelsw@snu.ac.kr")
                .isFirst(isFirst)
                .bjname(bjName)
                .provider(Provider.test)
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

    @DisplayName("내가 만든 스터디에 지원한 사람들 확인테스트 ")
    @Test
    void 스터디지원자test(){
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
        ResponseEntity<StudyResponse> response2 = studyService.createStudy(jwt, studyDto);
        Long studyId = response.getBody().getId();
        Long studyId2 = response2.getBody().getId();

        //when
        ResponseEntity responseEntity = studyService.makeParticipation(jwt2, ParticipationDTO.builder().studyId(studyId).message("HI").build());
        ResponseEntity responseEntity2 = studyService.makeParticipation(jwt2, ParticipationDTO.builder().studyId(studyId2).message("HI").build());

        ResponseEntity<List<ParticipationResponse>> participationResponse = userService.getMyParticipation(jwt);
        ResponseEntity<List<ParticipationResponse>> participationResponse2 = userService.getMyParticipation(jwt2);

        //then
        Long userId = savedUser2.getId();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(participationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(participationResponse.getBody()).hasSize(2);
        assertThat(participationResponse.getBody()).extracting("participationId").contains(userId+"_"+studyId, userId+"_"+studyId2);
        assertThat(participationResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(participationResponse2.getBody()).hasSize(0);
    }

    @DisplayName("참가 신청 확인. 2번의 참가신청을 했으면 2개의 참가신청이 있음을 뱉는다.")
    @Test
    void 참가신청test(){
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
        ResponseEntity<StudyResponse> response2 = studyService.createStudy(jwt, studyDto);
        Long studyId = response.getBody().getId();
        Long studyId2 = response2.getBody().getId();

        //when
        ResponseEntity responseEntity = studyService.makeParticipation(jwt2, ParticipationDTO.builder().studyId(studyId).message("HI").build());
        ResponseEntity responseEntity2 = studyService.makeParticipation(jwt2, ParticipationDTO.builder().studyId(studyId2).message("HI").build());
        ResponseEntity<List<ParticipationResponse>> participationResponse = userService.getMyApply(jwt2);

        //then
        Long userId = savedUser2.getId();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(participationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(participationResponse.getBody()).hasSize(2);
        assertThat(participationResponse.getBody()).extracting("participationId").contains(userId+"_"+studyId, userId+"_"+studyId2);

    }


    @DisplayName("프로파일 만들때 백준아이디가 유효하지 않으면 400을 뱉는다. ")
    @Test
    void setProfileTest(){
        String jwt = provider.createJwt(1L, TokenType.ACCESS);
        //given
        UserProfileDto inValidUser =userProfileDto("syleelsw123");
        UserProfileDto validUser = userProfileDto("syleelsw");
        //when

        ResponseEntity responseEntity = userService.setProfile(jwt, inValidUser);
        ResponseEntity responseEntity1 = userService.setProfile(jwt, validUser);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);

        //then
    }

}