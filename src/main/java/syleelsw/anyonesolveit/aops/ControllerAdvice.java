package syleelsw.anyonesolveit.aops;

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

@Aspect
@Component @Slf4j
public class ControllerAdvice {
    @Around("syleelsw.anyonesolveit.aops.Pointcuts.allService() &&  args(loginBody, bindingResult)")
    public ResponseEntity validator(ProceedingJoinPoint joinPoint, LoginBody loginBody,  BindingResult bindingResult) throws Throwable {
        log.info("validation AOP");
        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return (ResponseEntity) joinPoint.proceed();
    }

}
