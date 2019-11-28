package com.ymt.spring.formework.aop.config;

import lombok.Data;

/**
 * Created by @author yangmingtian on 2019/11/27
 */
@Data
public class GPAopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
