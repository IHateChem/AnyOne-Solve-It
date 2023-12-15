package syleelsw.anyonesolveit.api.login;

import com.sun.net.httpserver.HttpsServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import syleelsw.anyonesolveit.aops.RefreshTokenValidation;
import syleelsw.anyonesolveit.api.login.dto.LoginBody;
import syleelsw.anyonesolveit.api.login.dto.UpdateTokenRequest;
import syleelsw.anyonesolveit.service.login.LoginService;

@RestController @RequestMapping("/api")
@RequiredArgsConstructor @Slf4j
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity postLogin(@RequestBody @Validated LoginBody loginBody, BindingResult bindingResult){
        return loginService.login(loginBody);
    }
    @GetMapping("/test")
    public ResponseEntity getTest(){
        return new ResponseEntity(loginService.test("syleelsw@snu.ac.kr"), HttpStatus.OK);
    }

    @PostMapping("/update/token") @RefreshTokenValidation
    public ResponseEntity updateToken(@RequestBody UpdateTokenRequest updateTokenDto){
        return new ResponseEntity(loginService.updateRefreshToken(updateTokenDto.getRefresh()), HttpStatus.OK);
    }


}
