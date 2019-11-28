package com.ymt.spring.formework.webmvc.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created by @author yangmingtian on 2019/11/27
 */
@Data
public class GPHandlerMapping {

    private Object controller;

    private Method method;

    private Pattern pattern;

    public GPHandlerMapping(Pattern pattern,Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
