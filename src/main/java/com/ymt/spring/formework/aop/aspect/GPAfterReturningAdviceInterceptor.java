package com.ymt.spring.formework.aop.aspect;

import com.ymt.spring.formework.aop.intercept.GPMethodInterceptor;
import com.ymt.spring.formework.aop.intercept.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * Created by @author yangmingtian on 2019/11/28
 */
public class GPAfterReturningAdviceInterceptor extends GPAbstractAspectAdvice implements GPAdvice, GPMethodInterceptor {

    private GPJoinPoint joinPoint;

    public GPAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, retVal, null);
    }
}
