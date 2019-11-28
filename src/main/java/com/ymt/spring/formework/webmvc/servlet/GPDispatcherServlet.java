package com.ymt.spring.formework.webmvc.servlet;

import com.ymt.spring.formework.annotation.GPController;
import com.ymt.spring.formework.annotation.GPRequestMapping;
import com.ymt.spring.formework.context.GPApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by @author yangmingtian on 2019/11/27
 */
@Slf4j
public class GPDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private GPApplicationContext context;

    private List<GPHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<GPHandlerMapping, GPHandlerAdapter> handlerAdapters = new HashMap<>();

    private List<GPViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        GPHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new GPModelAndView("404"));
            return;
        }
        //2、准备调用前的参数
        GPHandlerAdapter ha = getHandlerAdapter(handler);

        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        GPModelAndView mv = ha.handle(req, resp, handler);

        //这一步才是真正的输出
        processDispatchResult(req, resp, mv);
    }

    private GPHandlerAdapter getHandlerAdapter(GPHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        GPHandlerAdapter ha = this.handlerAdapters.get(handler);
        if (ha.supports(handler)) {
            return ha;
        }
        return null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, GPModelAndView mv) throws IOException {
        if (mv == null) {
            return;
        }
        if (this.viewResolvers.isEmpty()) {
            return;
        }

        for (GPViewResolver viewResolver : this.viewResolvers) {
            GPView view = viewResolver.resolveViewName(mv.getViewName(), null);
            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    private GPHandlerMapping getHandler(HttpServletRequest req) throws Exception {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (GPHandlerMapping handler : this.handlerMappings) {
            try {
                Matcher matcher = handler.getPattern().matcher(url);
                if (!matcher.matches()) {
                    continue;
                }
                return handler;
            } catch (Exception e) {
                throw e;
            }
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        context = new GPApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));

        initStrategies(context);
        System.out.println("dispatcher ...");
    }

    private void initStrategies(GPApplicationContext context) {
        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);

        initHandlerMappings(context);
        initHandlerAdapters(context);

        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);

        initViewReSolvers(context);

        initFlashMapManager(context);


    }

    private void initFlashMapManager(GPApplicationContext context) {
    }

    private void initViewReSolvers(GPApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i++) {
            this.viewResolvers.add(new GPViewResolver(templateRoot));
        }
    }

    private void initRequestToViewNameTranslator(GPApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(GPApplicationContext context) {
    }

    private void initHandlerAdapters(GPApplicationContext context) {
        //把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参

        //可想而知，他要拿到HandlerMapping才能干活
        //就意味着，有几个HandlerMapping就有几个HandlerAdapter
        for (GPHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new GPHandlerAdapter());
        }
    }

    private void initHandlerMappings(GPApplicationContext context) {

        String[] beanNames = context.getBeanDefinitionNames();

        try {

            for (String beanName : beanNames) {

                Object controller = context.getBean(beanName);

                Class<?> clazz = controller.getClass();

                if (!clazz.isAnnotationPresent(GPController.class)) {
                    continue;
                }

                String baseUrl = "";
                //获取Controller的url配置
                if (clazz.isAnnotationPresent(GPRequestMapping.class)) {
                    GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //获取Method的url配置
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {

                    //没有加RequestMapping注解的直接忽略
                    if (!method.isAnnotationPresent(GPRequestMapping.class)) {
                        continue;
                    }

                    //映射URL
                    GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                    //  /demo/query

                    //  (//demo//query)

                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);

                    this.handlerMappings.add(new GPHandlerMapping(pattern, controller, method));
                    log.info("Mapped " + regex + "," + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initThemeResolver(GPApplicationContext context) {
    }

    private void initLocaleResolver(GPApplicationContext context) {
    }

    private void initMultipartResolver(GPApplicationContext context) {
    }
}
