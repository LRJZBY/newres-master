package com.newsrec.manage.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.newsrec.manage.domain.dto.HotNewsDTO;
import com.newsrec.manage.domain.vo.BannerVo;
import com.newsrec.manage.domain.vo.NewsVo;
import com.newsrec.manage.service.INewsService;
import com.newsrec.manage.utils.BannerTask;
import com.newsrec.manage.utils.RedisUtils;
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
import com.newsrec.manage.domain.Banner;
import com.newsrec.manage.service.IBannerService;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.common.core.page.TableDataInfo;

/**
 * 轮播图Controller
 *
 * @author weiyan
 * @date 2025-02-20
 */
@RestController
@RequestMapping("/manage/banner")
public class BannerController extends BaseController
{
    @Autowired
    private IBannerService bannerService;

    //定时任务，自动根据热点新闻添加刷新轮播图
    @Autowired
    private BannerTask bannerTask;

    //手动刷新的接口
    @GetMapping("/refresh")
    public String refreshBannerManually() {
        bannerTask.refreshBanner();
        return "Banner refreshed manually.";
    }


    /**
     * 查询轮播图列表
     */
    //@PreAuthorize("@ss.hasPermi('manage:banner:list')")
    @GetMapping("/list")
    public TableDataInfo list(Banner banner)
    {
        startPage();
        List<BannerVo> list = bannerService.selectBannerVoList(banner);
        return getDataTable(list);
    }

    /**
     * 导出轮播图列表
     */
    //@PreAuthorize("@ss.hasPermi('manage:banner:export')")
    @Log(title = "轮播图", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Banner banner)
    {
        List<Banner> list = bannerService.selectBannerList(banner);
        ExcelUtil<Banner> util = new ExcelUtil<Banner>(Banner.class);
        util.exportExcel(response, list, "轮播图数据");
    }

    /**
     * 获取轮播图详细信息
     */
    //@PreAuthorize("@ss.hasPermi('manage:banner:query')")
    @GetMapping(value = "/{bannerId}")
    public AjaxResult getInfo(@PathVariable("bannerId") Long bannerId)
    {
        return success(bannerService.selectBannerByBannerId(bannerId));
    }

    /**
     * 新增轮播图
     */
    //@PreAuthorize("@ss.hasPermi('manage:banner:add')")
    @Log(title = "轮播图", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Banner banner)
    {
        return toAjax(bannerService.insertBanner(banner));
    }

    /**
     * 修改轮播图
     */
    //@PreAuthorize("@ss.hasPermi('manage:banner:edit')")
    @Log(title = "轮播图", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Banner banner)
    {
        return toAjax(bannerService.updateBanner(banner));
    }

    /**
     * 删除轮播图
     */
    //@PreAuthorize("@ss.hasPermi('manage:banner:remove')")
    @Log(title = "轮播图", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bannerIds}")
    public AjaxResult remove(@PathVariable Long[] bannerIds)
    {
        return toAjax(bannerService.deleteBannerByBannerIds(bannerIds));
    }
}
