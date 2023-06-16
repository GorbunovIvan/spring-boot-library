package com.example.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class CustomAspect {

    @Around("Pointcuts.allEndpoints()")
    public Object aroundEndpointAdvice(ProceedingJoinPoint joinPoint) {

        var signature = (MethodSignature) joinPoint.getSignature();

        String message = String.format("controller '%s', endpoint-method '%s'",
                signature.getMethod().getDeclaringClass().getName(),
                signature.getName());

        Object result;

        try {
            log.info(message + " is invoked");
            result = joinPoint.proceed();
            log.info(message + " is completed");
        } catch (Throwable e) {
            log.error(message + " has thrown an error");
            throw new RuntimeException(e);
        }

        return result;
    }

    @Around("Pointcuts.allServiceMethods()")
    public Object aroundServiceMethodAdvice(ProceedingJoinPoint joinPoint) {

        var signature = (MethodSignature) joinPoint.getSignature();

        String message = String.format("service '%s', method '%s'",
                signature.getMethod().getDeclaringClass().getName(),
                signature.getName());

        Object result;

        try {
            log.info(message + " is invoked");
            result = joinPoint.proceed();
            log.info(message + " is completed");
        } catch (Throwable e) {
            log.error(message + " has thrown an error");
            throw new RuntimeException(e);
        }

        return result;
    }
}
