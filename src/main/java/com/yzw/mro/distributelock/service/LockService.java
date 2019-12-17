package com.yzw.mro.distributelock.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁服务
 * wujun
 * 2019/12/16 16:59
 */
@Slf4j
public class LockService {

    StringRedisTemplate stringRedisTemplate;

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 获取锁
     *
     * @param key      锁名称
     * @param time     锁过期时间
     * @param timeUnit 时间单位
     * @return
     */
    public String getLock(String key, final Long time, final TimeUnit timeUnit) {

        try {
            final String value = UUID.randomUUID().toString();
            Boolean result = stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(
                    key.getBytes(StandardCharsets.UTF_8),
                    value.getBytes(StandardCharsets.UTF_8),
                    Expiration.from(time, timeUnit),
                    RedisStringCommands.SetOption.SET_IF_ABSENT));
            return result ? value : null;
        } catch (Exception e) {
            log.error("获取分布式锁失败，key={}", key, e);
            return null;
        }
    }

    public void unlock(String key, String value) {

        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            boolean unLockStat = stringRedisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.eval(script.getBytes(), ReturnType.BOOLEAN, 1,
                            key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8)));
            if (!unLockStat) {
                log.error("释放分布式锁失败，key={}，已自动超时，其他线程可能已经重新获取锁", key);
            }
        } catch (Exception e) {
            log.error("释放分布式锁失败，key={}", key, e);
        }
    }
}
