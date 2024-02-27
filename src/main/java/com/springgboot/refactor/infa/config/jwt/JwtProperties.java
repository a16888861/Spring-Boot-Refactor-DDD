package com.springgboot.refactor.infa.config.jwt;

import com.springgboot.refactor.util.DateUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.springgboot.refactor.infa.config.jwt.JwtConstant.JWT_PRE_ADDRESS;

/**
 * Jwt安全配置
 *
 * @author Elliot
 */
@Data
@ConfigurationProperties(prefix = JWT_PRE_ADDRESS)
public class JwtProperties {

    /**
     * 是否开启Jwt
     */
    private Boolean enable;
    /**
     * 密钥
     */
    private String secret;
    /**
     * 发行人
     */
    private String issuer;
    /**
     * 密钥参数键
     */
    private String paramKey;
    /**
     * 密钥参数时间
     */
    private String paramDate = DateUtil.getNow();
}
