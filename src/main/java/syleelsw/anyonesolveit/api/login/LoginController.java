package syleelsw.anyonesolveit.api.login;

import com.sun.net.httpserver.HttpsServer;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import syleelsw.anyonesolveit.aops.RefreshTokenValidation;
import syleelsw.anyonesolveit.api.login.dto.LoginBody;
import syleelsw.anyonesolveit.api.login.dto.UpdateTokenRequest;
import syleelsw.anyonesolveit.service.login.LoginService;
import syleelsw.anyonesolveit.service.user.UserService;

import java.util.Map;

@RestController @RequestMapping("/api")
@RequiredArgsConstructor @Slf4j
public class LoginController {
    private final LoginService loginService;
    private final UserService userService;
    @Value("${anyone.log key}")
    private String key;
    @GetMapping("/tests")
    public ResponseEntity tests(){
        return new ResponseEntity(userService.getSolvedProblem("kepler186f"), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity postLogin(@RequestBody @Validated LoginBody loginBody){
        return loginService.login(loginBody);
    }

    @GetMapping("/login/github")
    public ResponseEntity getGitHubLogin(@RequestParam String code){
        return loginService.gitHubLogin(code);
    }

    @GetMapping("/login/kakao/tst")
    public ResponseEntity tkakaoLogin(){
        return loginService.kakaoLogin();
    }

    @PostMapping("/login/kakao")
    public ResponseEntity kakaoLogin(){
        return loginService.kakaoLogin();
    }
    @GetMapping("/login/kakao")
    public ResponseEntity getkakaoLogin(@RequestParam String code){
        return loginService.getkakaoLogin(code);
    }


    @GetMapping("/test")
    public ResponseEntity getTest(@RequestParam @Nullable String _key){
        if(_key == null || !_key.equals(key)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity(loginService.test("syleelsw@snu.ac.kr"), HttpStatus.OK);
    }

    @PostMapping("/update/token") @RefreshTokenValidation
    public ResponseEntity updateToken(@RequestBody UpdateTokenRequest updateTokenDto){
        return loginService.updateRefreshToken(updateTokenDto.getRefresh());
    }


}
