package com.newsrec.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.newsrec.manage.domain.Feedback;
import com.newsrec.manage.service.IFeedbackService;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.common.core.page.TableDataInfo;

/**
 * 问题反馈Controller
 *
 * @author weiyan
 * @date 2025-03-17
 */
@RestController
@RequestMapping("/manage/feedback")
public class FeedbackController extends BaseController
{
    @Autowired
    private IFeedbackService feedbackService;

    /**
     * 查询问题反馈列表
     */
//    @PreAuthorize("@ss.hasPermi('manage:feedback:list')")
    @GetMapping("/list")
    public TableDataInfo list(Feedback feedback)
    {
        startPage();
        List<Feedback> list = feedbackService.selectFeedbackList(feedback);
        return getDataTable(list);
    }



    /**
     * 获取问题反馈详细信息
     */
//    @PreAuthorize("@ss.hasPermi('manage:feedback:query')")
    @GetMapping(value = "/{feedbackId}")
    public AjaxResult getInfo(@PathVariable("feedbackId") Long feedbackId)
    {
        return success(feedbackService.selectFeedbackByFeedbackId(feedbackId));
    }

    /**
     * 新增问题反馈
     */
//    @PreAuthorize("@ss.hasPermi('manage:feedback:add')")
    @Log(title = "问题反馈", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Feedback feedback)
    {
        return toAjax(feedbackService.insertFeedback(feedback));
    }

    /**
     * 修改问题反馈
     */
//    @PreAuthorize("@ss.hasPermi('manage:feedback:edit')")
    @Log(title = "问题反馈", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Feedback feedback)
    {
        return toAjax(feedbackService.updateFeedback(feedback));
    }

    /**
     * 删除问题反馈
     */
//    @PreAuthorize("@ss.hasPermi('manage:feedback:remove')")
    @Log(title = "问题反馈", businessType = BusinessType.DELETE)
	@DeleteMapping("/{feedbackIds}")
    public AjaxResult remove(@PathVariable Long[] feedbackIds)
    {
        return toAjax(feedbackService.deleteFeedbackByFeedbackIds(feedbackIds));
    }
}
