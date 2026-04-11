package com.newsrec.manage.controller;

import com.newsrec.common.core.controller.BaseController;
import com.newsrec.common.core.page.TableDataInfo;
import com.newsrec.manage.domain.vo.NewsVo;
import com.newsrec.manage.service.IRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 新闻推荐Controller
 *
 * @author weiyan
 * @date 2025-04-07
 */
@RestController
@RequestMapping("/manage/recommendation")
public class RecommendationController extends BaseController {

    @Autowired
    private IRecommendationService recommendationService;

    /**
     * 获取用户的混合推荐新闻
     */
    @GetMapping("/hybrid/{userId}")
    public TableDataInfo getHybridRecommendations(@PathVariable("userId") Long userId,
                                                 @RequestParam(value = "limit", defaultValue = "10") int limit) {
        startPage();
        List<NewsVo> list = recommendationService.getHybridRecommendations(userId, limit);
        return getDataTable(list);
    }

    /**
     * 获取基于内容的推荐新闻
     */
    @GetMapping("/content/{userId}")
    public TableDataInfo getContentBasedRecommendations(@PathVariable("userId") Long userId,
                                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {
        startPage();
        List<NewsVo> list = recommendationService.getContentBasedRecommendations(userId, limit);
        return getDataTable(list);
    }

    /**
     * 获取基于协同过滤的推荐新闻
     */
    @GetMapping("/collaborative/{userId}")
    public TableDataInfo getCollaborativeFilteringRecommendations(@PathVariable("userId") Long userId,
                                                                 @RequestParam(value = "limit", defaultValue = "10") int limit) {
        startPage();
        List<NewsVo> list = recommendationService.getCollaborativeFilteringRecommendations(userId, limit);
        return getDataTable(list);
    }

    /**
     * 获取热门推荐新闻
     */
    @GetMapping("/popular")
    public TableDataInfo getPopularityBasedRecommendations(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        startPage();
        List<NewsVo> list = recommendationService.getPopularityBasedRecommendations(limit);
        return getDataTable(list);
    }
}
