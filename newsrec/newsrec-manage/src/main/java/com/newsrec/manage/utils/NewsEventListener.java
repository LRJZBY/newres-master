package com.newsrec.manage.utils;

/**
 * 功能：新闻监听器
 * 作者：严伟
 * 日期：2025/3/13 17:35
 **/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class NewsEventListener {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void onNewsView(Long newsId) {
        updateHotScore(newsId, 0.3);
    }

    public void onNewsLike(Long newsId) {
        updateHotScore(newsId, 0.5);
    }

    public void onNewsComment(Long newsId) {
        updateHotScore(newsId, 1.0);
    }

    private void updateHotScore(Long newsId, double score) {
        stringRedisTemplate.opsForZSet().incrementScore("hotNews", newsId.toString(), score);
    }
}
