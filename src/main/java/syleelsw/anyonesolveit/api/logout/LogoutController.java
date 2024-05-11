package syleelsw.anyonesolveit.api.logout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import syleelsw.anyonesolveit.aops.IgnoreValidation;
import syleelsw.anyonesolveit.service.login.LoginService;

@RestController @RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class LogoutController {
    private final LoginService loginService;
    @GetMapping("/logout")
    public ResponseEntity logout(@RequestHeader String Access){
        return loginService.logout(Access);
    }
    @GetMapping("/withdraw")
    public ResponseEntity withdraw(@RequestHeader String Access){
        return loginService.withdraw(Access);
    }
}
