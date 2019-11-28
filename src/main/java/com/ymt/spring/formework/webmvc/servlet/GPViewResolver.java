package com.ymt.spring.formework.webmvc.servlet;

import java.io.File;

/**
 * Created by @author yangmingtian on 2019/11/27
 */
public class GPViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;

    public GPViewResolver(String templateRoot) {
        String path = this.getClass().getClassLoader().getResource(templateRoot).getPath();
        templateRootDir = new File(path);
    }

    public GPView resolveViewName(String viewName, Object o) {
        if (viewName == null || "".equals(viewName.trim())){
            return null;
        }

        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)?viewName:(viewName+DEFAULT_TEMPLATE_SUFFIX);

        File templateFile = new File(templateRootDir.getPath() + "/" + viewName.replaceAll("/+", "/"));
        return new GPView(templateFile);

    }
}
