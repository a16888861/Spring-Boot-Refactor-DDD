package com.springgboot.refactor.infa.response;

import com.springgboot.refactor.util.IpUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
public class WebResponse<T> {

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 服务器IP
     */
    private String serverIp;

    /**
     * 当前时间
     */
    private Long time;

    /**
     * 请求成功
     */
    public static <T> WebResponse<T> success() {
        return returnResult(WebResponseEnum.SUCCESS.getCode(), WebResponseEnum.SUCCESS.getMessage(), null);
    }

    public static <T> WebResponse<T> success(T data) {
        return returnResult(WebResponseEnum.SUCCESS.getCode(), WebResponseEnum.SUCCESS.getMessage(), data);
    }

    public static <T> WebResponse<T> success(Integer code, String message) {
        return returnResult(code, message, null);
    }

    public static <T> WebResponse<T> success(Integer code, String message, T data) {
        return returnResult(code, message, data);
    }

    /**
     * 请求失败
     */
    public static <T> WebResponse<T> fail() {
        return returnResult(WebResponseEnum.FAILURE.getCode(), WebResponseEnum.FAILURE.getMessage(), null);
    }

    public static <T> WebResponse<T> fail(String message) {
        return returnResult(WebResponseEnum.FAILURE.getCode(), message, null);
    }

    public static <T> WebResponse<T> fail(Integer code, String message) {
        return returnResult(code, message, null);
    }

    public static <T> WebResponse<T> fail(Integer code, String message, T data) {
        return returnResult(code, message, data);
    }

    public static <T> WebResponse<T> custom(Integer code, String message) {
        return returnResult(code, message, null);
    }

    public static <T> WebResponse<T> custom(WebResponseEnum ackEnum) {
        return returnResult(ackEnum.getCode(), ackEnum.getMessage(), null);
    }

    private static <T> WebResponse<T> returnResult(int code, String message, T data) {
        WebResponse<T> responseInfo = new WebResponse<>();
        responseInfo.setCode(code)
                .setMessage(message)
                .setData(data)
                .setTime(System.currentTimeMillis())
                .setServerIp(IpUtil.getLocalIpAddress());
        return responseInfo;
    }

    /**
     * 响应枚举
     *
     * @author Elliot
     */
    public enum WebResponseEnum {

        /**
         * 请求成功
         */
        SUCCESS(200, "Request Success"),
        /**
         * 请求失败
         */
        FAILURE(500, "Request Fail"),
        ;

        private final Integer code;

        private final String message;

        WebResponseEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public static WebResponseEnum match(Integer code) {
            if (code != null) {
                for (WebResponseEnum response : WebResponseEnum.values()) {
                    if (code.equals(response.code)) {
                        return response;
                    }
                }
            }
            return WebResponseEnum.FAILURE;
        }
    }
}
