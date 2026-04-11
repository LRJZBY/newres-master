package com.newsrec.manage.service.impl;

import com.newsrec.manage.domain.UserPreferences;
import com.newsrec.manage.domain.vo.NewsVo;
import com.newsrec.manage.mapper.UserPreferencesMapper;
import com.newsrec.manage.service.INewsService;
import com.newsrec.manage.service.IRecommendationService;
import com.newsrec.manage.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 新闻推荐Service实现类
 *
 * @author weiyan
 * @date 2025-04-07
 */
@Service
public class RecommendationServiceImpl implements IRecommendationService {

    @Resource
    private UserPreferencesMapper userPreferencesMapper;

    @Autowired
    private INewsService newsService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 获取基于用户兴趣分类的推荐新闻
     * 根据用户设置的兴趣分类，推荐相关分类新闻
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 推荐新闻列表
     */
    @Override
    public List<NewsVo> getContentBasedRecommendations(Long userId, int limit) {
        // 1. 获取用户兴趣分类
        List<UserPreferences> userPreferences = userPreferencesMapper.selectUserPreferencesByUserId(userId);
        if (userPreferences == null || userPreferences.isEmpty()) {
            // 用户未设置兴趣分类，返回热点新闻
            return getPopularityBasedRecommendations(limit);
        }
        // 2. 按照优先级排序
        userPreferences.sort(Comparator.comparing(UserPreferences::getPriority));
        // 3. 获取分类ID列表
        List<Long> categoryIds = userPreferences.stream()
                .map(UserPreferences::getCategoryId)
                .collect(Collectors.toList());
        // 4. 根据分类ID获取新闻
        List<NewsVo> recommendedNews = new ArrayList<>();
        // 先按照用户兴趣优先级分配不同类别的新闻数量
        Map<Long, Integer> categoryNewsCount = new HashMap<>();
        int totalPreferenceWeight = userPreferences.stream()
                .mapToInt(pref -> (4 - pref.getPriority().intValue()))
                .sum();
        for (UserPreferences pref : userPreferences) {
            int weight = 4 - pref.getPriority().intValue();
            int categoryNewsLimit = Math.max(1, (weight * limit) / totalPreferenceWeight);
            categoryNewsCount.put(pref.getCategoryId(), categoryNewsLimit);
        }
        // 5. 按权重获取每个分类的新闻
        for (UserPreferences pref : userPreferences) {
            List<NewsVo> categoryNews = userPreferencesMapper.selectNewsByCategory(
                    pref.getCategoryId(), categoryNewsCount.get(pref.getCategoryId()));
            recommendedNews.addAll(categoryNews);
            // 如果已经获取足够的新闻，就退出循环
            if (recommendedNews.size() >= limit) {
                break;
            }
        }
        // 6. 如果新闻数量不足，补充热点新闻
        if (recommendedNews.size() < limit) {
            List<NewsVo> popularNews = getPopularityBasedRecommendations(limit - recommendedNews.size());
            // 排除已经推荐的新闻
            Set<Long> recommendedNewsIds = recommendedNews.stream()
                    .map(NewsVo::getNewsId)
                    .collect(Collectors.toSet());
            popularNews = popularNews.stream()
                    .filter(news -> !recommendedNewsIds.contains(news.getNewsId()))
                    .collect(Collectors.toList());
            recommendedNews.addAll(popularNews);
        }
        // 7. 限制数量
        return recommendedNews.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取基于用户行为的推荐新闻（协同过滤）
     * 根据用户的历史行为（浏览、点赞、收藏等）推荐类似用户喜欢的新闻
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 推荐新闻列表
     */
    @Override
    public List<NewsVo> getCollaborativeFilteringRecommendations(Long userId, int limit) {
        // 1. 获取用户的历史行为数据（浏览、点赞、收藏）
        List<Long> viewedNewsIds = userPreferencesMapper.selectUserViewedNewsIds(userId);
        List<Long> likedNewsIds = userPreferencesMapper.selectUserLikedNewsIds(userId);
        List<Long> favoriteNewsIds = userPreferencesMapper.selectUserFavoriteNewsIds(userId);

        // 合并所有行为数据，并给予不同权重
        Map<Long, Double> userNewsInteractions = new HashMap<>();

        // 浏览的权重较低
        for (Long newsId : viewedNewsIds) {
            userNewsInteractions.put(newsId, 0.3);
        }

        // 点赞的权重中等
        for (Long newsId : likedNewsIds) {
            userNewsInteractions.put(newsId, userNewsInteractions.getOrDefault(newsId, 0.0) + 0.5);
        }

        // 收藏的权重最高
        for (Long newsId : favoriteNewsIds) {
            userNewsInteractions.put(newsId, userNewsInteractions.getOrDefault(newsId, 0.0) + 1.0);
        }

        // 2. 如果用户没有历史行为数据，返回热点新闻
        if (userNewsInteractions.isEmpty()) {
            return getPopularityBasedRecommendations(limit);
        }

        // 3. 找到与当前用户有相似行为的其他用户
        Map<Long, Double> similarUsers = findSimilarUsers(userId, userNewsInteractions);

        // 4. 获取相似用户喜欢的新闻（排除当前用户已浏览的新闻）
        Set<Long> userInteractedNewsIds = userNewsInteractions.keySet();
        List<NewsVo> recommendedNews = new ArrayList<>();

        // 5. 从相似用户中获取推荐新闻
        for (Map.Entry<Long, Double> entry : similarUsers.entrySet()) {
            Long similarUserId = entry.getKey();
            Double similarity = entry.getValue();

            // 获取相似用户交互过的新闻
            List<Long> similarUserViewedNewsIds = userPreferencesMapper.selectUserViewedNewsIds(similarUserId);
            List<Long> similarUserLikedNewsIds = userPreferencesMapper.selectUserLikedNewsIds(similarUserId);
            List<Long> similarUserFavoriteNewsIds = userPreferencesMapper.selectUserFavoriteNewsIds(similarUserId);

            // 合并并添加权重
            Map<Long, Double> newsWeight = new HashMap<>();
            for (Long newsId : similarUserViewedNewsIds) {
                if (!userInteractedNewsIds.contains(newsId)) {
                    newsWeight.put(newsId, 0.2 * similarity);
                }
            }

            for (Long newsId : similarUserLikedNewsIds) {
                if (!userInteractedNewsIds.contains(newsId)) {
                    newsWeight.put(newsId, newsWeight.getOrDefault(newsId, 0.0) + 0.3 * similarity);
                }
            }

            for (Long newsId : similarUserFavoriteNewsIds) {
                if (!userInteractedNewsIds.contains(newsId)) {
                    newsWeight.put(newsId, newsWeight.getOrDefault(newsId, 0.0) + 0.5 * similarity);
                }
            }

            // 按权重排序并获取推荐新闻
            List<Long> recommendNewsIds = newsWeight.entrySet().stream()
                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // 获取新闻详情
            for (Long newsId : recommendNewsIds) {
                NewsVo newsVo = newsService.selectNewsVOByNewsId(newsId);
                if (newsVo != null) {
                    recommendedNews.add(newsVo);
                }

                if (recommendedNews.size() >= limit) {
                    break;
                }
            }

            if (recommendedNews.size() >= limit) {
                break;
            }
        }

        // 6. 如果新闻数量不足，补充热点新闻
        if (recommendedNews.size() < limit) {
            List<NewsVo> popularNews = getPopularityBasedRecommendations(limit - recommendedNews.size());
            // 排除已经推荐的新闻
            Set<Long> recommendedNewsIds = recommendedNews.stream()
                    .map(NewsVo::getNewsId)
                    .collect(Collectors.toSet());
            popularNews = popularNews.stream()
                    .filter(news -> !recommendedNewsIds.contains(news.getNewsId()))
                    .collect(Collectors.toList());
            recommendedNews.addAll(popularNews);
        }

        // 7. 限制数量
        return recommendedNews.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 找到与当前用户有相似行为的其他用户
     *
     * @param userId 用户ID
     * @param userNewsInteractions 用户新闻交互数据
     * @return 相似用户映射（用户ID -> 相似度）
     */
    private Map<Long, Double> findSimilarUsers(Long userId, Map<Long, Double> userNewsInteractions) {
        // 1. 获取所有其他用户
        List<Long> allUserIds = userPreferencesMapper.selectAllUserIds();
        allUserIds.remove(userId);
        // 2. 计算相似度
        Map<Long, Double> similarityScores = new HashMap<>();
        for (Long otherUserId : allUserIds) {
            // 获取其他用户的交互数据
            List<Long> otherViewedNewsIds = userPreferencesMapper.selectUserViewedNewsIds(otherUserId);
            List<Long> otherLikedNewsIds = userPreferencesMapper.selectUserLikedNewsIds(otherUserId);
            List<Long> otherFavoriteNewsIds = userPreferencesMapper.selectUserFavoriteNewsIds(otherUserId);
            Map<Long, Double> otherUserInteractions = new HashMap<>();
            // 浏览权重
            for (Long newsId : otherViewedNewsIds) {
                otherUserInteractions.put(newsId, 0.2);
            }
            // 点赞权重
            for (Long newsId : otherLikedNewsIds) {
                otherUserInteractions.put(newsId, otherUserInteractions.getOrDefault(newsId, 0.0) + 0.3);
            }
            // 收藏权重
            for (Long newsId : otherFavoriteNewsIds) {
                otherUserInteractions.put(newsId, otherUserInteractions.getOrDefault(newsId, 0.0) +0.5);
            }
            // 计算余弦相似度
            double similarity = calculateCosineSimilarity(userNewsInteractions, otherUserInteractions);
            if (similarity > 0) {
                similarityScores.put(otherUserId, similarity);
            }
        }
        // 3. 根据相似度排序
        return similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * 计算两个向量的余弦相似度
     *
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 余弦相似度
     */
    private double calculateCosineSimilarity(Map<Long, Double> vector1, Map<Long, Double> vector2) {
        Set<Long> intersection = new HashSet<>(vector1.keySet());
        intersection.retainAll(vector2.keySet());

        if (intersection.isEmpty()) {
            return 0.0;
        }
        double dotProduct = 0.0;
        for (Long key : intersection) {
            dotProduct += vector1.get(key) * vector2.get(key);
        }
        double norm1 = Math.sqrt(vector1.values().stream().mapToDouble(val -> val * val).sum());
        double norm2 = Math.sqrt(vector2.values().stream().mapToDouble(val -> val * val).sum());
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        return dotProduct / (norm1 * norm2);
    }

    /**
     * 获取基于热点的推荐新闻
     *
     * @param limit 返回数量
     * @return 推荐新闻列表
     */
    @Override
    public List<NewsVo> getPopularityBasedRecommendations(int limit) {
        // 1. 从Redis获取热点新闻ID
        List<String> hotNewsIds = redisUtils.getHotNews(limit);

        // 2. 获取新闻详情
        List<NewsVo> popularNews = new ArrayList<>();
        for (String newsId : hotNewsIds) {
            NewsVo newsVo = newsService.selectNewsVOByNewsId(Long.parseLong(newsId));
            if (newsVo != null) {
                popularNews.add(newsVo);
            }
        }

        return popularNews;
    }

    /**
     * 获取混合推荐结果
     * 综合基于内容、协同过滤和热点的推荐结果
     *
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 混合推荐新闻列表
     */
    @Override
    public List<NewsVo> getHybridRecommendations(Long userId, int limit) {
        // 1. 确定各种推荐算法的权重分配
        int contentBasedLimit = limit * 4 / 10;  // 基于用户新闻分类偏好的推荐：40%
        int collaborativeLimit = limit * 3 / 10; // 协同过滤推荐：30%
        int popularityLimit = limit * 3 / 10;    // 热点新闻推荐：30%

        // 2. 获取各类推荐结果
        List<NewsVo> contentBasedNews = getContentBasedRecommendations(userId, contentBasedLimit);
        List<NewsVo> collaborativeNews = getCollaborativeFilteringRecommendations(userId, collaborativeLimit);

        // 3. 合并推荐结果，去重
        Set<Long> recommendedNewsIds = new HashSet<>();
        List<NewsVo> hybridRecommendations = new ArrayList<>();

        // 先添加基于内容的推荐
        for (NewsVo news : contentBasedNews) {
            if (recommendedNewsIds.add(news.getNewsId())) {
                hybridRecommendations.add(news);
            }
        }

        // 再添加协同过滤的推荐
        for (NewsVo news : collaborativeNews) {
            if (recommendedNewsIds.add(news.getNewsId())) {
                hybridRecommendations.add(news);
            }
        }

        // 4. 如果推荐结果不够，补充热点新闻
        if (hybridRecommendations.size() < limit) {
            List<NewsVo> popularNews = getPopularityBasedRecommendations(limit - hybridRecommendations.size() + popularityLimit);
            for (NewsVo news : popularNews) {
                if (recommendedNewsIds.add(news.getNewsId())) {
                    hybridRecommendations.add(news);
                }

                if (hybridRecommendations.size() >= limit) {
                    break;
                }
            }
        }

        // 5. 限制数量，我这里最后最多10条
        return hybridRecommendations.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}
