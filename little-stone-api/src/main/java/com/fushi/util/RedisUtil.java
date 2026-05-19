package com.fushi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis set error, key: {}", key, e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis set with timeout error, key: {}", key, e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get error, key: {}", key, e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? clazz.cast(value) : null;
        } catch (Exception e) {
            log.error("Redis get with class error, key: {}", key, e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }

    public Boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis delete error, key: {}", key, e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }

    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Redis hasKey error, key: {}", key, e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }

    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis increment error, key: {}", key, e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }

    public void expire(String key, long timeout, TimeUnit unit) {
        try {
            redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("Redis expire error, key: {}", key, e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }

    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            log.error("Redis getExpire error, key: {}", key, e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }
}