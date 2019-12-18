package com.yzw.mro.distributelock.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * wujun
 * 2019/12/17 16:40
 */
@Configuration
@ConditionalOnClass(Redisson.class)
@EnableConfigurationProperties(RedissonConfigurationProperties.class)
public class RedissonAutoConfiguration {

    @Autowired
    RedissonConfigurationProperties redissonConfigurationProperties;

    @Autowired
    private ApplicationContext ctx;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient() {
        Config config = null;
        if (redissonConfigurationProperties.getConfig() != null) {
            try {
                InputStream is = getConfigStream();
                config = Config.fromJSON(is);
            } catch (IOException e) {
                // trying next format
                try {
                    InputStream is = getConfigStream();
                    config = Config.fromYAML(is);
                } catch (IOException e1) {
                    throw new IllegalArgumentException("Can't parse config", e1);
                }
            }
        }
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<>(16);
        config.put("localCache", new CacheConfig(60000, 20000));
        CacheManager cacheManager = new RedissonSpringCacheManager(redissonClient, config);
        return cacheManager;
    }

    private InputStream getConfigStream() throws IOException {
        Resource resource = ctx.getResource(redissonConfigurationProperties.getConfig());
        InputStream is = resource.getInputStream();
        return is;
    }
}
