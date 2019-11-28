package com.ymt.spring.formework.aop;

import com.ymt.spring.formework.aop.intercept.GPMethodInvocation;
import com.ymt.spring.formework.aop.support.GPAdvisedSAupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by @author yangmingtian on 2019/11/27
 */
public class GPJdkDynamicProxy implements GPAopProxy, InvocationHandler {

    private GPAdvisedSAupport advised;

    public GPJdkDynamicProxy(GPAdvisedSAupport advised){
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, this.advised.getTargetClass());
        GPMethodInvocation invocation = new GPMethodInvocation(proxy, this.advised.getTarget(), method, args, this.advised.getTargetClass(), interceptorsAndDynamicMethodMatchers);
        Object res = invocation.proceed();
        System.out.println("动态代理返回值："+res);
        return res;
    }
}
