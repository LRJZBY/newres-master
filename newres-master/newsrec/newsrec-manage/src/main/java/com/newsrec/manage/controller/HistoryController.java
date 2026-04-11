package com.newsrec.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.newsrec.manage.domain.vo.HistoryVo;
import com.newsrec.manage.utils.NewsEventListener;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.newsrec.manage.domain.History;
import com.newsrec.manage.service.IHistoryService;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.common.core.page.TableDataInfo;

/**
 * 浏览历史Controller
 *
 * @author weiyan
 * @date 2025-02-20
 */
@RestController
@RequestMapping("/manage/history")
public class HistoryController extends BaseController
{
    @Autowired
    private IHistoryService historyService;


    /**
     * 查询浏览历史列表VO
     */
//    @PreAuthorize("@ss.hasPermi('manage:history:list')")
    @GetMapping("/list")
    public TableDataInfo list(History history)
    {
        startPage();
        List<HistoryVo> list = historyService.selectHistoryVoList(history);
        return getDataTable(list);
    }

    /**
     * 导出浏览历史列表
     */
//    @PreAuthorize("@ss.hasPermi('manage:history:export')")
    @Log(title = "浏览历史", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, History history)
    {
        List<History> list = historyService.selectHistoryList(history);
        ExcelUtil<History> util = new ExcelUtil<History>(History.class);
        util.exportExcel(response, list, "浏览历史数据");
    }

    /**
     * 获取浏览历史详细信息
     */
//    @PreAuthorize("@ss.hasPermi('manage:history:query')")
    @GetMapping(value = "/{historyId}")
    public AjaxResult getInfo(@PathVariable("historyId") Long historyId)
    {
        return success(historyService.selectHistoryByHistoryId(historyId));
    }

    /**
     * 新增浏览历史
     */
//    @PreAuthorize("@ss.hasPermi('manage:history:add')")
    @Log(title = "浏览历史", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody History history) {
        return toAjax(historyService.insertHistory(history));
    }

    /**
     * 修改浏览历史
     */
//    @PreAuthorize("@ss.hasPermi('manage:history:edit')")
    @Log(title = "浏览历史", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody History history)
    {
        return toAjax(historyService.updateHistory(history));
    }

    /**
     * 删除浏览历史
     */
//    @PreAuthorize("@ss.hasPermi('manage:history:remove')")
    @Log(title = "浏览历史", businessType = BusinessType.DELETE)
	@DeleteMapping("/{historyIds}")
    public AjaxResult remove(@PathVariable Long[] historyIds) {
        return toAjax(historyService.deleteHistoryByHistoryIds(historyIds));
    }
}
