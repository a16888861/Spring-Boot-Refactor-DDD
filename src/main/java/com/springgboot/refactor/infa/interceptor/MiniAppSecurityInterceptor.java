package com.springgboot.refactor.infa.interceptor;

import com.springgboot.refactor.infa.annotation.IgnoreMiniAppSecurity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 小程序-安全校验拦截器
 *
 * @author Elliot
 */
@Component
public class MiniAppSecurityInterceptor implements HandlerInterceptor {
    private static final String PATH_PATTERN = "saas";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (PATH_PATTERN.equals(request.getRequestURI().split("/")[1])) {
            if (handler instanceof HandlerMethod method) {
                // 放行加入注解的接口
                if (method.getMethodAnnotation(IgnoreMiniAppSecurity.class) != null) {
                    return Boolean.TRUE;
                }
                // do some thing
                handleMiniAppRequestToken(request);
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 处理请求头token数据
     */
    private void handleMiniAppRequestToken(HttpServletRequest request) {

    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
