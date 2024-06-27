package syleelsw.anyonesolveit.service.login;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import syleelsw.anyonesolveit.api.login.Provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class ㅌLoginServiceTest {
    @Autowired
    private LoginService loginService;

    @Test
    @DisplayName("로그인시 Provider가 다른데 같은 이메일이면 400오류")
    void findUserAndJoin() {
        ResponseEntity userAndJoin = loginService.findUserAndJoin("test@test.com", "test", Provider.test, "img1");
        ResponseEntity userAndJoin2 = loginService.findUserAndJoin("test@test.com", "naver", Provider.NAVER, "img1");

        assertThat(userAndJoin2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(userAndJoin.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}