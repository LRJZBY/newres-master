package com.newsrec.manage.mapper;

import java.util.List;
import com.newsrec.manage.domain.News;
import com.newsrec.manage.domain.NewsCategories;
import com.newsrec.manage.domain.vo.NewsCategoriesVo;
import com.newsrec.manage.domain.vo.NewsVo;
import org.apache.ibatis.annotations.Select;

/**
 * 新闻内容Mapper接口
 *
 * @author weiyan
 * @date 2025-02-20
 */
public interface NewsMapper
{

    //简单的统计数据，进行echarts渲染
    @Select("SELECT COUNT(*) FROM tb_news WHERE status = 1")
    Integer countNews();

    //待审核的新闻数量
    @Select("SELECT COUNT(*) FROM tb_news WHERE status = 0")
    Integer countPendingNews();

    //根据id返回NewsVo对象
    public NewsVo selectNewsVOByNewsId(Long newsId);


    // 根据用户id，统计该发布的新闻获得的评论数量
    @Select("SELECT COUNT(DISTINCT c.comment_id) AS total_comment_count " +
            "FROM tb_news n LEFT JOIN tb_comments c ON n.news_id = c.news_id " +
            "WHERE n.user_id = #{userId}")
    public Integer countNewsCommentByUserId(Long userId);

    //根据用户id，统计该发布的新闻获得的点赞数量
    @Select("SELECT COUNT(DISTINCT l.like_id) AS total_like_count " +
            "FROM tb_news n LEFT JOIN tb_likes l ON n.news_id = l.news_id " +
            "WHERE n.user_id = #{userId}")
    public Integer countNewsLikeByUserId(Long userId);

    //根据用户id，统计该发布的新闻获得的收藏数量
    @Select("SELECT COUNT(DISTINCT f.favorite_id) AS total_favorite_count " +
            "FROM tb_news n LEFT JOIN tb_favorites f ON n.news_id = f.news_id " +
            "WHERE n.user_id = #{userId}")
    public Integer countNewsFavoriteByUserId(Long userId);

    //根据用户id查询用户发布的新闻数量
    @Select("SELECT COUNT(*) FROM tb_news WHERE status = 1 AND user_id = #{userId}")
    public Integer countNewsByUserId(Long userId);


    /**
     * 查询新闻内容
     *
     * @param newsId 新闻内容主键
     * @return 新闻内容
     */
    public News selectNewsByNewsId(Long newsId);

    /**
     * 查询新闻内容列表
     *
     * @param news 新闻内容
     * @return 新闻内容集合
     */
    public List<News> selectNewsList(News news);

    /**
     * 新增新闻内容
     *
     * @param news 新闻内容
     * @return 结果
     */
    public int insertNews(News news);

    /**
     * 修改新闻内容
     *
     * @param news 新闻内容
     * @return 结果
     */
    public int updateNews(News news);

    /**
     * 删除新闻内容
     *
     * @param newsId 新闻内容主键
     * @return 结果
     */
    public int deleteNewsByNewsId(Long newsId);

    /**
     * 批量删除新闻内容
     *
     * @param newsIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNewsByNewsIds(Long[] newsIds);

    /**
     * 查询新闻列表VO
     *
     * @param news 新闻
     * @return 新闻
     */
    public List<NewsVo> selectNewsVOList(News news);


    //查询所有的新闻VO
    public List<NewsVo> selectAllNewsVOList();
}
