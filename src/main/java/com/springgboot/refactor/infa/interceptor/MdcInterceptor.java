package com.springgboot.refactor.infa.interceptor;

import com.springgboot.refactor.infa.constants.BaseConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;


/**
 * MDC日志链路追踪 拦截器
 *
 * @author Elliot
 */
@Component
public class MdcInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /* 如果有上层调用就用上层的ID */
        String traceId = request.getHeader(BaseConstants.TRACE_ID);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        MDC.put(BaseConstants.TRACE_ID, traceId);
        return Boolean.TRUE;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        /* 每次请求完成后清理存储的线程 */
        MDC.remove(BaseConstants.TRACE_ID);
    }
}
