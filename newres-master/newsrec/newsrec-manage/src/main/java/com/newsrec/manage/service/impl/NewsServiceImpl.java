package com.newsrec.manage.service.impl;

import java.util.List;
import com.newsrec.common.utils.DateUtils;
import com.newsrec.manage.domain.vo.NewsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.newsrec.manage.mapper.NewsMapper;
import com.newsrec.manage.domain.News;
import com.newsrec.manage.service.INewsService;

import javax.annotation.Resource;

/**
 * 新闻内容Service业务层处理
 *
 * @author weiyan
 * @date 2025-02-20
 */
@Service
public class NewsServiceImpl implements INewsService
{
    @Resource
    private NewsMapper newsMapper;


    @Override
    public Integer countNewsCommentByUserId(Long userId) {
        return newsMapper.countNewsCommentByUserId(userId);
    }

    @Override
    public Integer countNewsLikeByUserId(Long userId) {
        return newsMapper.countNewsLikeByUserId(userId);
    }

    @Override
    public Integer countNewsFavoriteByUserId(Long userId) {
        return newsMapper.countNewsFavoriteByUserId(userId);
    }

    @Override
    public Integer countNewsByUserId(Long userId) {
        return newsMapper.countNewsByUserId(userId);
    }

    //返回所有的新闻vo数据
    public List<NewsVo> selectAllNewsVOList(){
        return newsMapper.selectAllNewsVOList();
    }


    //新闻的数据echarts渲染
    @Override
    public Integer getNewsTotal() {return newsMapper.countNews();}


    @Override
    public NewsVo selectNewsVOByNewsId(Long newsId){
        return newsMapper.selectNewsVOByNewsId(newsId);
    }

    @Override
    public Integer getPendingTotal() {return newsMapper.countPendingNews();}

    /**
     * 查询新闻内容
     *
     * @param newsId 新闻内容主键
     * @return 新闻内容
     */
    @Override
    public News selectNewsByNewsId(Long newsId)
    {
        return newsMapper.selectNewsByNewsId(newsId);
    }

    /**
     * 查询新闻内容列表
     *
     * @param news 新闻内容
     * @return 新闻内容
     */
    @Override
    public List<News> selectNewsList(News news)
    {
        return newsMapper.selectNewsList(news);
    }

    /**
     * 新增新闻内容
     *
     * @param news 新闻内容
     * @return 结果
     */
    @Override
    public int insertNews(News news)
    {
        news.setCreateTime(DateUtils.getNowDate());
        return newsMapper.insertNews(news);
    }

    /**
     * 修改新闻内容
     *
     * @param news 新闻内容
     * @return 结果
     */
    @Override
    public int updateNews(News news)
    {
        news.setUpdateTime(DateUtils.getNowDate());
        return newsMapper.updateNews(news);
    }

    /**
     * 批量删除新闻内容
     *
     * @param newsIds 需要删除的新闻内容主键
     * @return 结果
     */
    @Override
    public int deleteNewsByNewsIds(Long[] newsIds)
    {
        return newsMapper.deleteNewsByNewsIds(newsIds);
    }

    /**
     * 删除新闻内容信息
     *
     * @param newsId 新闻内容主键
     * @return 结果
     */
    @Override
    public int deleteNewsByNewsId(Long newsId)
    {
        return newsMapper.deleteNewsByNewsId(newsId);
    }

    //轮播图vo
    @Override
    public List<NewsVo> selectNewsVOList(News news){
        return newsMapper.selectNewsVOList(news);
    }
}
