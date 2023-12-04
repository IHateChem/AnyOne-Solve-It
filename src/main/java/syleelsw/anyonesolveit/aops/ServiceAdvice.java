package syleelsw.anyonesolveit.aops;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect @Component @Slf4j
public class ServiceAdvice {
    @Around("syleelsw.anyonesolveit.aops.Pointcuts.allService() && @annotation(timer)")
    public Object doTimer(ProceedingJoinPoint joinPoint, Timer timer) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // joinPoint.proceed() -> 실질적인 method 실행
        Object result = joinPoint.proceed();

        // 실행 후
        stopWatch.stop();
        // 총 걸린 시간 (초단위)
        log.info("{} total time : {}",timer.value(), + stopWatch.getTotalTimeSeconds());
        return result;

    }
}
