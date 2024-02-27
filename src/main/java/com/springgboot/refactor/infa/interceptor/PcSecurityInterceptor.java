package com.springgboot.refactor.infa.interceptor;

import com.springgboot.refactor.infa.annotation.IgnorePcSecurity;
import com.springgboot.refactor.infa.exception.AuthorizationCheckException;
import com.springgboot.refactor.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Saas端-安全校验拦截器
 *
 * @author Elliot
 */
@Component
public class PcSecurityInterceptor implements HandlerInterceptor {

    private static final String PATH_PATTERN = "saas";
    private static final String AUTHORIZATION = "Saas-Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (PATH_PATTERN.equals(request.getRequestURI().split("/")[1])) {
            if (handler instanceof HandlerMethod method) {
                // 放行加入注解的接口
                if (method.getMethodAnnotation(IgnorePcSecurity.class) != null) {
                    return Boolean.TRUE;
                }
                // 校验请求
                return handleMiniAppRequestToken(request);
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 处理请求头token数据
     */
    private boolean handleMiniAppRequestToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);
        if (StringUtils.isBlank(authorization)) {
            throw new AuthorizationCheckException("Request IllegalStateException!");
        }
        return JwtUtil.verify(authorization);
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
