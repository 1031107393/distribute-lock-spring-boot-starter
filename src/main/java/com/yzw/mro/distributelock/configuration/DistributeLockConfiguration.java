package com.yzw.mro.distributelock.configuration;

import com.yzw.mro.distributelock.service.LockService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 分布式锁配置
 * wujun
 * 2019/12/16 17:23
 */
@Configuration
public class DistributeLockConfiguration {

    @Bean
    @ConditionalOnClass(LockService.class)
    @ConditionalOnMissingBean(LockService.class)
    public LockService lockService(StringRedisTemplate stringRedisTemplate) {
        LockService lockService = new LockService();
        lockService.setStringRedisTemplate(stringRedisTemplate);
        return lockService;
    }
}
