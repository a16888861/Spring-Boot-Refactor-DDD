package com.springgboot.refactor.infrastructure.config.akka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.springgboot.refactor.infrastructure.config.akka.AkkaConstant.AKKA_PRE_ADDRESS;

/**
 * Akka配置文件
 *
 * @author Elliot
 */
@Data
@ConfigurationProperties(prefix = AKKA_PRE_ADDRESS)
public class AkkaProperties {

    /**
     * 是否开启Akka(true/false)
     */
    private Boolean enable;
    /**
     * 是否刷新Akka配置(todo 待做)
     */
    private Boolean refreshConfiguration = false;
}
