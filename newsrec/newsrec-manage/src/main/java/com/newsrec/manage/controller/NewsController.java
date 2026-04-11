package com.newsrec.manage.controller;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.newsrec.manage.domain.vo.NewsVo;
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
import com.newsrec.manage.domain.News;
import com.newsrec.manage.service.INewsService;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.common.core.page.TableDataInfo;

/**
 * 新闻内容Controller
 *
 * @author weiyan
 * @date 2025-02-20
 */
@RestController
@RequestMapping("/manage/news")
public class NewsController extends BaseController
{
    @Autowired
    private INewsService newsService;

    /**
     * 查询新闻内容列表
     */
//    @PreAuthorize("@ss.hasPermi('manage:news:list')")
    @GetMapping("/list")
    public TableDataInfo list(News news)
    {
        startPage();
        List<NewsVo> list = newsService.selectNewsVOList(news);
        return getDataTable(list);
    }


    /**
     * 导出新闻内容列表
     */
//    @PreAuthorize("@ss.hasPermi('manage:news:export')")
    @Log(title = "新闻内容", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, News news)
    {
        List<News> list = newsService.selectNewsList(news);
        ExcelUtil<News> util = new ExcelUtil<News>(News.class);
        util.exportExcel(response, list, "新闻内容数据");
    }

    /**
     * 获取新闻内容详细信息
     */
//    @PreAuthorize("@ss.hasPermi('manage:news:query')")
    @GetMapping(value = "/{newsId}")
    public AjaxResult getInfo(@PathVariable("newsId") Long newsId)
    {
        return success(newsService.selectNewsVOByNewsId(newsId));
    }

    /**
     * 新增新闻内容
     */
//    @PreAuthorize("@ss.hasPermi('manage:news:add')")
    @Log(title = "新闻内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody News news)
    {
        //默认从后台添加的新闻为特殊用户2（admin管理员）；正常业务应该是前台添加新闻
        //这里设置，为了方便测试功能，快速通过后台管理系统添加新闻
        // news.setUserId(2L);
        return toAjax(newsService.insertNews(news));
    }

    /**
     * 修改新闻内容
     */
//    @PreAuthorize("@ss.hasPermi('manage:news:edit')")
    @Log(title = "新闻内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody News news)
    {
        return toAjax(newsService.updateNews(news));
    }

    /**
     * 删除新闻内容
     */
//    @PreAuthorize("@ss.hasPermi('manage:news:remove')")
    @Log(title = "新闻内容", businessType = BusinessType.DELETE)
	@DeleteMapping("/{newsIds}")
    public AjaxResult remove(@PathVariable Long[] newsIds)
    {
        return toAjax(newsService.deleteNewsByNewsIds(newsIds));
    }
}
