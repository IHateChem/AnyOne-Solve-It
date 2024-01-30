package syleelsw.anyonesolveit.aops;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


public class Pointcuts {
    @Pointcut("execution(* syleelsw.anyonesolveit.api..*(..)) && !execution(* syleelsw.anyonesolveit.api.login..*(..))")
    public void allApi(){}
    @Pointcut("execution(* syleelsw.anyonesolveit.service..*(..))")
    public void allService(){}

}
