package com.springgboot.refactor.infa.aop;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.springgboot.refactor.infa.constants.BaseConstants;
import com.springgboot.refactor.infa.exception.ParamCheckException;
import com.springgboot.refactor.util.IpUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

/**
 * 全局日志打印(没事儿干别随便动这个切面 除非有全局参数要调整)
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    private static String VO_LOCATION;

    @Value("${project.global.vo.location}")
    public void setVoLocation(String voLocation) {
        VO_LOCATION = voLocation;
    }

    private static final String TOKEN = "token";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder(toBuilder = true)
    private static class RequestInfo {
        private String url;
        private String uri;
        private String method;
        private String ip;
        private String classMethod;
        private String queryString;
        private Object[] args;
        private Object params;
        private String startTime;
        private Object header;
        private String token;
        private String traceId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder(toBuilder = true)
    private static class ReturnInfo {
        private Object resultStr;
        private long cost;
        private String classMethod;
        private String uri;
        private RequestInfo requestInfo;
        private Object params;
        private Object header;
        private String token;
        private String traceId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class RequestAndReturnInfo {
        private RequestInfo requestInfo;
        private ReturnInfo returnInfo;
        private String params;
        private String header;
    }

    @Pointcut("execution(public * com.springgboot.refactor.api..*.*(..))")
    public void webLog() {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        RequestInfo requestInfo = new RequestInfo();
        if (!url.contains(BaseConstants.PING)) {
            // 这里将接口调用次数的缓存去掉了 有需要可以加回来
            requestInfo = paddingRequestInfo(request, pjp);
        }

        // obj的值就是被拦截方法的返回值
        Object obj;
        try {
            obj = pjp.proceed();
        } catch (Exception e) {
            // 属于这些异常 打印 info 日志
            if (e instanceof ParamCheckException) {
                paddingReturnInfo(e.getMessage(), startTime, requestInfo);
                throw e;
            }
            dealException(requestInfo, startTime, e.getMessage());
            throw e;
        }

        if (!url.contains(BaseConstants.PING)) {
            paddingReturnInfo(obj, startTime, requestInfo);
        }
        return obj;
    }

    private static JSONObject getKeyAndValue(Object[] params) {
        JSONObject json = new JSONObject(60);
        for (int i = 0; i < params.length; i++) {
            // ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
            // ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
            if (params[i] == null) {
                continue;
            }
            if (params[i] instanceof BindingResult
                    || params[i] instanceof ServletRequest
                    || params[i] instanceof ServletResponse
                    || params[i] instanceof MultipartFile) {
                continue;
            }
            if (params[i] instanceof Integer
                    || params[i] instanceof String
                    || params[i] instanceof Long
                    || params[i] instanceof Character
                    || params[i] instanceof Double
                    || params[i] instanceof Short
                    || params[i] instanceof Float
                    || params[i] instanceof Boolean
                    || params[i] instanceof Byte) {
                continue;
            }
            // 得到类对象
            Class<?> userCla = params[i].getClass();
            /* 得到类中的所有属性集合 */
            Field[] fs = userCla.getDeclaredFields();
            for (Field f : fs) {
                // 设置些属性是可以访问的
                f.setAccessible(true);
                try {
                    // 设置键值
                    json.put(f.getName(), f.get(params[i]));
                } catch (Exception e) {
                    log.error("解析参数异常", e);
                }
            }
            superKeyAndValue(params, json, i, userCla);
        }
        return json;
    }

    private static void superKeyAndValue(Object[] params, JSONObject json, int i, Class<?> userCla) {
        Class<?> superclass = userCla.getSuperclass();
        if (superclass.getName().startsWith(VO_LOCATION)) {
            for (Field f : superclass.getDeclaredFields()) {
                // 设置些属性是可以访问的
                f.setAccessible(true);
                try {
                    // 设置键值
                    json.put(f.getName(), f.get(params[i]));
                } catch (Exception e) {
                    log.error("解析参数异常", e);
                }
            }
            superKeyAndValue(params, json, i, superclass);
        }
    }

    /**
     * 打印请求日志
     */
    private RequestInfo paddingRequestInfo(HttpServletRequest request, ProceedingJoinPoint pjp) {
        String method = request.getMethod();
        String queryString = request.getQueryString();

        RequestInfo.RequestInfoBuilder<?, ?> requestInfoBuilder = RequestInfo.builder();
        requestInfoBuilder.url(request.getRequestURL().toString())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .ip(IpUtil.getIp(request))
                .classMethod(pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName())
                .queryString(queryString);

        Object[] args = pjp.getArgs();
        Object params = "";
        // 获取请求参数集合并进行遍历拼接
        if (args.length > 0) {
            if ("POST".equalsIgnoreCase(method)) {
                params = getKeyAndValue(args);
            } else if ("GET".equalsIgnoreCase(method)) {
                params = queryString;
            }
        }
        requestInfoBuilder.params(params);

        JSONObject header = new JSONObject();
        Enumeration<String> headerNames = request.getHeaderNames();
        String token = "";
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            header.put(key, value);
            if (TOKEN.equals(key)) {
                token = value;
            }
        }
        requestInfoBuilder
                .token(token)
                .header(header)
                .traceId(MDC.get(BaseConstants.TRACE_ID))
                .startTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        RequestInfo requestInfo = requestInfoBuilder.build();
        log.info(JSON.toJSONString(requestInfo));
        return requestInfo;
    }

    /**
     * 打印响应日志
     */
    private void paddingReturnInfo(Object result, long startTime, RequestInfo requestInfo) {
        log.info(
                JSON.toJSONString(
                        ReturnInfo.builder()
                                .cost(System.currentTimeMillis() - startTime)
                                .uri(requestInfo.getUri())
                                .requestInfo(requestInfo)
                                .resultStr(
                                        result == null ?
                                                "" : result
                                )
                                .params(requestInfo.getParams())
                                .header(requestInfo.getHeader())
                                .token(requestInfo.getToken())
                                .traceId(requestInfo.getTraceId())
                                .build()
                )
        );
    }

    /**
     * 打印异常日志
     */
    private void dealException(RequestInfo requestInfo, Long startTime, String ex) {
        paddingReturnInfo(ex, startTime, requestInfo);
        log.error(JSONObject.toJSONString(requestInfo));
    }

}


