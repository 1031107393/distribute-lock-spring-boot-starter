package com.yzw.mro.distributelock.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TODO
 * wujun
 * 2019/12/17 16:41
 */
@ConfigurationProperties(prefix = "spring.redis.redisson")
public class RedissonConfigurationProperties {

    private String config;

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
