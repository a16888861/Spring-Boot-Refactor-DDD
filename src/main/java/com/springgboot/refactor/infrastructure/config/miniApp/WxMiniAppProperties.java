package com.springgboot.refactor.infrastructure.config.miniApp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 微信小程序配置类
 *
 * @author Elliot
 */
@Data
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMiniAppProperties {
    /**
     * 是否开启
     */
    private Boolean enable;
    /**
     * 是否使用redis
     */
    private Boolean useRedis;
    /**
     * 小程序的配置
     */
    private List<MiniAppConfig> configs;

    @Data
    public static class MiniAppConfig {
        /**
         * 小程序ID
         */
        private String appid;
        /**
         * 密钥
         */
        private String secret;
        /**
         * 微信小程序消息服务器配置的token
         */
        private String token = null;
        /**
         * 微信小程序消息服务器配置的EncodingAESKey
         */
        private String aesKey = null;
        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;
    }
}
