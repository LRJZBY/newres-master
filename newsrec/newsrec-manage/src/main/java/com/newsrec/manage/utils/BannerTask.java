package com.newsrec.manage.utils;

/**
 * 功能
 * 作者：严伟
 * 日期：2025/3/31 17:34
 **/
import com.newsrec.manage.domain.Banner;
import com.newsrec.manage.domain.vo.NewsVo;
import com.newsrec.manage.service.IBannerService;
import com.newsrec.manage.service.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

//轮播图实现定时添加，每天顶部添加5条热榜新闻前三（后台也可以手动进行添加）
@Component
public class BannerTask {

    @Autowired
    private INewsService newsService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private IBannerService bannerService;

    // 每天凌晨0点添加新的轮播图
    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshBanner() {
        // 获取热度最高的前5条新闻
        List<String> hotNewsIds = redisUtils.getHotNews(5);
        // 确保列表是按照热度从高到低排序的
        Collections.reverse(hotNewsIds);
        //将热度最高的5条新闻插入到轮播图中
        for (String newsId : hotNewsIds) {
        //获取当前热点新闻数据
            NewsVo news = newsService.selectNewsVOByNewsId(Long.parseLong(newsId));
            Banner banner = new Banner();
            if (news != null) {
                //添加新的轮播图
                String remark=news.getTitle();
                String img=news.getImg();
                Long newsId1=news.getNewsId();
                banner.setNewsId(newsId1);
                banner.setImg(img);
                banner.setRemark(remark);
                bannerService.insertBanner(banner);
            }
        }
        // 清理旧的轮播图，只保留最新的十条
        bannerService.clearOldBanners();
    }
}
