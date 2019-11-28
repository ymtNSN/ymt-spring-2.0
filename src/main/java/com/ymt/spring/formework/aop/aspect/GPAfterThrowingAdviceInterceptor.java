package com.ymt.spring.formework.aop.aspect;

import com.ymt.spring.formework.aop.intercept.GPMethodInterceptor;
import com.ymt.spring.formework.aop.intercept.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * Created by @author yangmingtian on 2019/11/28
 */
public class GPAfterThrowingAdviceInterceptor extends GPAbstractAspectAdvice implements GPAdvice, GPMethodInterceptor {

    private String throwingName;

    public GPAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        try {
            Object res = mi.proceed();
            return res;
        } catch (Throwable e) {
            invokeAdviceMethod(mi, null, e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName) {
        this.throwingName = throwName;
    }

}
