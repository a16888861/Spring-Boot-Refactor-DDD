package com.springgboot.refactor.infrastructure.config.akka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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
     * 是否刷新Akka配置(true/false)
     */
    private Boolean refreshConfiguration = false;
    /**
     * 是否开启Akka种子节点配置(true/false)
     */
    private Boolean enableSeedNode = false;
    /**
     * 种子节点(集群中用得到)
     */
    private List<String> seedNode;
}
