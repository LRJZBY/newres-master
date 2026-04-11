package com.newsrec.manage.service.impl;

import java.util.List;
import com.newsrec.common.utils.DateUtils;
import com.newsrec.manage.domain.vo.NewsCategoriesVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.newsrec.manage.mapper.NewsCategoriesMapper;
import com.newsrec.manage.domain.NewsCategories;
import com.newsrec.manage.service.INewsCategoriesService;

import javax.annotation.Resource;

/**
 * 新闻分类Service业务层处理
 *
 * @author weiyan
 * @date 2025-02-20
 */
@Service
public class NewsCategoriesServiceImpl implements INewsCategoriesService
{
    @Resource
    private NewsCategoriesMapper newsCategoriesMapper;

    /**
     * 查询新闻分类
     *
     * @param categoryId 新闻分类主键
     * @return 新闻分类
     */
    @Override
    public NewsCategories selectNewsCategoriesByCategoryId(Long categoryId)
    {
        return newsCategoriesMapper.selectNewsCategoriesByCategoryId(categoryId);
    }

    /**
     * 查询新闻分类列表
     *
     * @param newsCategories 新闻分类
     * @return 新闻分类
     */
    @Override
    public List<NewsCategories> selectNewsCategoriesList(NewsCategories newsCategories)
    {
        return newsCategoriesMapper.selectNewsCategoriesList(newsCategories);
    }

    /**
     * 查询新闻分类VO列表
     *
     * @param newsCategories 新闻分类
     * @return 新闻分类
     */
    @Override
    public List<NewsCategoriesVo> selectNewsCategoriesVoList(NewsCategories newsCategories)
    {
        return newsCategoriesMapper.selectNewsCategoriesVOList(newsCategories);
    }

    /**
     * 新增新闻分类
     *
     * @param newsCategories 新闻分类
     * @return 结果
     */
    @Override
    public int insertNewsCategories(NewsCategories newsCategories)
    {
        newsCategories.setCreateTime(DateUtils.getNowDate());
        return newsCategoriesMapper.insertNewsCategories(newsCategories);
    }

    /**
     * 修改新闻分类
     *
     * @param newsCategories 新闻分类
     * @return 结果
     */
    @Override
    public int updateNewsCategories(NewsCategories newsCategories)
    {
        newsCategories.setUpdateTime(DateUtils.getNowDate());
        return newsCategoriesMapper.updateNewsCategories(newsCategories);
    }

    /**
     * 批量删除新闻分类
     *
     * @param categoryIds 需要删除的新闻分类主键
     * @return 结果
     */
    @Override
    public int deleteNewsCategoriesByCategoryIds(Long[] categoryIds)
    {
        return newsCategoriesMapper.deleteNewsCategoriesByCategoryIds(categoryIds);
    }

    /**
     * 删除新闻分类信息
     *
     * @param categoryId 新闻分类主键
     * @return 结果
     */
    @Override
    public int deleteNewsCategoriesByCategoryId(Long categoryId)
    {
        return newsCategoriesMapper.deleteNewsCategoriesByCategoryId(categoryId);
    }



}
