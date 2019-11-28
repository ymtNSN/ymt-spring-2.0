package com.ymt.spring.formework.core;

/**
 * Created by @author yangmingtian on 2019/11/26
 */
public interface GPBeanFactory {

    Object getBean(String beanName) throws Exception;

    public Object getBean(Class<?> beanClass) throws Exception;
}
