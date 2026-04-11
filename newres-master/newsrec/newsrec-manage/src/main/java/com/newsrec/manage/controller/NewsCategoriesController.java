package com.newsrec.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.newsrec.manage.domain.vo.NewsCategoriesVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.newsrec.common.annotation.Log;
import com.newsrec.common.core.controller.BaseController;
import com.newsrec.common.core.domain.AjaxResult;
import com.newsrec.common.enums.BusinessType;
import com.newsrec.manage.domain.NewsCategories;
import com.newsrec.manage.service.INewsCategoriesService;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.common.core.page.TableDataInfo;

/**
 * 新闻分类Controller
 *
 * @author weiyan
 * @date 2025-02-20
 */
@RestController
@RequestMapping("/manage/categories")
public class NewsCategoriesController extends BaseController
{
    @Autowired
    private INewsCategoriesService newsCategoriesService;

    /**
     * 查询新闻分类列表（改造，Vo）
     */
//    @PreAuthorize("@ss.hasPermi('manage:categories:list')")
    @GetMapping("/list")
    public TableDataInfo list(NewsCategories newsCategories)
    {
        startPage();
        List<NewsCategoriesVo> volist = newsCategoriesService.selectNewsCategoriesVoList(newsCategories);
        return getDataTable(volist );
    }


    @GetMapping("/list1")
    public TableDataInfo list1(NewsCategories newsCategories)
    {
        startPage();
        List<NewsCategoriesVo> volist = newsCategoriesService.selectNewsCategoriesVoList(newsCategories);
        return getDataTable(volist );
    }

    /**
     * 导出新闻分类列表
     */
//    @PreAuthorize("@ss.hasPermi('manage:categories:export')")
    @Log(title = "新闻分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NewsCategories newsCategories)
    {
        List<NewsCategories> list = newsCategoriesService.selectNewsCategoriesList(newsCategories);
        ExcelUtil<NewsCategories> util = new ExcelUtil<NewsCategories>(NewsCategories.class);
        util.exportExcel(response, list, "新闻分类数据");
    }

    /**
     * 获取新闻分类详细信息
     */
//    @PreAuthorize("@ss.hasPermi('manage:categories:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return success(newsCategoriesService.selectNewsCategoriesByCategoryId(categoryId));
    }

    /**
     * 新增新闻分类
     */
//    @PreAuthorize("@ss.hasPermi('manage:categories:add')")
    @Log(title = "新闻分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NewsCategories newsCategories)
    {
        return toAjax(newsCategoriesService.insertNewsCategories(newsCategories));
    }

    /**
     * 修改新闻分类
     */
//    @PreAuthorize("@ss.hasPermi('manage:categories:edit')")
    @Log(title = "新闻分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NewsCategories newsCategories)
    {
        return toAjax(newsCategoriesService.updateNewsCategories(newsCategories));
    }

    /**
     * 删除新闻分类
     */
//    @PreAuthorize("@ss.hasPermi('manage:categories:remove')")
    @Log(title = "新闻分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(newsCategoriesService.deleteNewsCategoriesByCategoryIds(categoryIds));
    }
}
