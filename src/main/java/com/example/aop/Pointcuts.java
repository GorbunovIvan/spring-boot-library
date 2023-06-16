package com.example.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.example.controller.*.*(..))")
    public void allEndpoints() {}

    @Pointcut("execution(* com.example.service.*.*(..))")
    public void allServiceMethods() {}
}
