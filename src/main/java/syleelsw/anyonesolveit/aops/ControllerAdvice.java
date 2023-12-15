package syleelsw.anyonesolveit.aops;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import syleelsw.anyonesolveit.api.login.dto.LoginBody;
import syleelsw.anyonesolveit.api.login.dto.UpdateTokenRequest;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;

@Aspect @RequiredArgsConstructor
@Component @Slf4j
public class ControllerAdvice {
    private final JwtTokenProvider jwtTokenProvider;
    @Around("syleelsw.anyonesolveit.aops.Pointcuts.allService() &&  args(loginBody, bindingResult)")
    public ResponseEntity validator(ProceedingJoinPoint joinPoint, LoginBody loginBody,  BindingResult bindingResult) throws Throwable {
        log.info("validation AOP");
        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return (ResponseEntity) joinPoint.proceed();
    }

    @Around("@annotation(RefreshTokenValidation) && args(updateTokenDto)")
    public Object doFilterRefresh(ProceedingJoinPoint joinPoint, UpdateTokenRequest updateTokenDto) throws Throwable {
        String jwt = updateTokenDto.getRefresh();
        if(jwtTokenProvider.validateToken(jwt)){
            return joinPoint.proceed();
        }else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Around("syleelsw.anyonesolveit.aops.Pointcuts.allApi() && args(Access, ..)")
    public ResponseEntity jwtValidation(ProceedingJoinPoint joinPoint, String Access) throws Throwable {
        if (jwtTokenProvider.validateToken(Access)) {
            return (ResponseEntity) joinPoint.proceed();
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
