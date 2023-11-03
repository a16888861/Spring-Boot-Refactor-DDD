package com.springgboot.refactor.infrastructure.config;

import com.springgboot.refactor.infrastructure.interceptor.AppletSecurityInterceptor;
import com.springgboot.refactor.infrastructure.interceptor.MdcInterceptor;
import com.springgboot.refactor.infrastructure.interceptor.PcSecurityInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器配置
 *
 * @author Elliot
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final MdcInterceptor mdcInterceptor;
    private final PcSecurityInterceptor pcSecurityInterceptor;
    private final AppletSecurityInterceptor appletSecurityInterceptor;

    /**
     * 指定静态资源的位置 非前后端分离项目需要注册
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*静态资源的位置*/
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
        /*放行swagger*/
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 拦截配置-此处可以将一些东西抽为启动器进行配置加载
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePathList = new ArrayList<>();
        excludePathList.add("/ping");
        excludePathList.add("/error");
        excludePathList.add("/favicon.ico");

        excludePathList.add("/doc.html");
        excludePathList.add("/webjars/**");
        excludePathList.add("/resources");
        excludePathList.add("/swagger-resources");
        excludePathList.add("/v2/**");

        excludePathList.add("/static");
        excludePathList.add("/public");
        excludePathList.add("/user/register");

        /*自定义拦截器*/
        registry.addInterceptor(mdcInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(pcSecurityInterceptor)
                .addPathPatterns("/pc")
                .excludePathPatterns(excludePathList);
        registry.addInterceptor(appletSecurityInterceptor)
                .addPathPatterns("/applet")
                .excludePathPatterns(excludePathList);
        log.info("拦截器放行的接口为:" + excludePathList);
    }
}
