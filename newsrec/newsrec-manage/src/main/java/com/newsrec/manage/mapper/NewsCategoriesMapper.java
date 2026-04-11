package com.newsrec.manage.mapper;

import java.util.List;
import com.newsrec.manage.domain.NewsCategories;
import com.newsrec.manage.domain.vo.NewsCategoriesVo;

/**
 * 新闻分类Mapper接口
 *
 * @author weiyan
 * @date 2025-02-20
 */
public interface NewsCategoriesMapper
{
    /**
     * 查询新闻分类
     *
     * @param categoryId 新闻分类主键
     * @return 新闻分类
     */
    public NewsCategories selectNewsCategoriesByCategoryId(Long categoryId);

    /**
     * 查询新闻分类列表
     *
     * @param newsCategories 新闻分类
     * @return 新闻分类集合
     */
    public List<NewsCategories> selectNewsCategoriesList(NewsCategories newsCategories);

    /**
     * 新增新闻分类
     *
     * @param newsCategories 新闻分类
     * @return 结果
     */
    public int insertNewsCategories(NewsCategories newsCategories);

    /**
     * 修改新闻分类
     *
     * @param newsCategories 新闻分类
     * @return 结果
     */
    public int updateNewsCategories(NewsCategories newsCategories);

    /**
     * 删除新闻分类
     *
     * @param categoryId 新闻分类主键
     * @return 结果
     */
    public int deleteNewsCategoriesByCategoryId(Long categoryId);

    /**
     * 批量删除新闻分类
     *
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNewsCategoriesByCategoryIds(Long[] categoryIds);


    /**
     * 查询新闻分类列表VO
     *
     * @param newsCategories 新闻分类
     * @return 新闻分类集合
     */
    public List<NewsCategoriesVo> selectNewsCategoriesVOList(NewsCategories newsCategories);
}
