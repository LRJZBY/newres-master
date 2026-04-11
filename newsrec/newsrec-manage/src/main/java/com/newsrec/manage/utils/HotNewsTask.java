package com.newsrec.manage.utils;

/**
 * 功能:新闻热榜更新的定时任务
 * 作者：严伟
 * 日期：2025/3/13 16:47
 **/
import com.newsrec.manage.domain.vo.NewsVo;
import com.newsrec.manage.service.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

@Component
public class HotNewsTask {

    @Autowired
    private INewsService newsService;

    @Autowired
    private RedisUtils redisUtils;

    // 每30分钟更新一次热点新闻榜
    @Scheduled(cron = "0 0/30 * * * ?")
    public void refreshHotNews() {
        // 获取所有新闻Vo
        List<NewsVo> newsList = newsService.selectAllNewsVOList();
        // 清空当前的热榜数据
        // 错误代码：redisUtils.increment("hotNews", 0);
        // 修正：清空 ZSet 中的所有成员
        redisUtils.clearHotNews();
        // 重新计算每条新闻的热度
        for (NewsVo news : newsList) {
            double hotScore = calculateHotScore(news);
            redisUtils.increment(news.getNewsId().toString(), hotScore);
        }
    }

    private double calculateHotScore(NewsVo news) {
        // 获取当前时间
        Date currentDate = new Date();
        // 计算时间差（毫秒）
        long timeDiff = currentDate.getTime() - news.getCreateTime().getTime();
        // 转换为天数
        double daysDiff = timeDiff / (24 * 60 * 60 * 1000.0);
        // 时间衰减因子
        double decayFactor = Math.exp(-daysDiff);
        // 热度计算公式
        return (news.getViewCount() * 0.3 + news.getLikeCount() * 0.5 + news.getCommentCount() * 1.0) * decayFactor;
    }
}
