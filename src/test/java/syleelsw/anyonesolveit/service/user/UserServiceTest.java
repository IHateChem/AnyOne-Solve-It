package syleelsw.anyonesolveit.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import syleelsw.anyonesolveit.api.user.dto.UserProfileDto;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;
import syleelsw.anyonesolveit.etc.TokenType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider provider;
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