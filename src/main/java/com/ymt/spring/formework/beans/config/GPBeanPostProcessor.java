package com.ymt.spring.formework.beans.config;

/**
 * Created by @author yangmingtian on 2019/11/26
 */
public class GPBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
