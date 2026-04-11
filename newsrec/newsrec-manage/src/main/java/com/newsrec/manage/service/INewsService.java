package com.newsrec.manage.service;

import java.util.List;
import com.newsrec.manage.domain.News;
import com.newsrec.manage.domain.vo.NewsVo;

/**
 * 新闻内容Service接口
 *
 * @author weiyan
 * @date 2025-02-20
 */
public interface INewsService
{

    //获取新闻的条数和待审核数量
    public Integer getNewsTotal() ;

    public Integer getPendingTotal();

    public Integer countNewsCommentByUserId(Long userId);
    public Integer countNewsLikeByUserId(Long userId);
    public Integer countNewsFavoriteByUserId(Long userId);
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
     * 批量删除新闻内容
     *
     * @param newsIds 需要删除的新闻内容主键集合
     * @return 结果
     */
    public int deleteNewsByNewsIds(Long[] newsIds);

    /**
     * 删除新闻内容信息
     *
     * @param newsId 新闻内容主键
     * @return 结果
     */
    public int deleteNewsByNewsId(Long newsId);

    /**
     * 查询新闻列表VO
     *
     * @param news 新闻
     * @return 新闻
     */
    public List<NewsVo> selectNewsVOList(News news);

    //根据新闻id查询新闻VO
    public NewsVo selectNewsVOByNewsId(Long newsId);

    //返回所有的新闻vo数据
    public List<NewsVo> selectAllNewsVOList();
}
