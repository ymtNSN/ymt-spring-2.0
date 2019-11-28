package com.ymt.spring;

import com.ymt.spring.demo.action.MyAction;
import com.ymt.spring.formework.context.GPApplicationContext;

/**
 * Created by @author yangmingtian on 2019/11/27
 */
public class Test {
    public static void main(String[] args) {
        GPApplicationContext context = new GPApplicationContext("classpath:application.properties");
        try {
            Object bean = context.getBean(MyAction.class);
            System.out.println(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
