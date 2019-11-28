package com.ymt.spring.formework.beans;

/**
 * Created by @author yangmingtian on 2019/11/26
 */
public class GPBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public GPBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return this.wrappedInstance;
    }

    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }

}
