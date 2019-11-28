package com.ymt.spring.formework.aop.intercept;

public interface GPMethodInterceptor {

    Object invoke(GPMethodInvocation invocation) throws Throwable;
}
