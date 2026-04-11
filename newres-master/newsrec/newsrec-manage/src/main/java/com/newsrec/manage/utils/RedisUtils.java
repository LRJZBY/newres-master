package com.newsrec.manage.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, String value, long time) {
        logger.info("Setting key: {}, value: {}, time: {}", key, value, time);
        stringRedisTemplate.opsForValue().set(key, value, time);
    }

    public String get(String key) {
        logger.info("Getting key: {}", key);
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void increment(String key, double delta) {
        logger.info("Incrementing key: {}, delta: {}", key, delta);
        stringRedisTemplate.opsForZSet().incrementScore("hotNews", key, delta);
    }

    public List<String> getHotNews(int count) {
        logger.info("Getting hot news, count: {}", count);
        Set<String> hotNewsSet = stringRedisTemplate.opsForZSet().reverseRange("hotNews", 0, count - 1);
        return hotNewsSet.stream().collect(Collectors.toList());
    }

    // 新增方法：清空热榜数据
    public void clearHotNews() {
        logger.info("Clearing hot news data");
        stringRedisTemplate.opsForZSet().removeRange("hotNews", 0, -1);
    }
}
