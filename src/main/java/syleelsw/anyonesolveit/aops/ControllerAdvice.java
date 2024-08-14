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
import syleelsw.anyonesolveit.domain.login.RefreshCnt;
import syleelsw.anyonesolveit.domain.login.RefreshShort;
import syleelsw.anyonesolveit.domain.login.Respository.RefreshCntRedisRepository;
import syleelsw.anyonesolveit.domain.login.Respository.RefreshRedisRepository;
import syleelsw.anyonesolveit.domain.login.Respository.RefreshShortRedisRepository;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;

import java.util.Optional;

@Aspect @RequiredArgsConstructor
@Component @Slf4j
public class ControllerAdvice {
    private final JwtTokenProvider jwtTokenProvider;
    private final StudyRepository studyRepository;
    private final RefreshShortRedisRepository refreshShortRedisRepository;
    private final RefreshRedisRepository refreshRedisRepository;
    private final RefreshCntRedisRepository refreshCntRedisRepository;
    @Around("syleelsw.anyonesolveit.aops.Pointcuts.allService() &&  args(loginBody, bindingResult)")
    public ResponseEntity validator(ProceedingJoinPoint joinPoint, LoginBody loginBody,  BindingResult bindingResult) throws Throwable {
        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return (ResponseEntity) joinPoint.proceed();
    }

    @Around("@annotation(RefreshTokenValidation) && args(updateTokenDto)")
    public Object doFilterRefresh(ProceedingJoinPoint joinPoint, UpdateTokenRequest updateTokenDto) throws Throwable {
        String jwt = updateTokenDto.getRefresh();
        Optional<RefreshCnt> byId = refreshCntRedisRepository.findById(jwt);
        RefreshCnt refreshCnt;
        if(byId.isPresent()){
            refreshCnt = byId.get();
        }else{
            refreshCnt = new RefreshCnt(jwt, 0);
        }
        refreshCnt.addCnt();
        if(refreshCnt.getCnt()>5){
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }
        refreshCntRedisRepository.save(refreshCnt);
        if(jwtTokenProvider.validateToken(jwt)){
            return joinPoint.proceed();
        }else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Around("syleelsw.anyonesolveit.aops.Pointcuts.allApi() && args(Access, ..) && !@annotation(IgnoreValidation )")
    public ResponseEntity jwtValidation(ProceedingJoinPoint joinPoint, String Access) throws Throwable {
        if (jwtTokenProvider.validateToken(Access) && isLoginUser(jwtTokenProvider.getUserId(Access))) {
            return (ResponseEntity) joinPoint.proceed();
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isLoginUser(Long id) {
        return refreshRedisRepository.findById(id).isPresent();
    }

    @Around("syleelsw.anyonesolveit.aops.Pointcuts.allApi() && args(Access, id, ..) && !@annotation(IgnoreValidation) && !@annotation(Notices)")
    public ResponseEntity idValidation(ProceedingJoinPoint joinPoint, String Access, Long id) throws Throwable {
        if(studyRepository.findById(id).isPresent()){
            return (ResponseEntity) joinPoint.proceed();
        }else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Around("syleelsw.anyonesolveit.aops.Pointcuts.allApi() && args(id, ..)  && !@annotation(IgnoreValidation )")
    public ResponseEntity idValidation(ProceedingJoinPoint joinPoint, Long id) throws Throwable {
        if(studyRepository.findById(id).isPresent()){
            return (ResponseEntity) joinPoint.proceed();
        }else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
