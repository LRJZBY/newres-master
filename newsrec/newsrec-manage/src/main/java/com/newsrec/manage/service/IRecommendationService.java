package com.newsrec.manage.service;

import com.newsrec.manage.domain.vo.NewsVo;

import java.util.List;

/**
 * 新闻推荐Service接口
 *
 * @author weiyan
 * @date 2025-04-07
 */
public interface IRecommendationService {

    /**
     * 获取基于用户兴趣分类的推荐新闻
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 推荐新闻列表
     */
    public List<NewsVo> getContentBasedRecommendations(Long userId, int limit);

    /**
     * 获取基于用户行为的推荐新闻（协同过滤）
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 推荐新闻列表
     */
    public List<NewsVo> getCollaborativeFilteringRecommendations(Long userId, int limit);

    /**
     * 获取基于热点的推荐新闻
     *
     * @param limit 返回数量
     * @return 推荐新闻列表
     */
    public List<NewsVo> getPopularityBasedRecommendations(int limit);

    /**
     * 获取混合推荐结果
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 混合推荐新闻列表
     */
    public List<NewsVo> getHybridRecommendations(Long userId, int limit);
}
