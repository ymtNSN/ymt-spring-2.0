package com.ymt.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * Created by @author yangmingtian on 2019/11/27
 */
public interface GPJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key,Object value);

    Object getUserAttribute(String key);
}
