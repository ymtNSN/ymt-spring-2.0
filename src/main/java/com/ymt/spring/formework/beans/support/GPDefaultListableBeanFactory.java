package com.ymt.spring.formework.beans.support;

import com.ymt.spring.formework.beans.config.GPBeanDefinition;
import com.ymt.spring.formework.context.support.GPAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by @author yangmingtian on 2019/11/26
 */
public class GPDefaultListableBeanFactory extends GPAbstractApplicationContext {

    protected final Map<String, GPBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
}
