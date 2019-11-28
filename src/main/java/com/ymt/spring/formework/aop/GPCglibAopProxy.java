package com.ymt.spring.formework.aop;

import com.ymt.spring.formework.aop.support.GPAdvisedSAupport;

/**
 * Created by @author yangmingtian on 2019/11/27
 */
public class GPCglibAopProxy implements GPAopProxy {
    public GPCglibAopProxy(GPAdvisedSAupport advisedSAupport) {


    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
