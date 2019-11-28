package com.ymt.spring.formework.context;

import com.ymt.spring.formework.annotation.GPAutowired;
import com.ymt.spring.formework.annotation.GPController;
import com.ymt.spring.formework.annotation.GPService;
import com.ymt.spring.formework.aop.GPAopProxy;
import com.ymt.spring.formework.aop.GPCglibAopProxy;
import com.ymt.spring.formework.aop.GPJdkDynamicProxy;
import com.ymt.spring.formework.aop.config.GPAopConfig;
import com.ymt.spring.formework.aop.support.GPAdvisedSAupport;
import com.ymt.spring.formework.beans.GPBeanWrapper;
import com.ymt.spring.formework.beans.config.GPBeanDefinition;
import com.ymt.spring.formework.beans.config.GPBeanPostProcessor;
import com.ymt.spring.formework.beans.support.GPBeanDefinitionReader;
import com.ymt.spring.formework.beans.support.GPDefaultListableBeanFactory;
import com.ymt.spring.formework.core.GPBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by @author yangmingtian on 2019/11/26
 */
public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {

    private String[] configLocations;
    private GPBeanDefinitionReader reader;

    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private Map<String, GPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    public GPApplicationContext() {
    }

    public GPApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        // 1.定位，配置文件
        reader = new GPBeanDefinitionReader(this.configLocations);

        // 2.加载配置文件，扫描相关的类，把他们封装成BeanDefinition
        List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        // 3.注册，把配置信息放到容器里面
        doRegistryBeanDefinition(beanDefinitions);

        //4.把不是延时加载的类，提前初始化
        doAutowired();
    }

    private void doAutowired() {
        for (Map.Entry<String, GPBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegistryBeanDefinition(List<GPBeanDefinition> beanDefinitions) throws Exception {
        for (GPBeanDefinition beanDefinition : beanDefinitions) {
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {

        GPBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        GPBeanPostProcessor postProcessor = new GPBeanPostProcessor();
        postProcessor.postProcessBeforeInitialization(instance, beanName);

        instance = instantiateBean(beanName, beanDefinition);

        // 包装
        GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);

        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        postProcessor.postProcessAfterInitialization(instance, beanName);

        // 注入
        populateBean(beanName, new GPBeanDefinition(), beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, GPBeanDefinition gpBeanDefinition, GPBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        if (!(clazz.isAnnotationPresent(GPController.class) || clazz.isAnnotationPresent(GPService.class))) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(GPAutowired.class)) {
                continue;
            }
            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);


            try {
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    continue;
                }
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(String beanName, GPBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();

        Object instance = null;
        try {
            if (this.singletonObjects.containsKey(className)) {
                instance = this.singletonObjects.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                GPAdvisedSAupport advisedSAupport = instantionAopConfig(beanDefinition);
                advisedSAupport.setTargetClass(clazz);
                advisedSAupport.setTarget(instance);

                if (advisedSAupport.pointCutMatch()) {
                    instance = createProxy(advisedSAupport).getProxy();
                }

                this.singletonObjects.put(className, instance);
                this.singletonObjects.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private GPAopProxy createProxy(GPAdvisedSAupport advisedSAupport) {
        Class<?> targetClass = advisedSAupport.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new GPJdkDynamicProxy(advisedSAupport);
        }
        return new GPCglibAopProxy(advisedSAupport);
    }

    private GPAdvisedSAupport instantionAopConfig(GPBeanDefinition beanDefinition) {
        GPAopConfig config = new GPAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));

        return new GPAdvisedSAupport(config);
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(GPBeanDefinitionReader.toLowerFirstCase(beanClass.getSimpleName()));
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
