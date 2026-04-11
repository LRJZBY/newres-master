package com.newsrec.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.newsrec.manage.domain.Messages;
import com.newsrec.manage.service.IMessagesService;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.common.core.page.TableDataInfo;

/**
 * 留言Controller
 *
 * @author weiyan
 * @date 2025-03-17
 */
@RestController
@RequestMapping("/manage/messages")
public class MessagesController extends BaseController
{
    @Autowired
    private IMessagesService messagesService;


    //获取前台的多级列表(在imp层递归树状结构)
    @GetMapping("frontList")
    public TableDataInfo getAllMessages() {
        startPage();
        List<Messages> list = messagesService.getAllMessages();
        return getDataTable(list);
    }


    /**
     * 查询留言列表
     */
//    @PreAuthorize("@ss.hasPermi('manage:messages:list')")
    @GetMapping("/list")
    public TableDataInfo list(Messages messages)
    {
        startPage();
        List<Messages> list = messagesService.selectMessagesList(messages);
        return getDataTable(list);
    }

    /**
     * 导出留言列表
     */
//    @PreAuthorize("@ss.hasPermi('manage:messages:export')")
    @Log(title = "留言", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Messages messages)
    {
        List<Messages> list = messagesService.selectMessagesList(messages);
        ExcelUtil<Messages> util = new ExcelUtil<Messages>(Messages.class);
        util.exportExcel(response, list, "留言数据");
    }

    /**
     * 获取留言详细信息
     */
//    @PreAuthorize("@ss.hasPermi('manage:messages:query')")
    @GetMapping(value = "/{messageId}")
    public AjaxResult getInfo(@PathVariable("messageId") Long messageId)
    {
        return success(messagesService.selectMessagesByMessageId(messageId));
    }

    /**
     * 新增留言
     */
//    @PreAuthorize("@ss.hasPermi('manage:messages:add')")
    @Log(title = "留言", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Messages messages)
    {
        return toAjax(messagesService.insertMessages(messages));
    }

    /**
     * 修改留言
     */
//    @PreAuthorize("@ss.hasPermi('manage:messages:edit')")
    @Log(title = "留言", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Messages messages)
    {
        return toAjax(messagesService.updateMessages(messages));
    }

    /**
     * 删除留言
     */
//    @PreAuthorize("@ss.hasPermi('manage:messages:remove')")
    @Log(title = "留言", businessType = BusinessType.DELETE)
	@DeleteMapping("/{messageIds}")
    public AjaxResult remove(@PathVariable Long[] messageIds)
    {
        return toAjax(messagesService.deleteMessagesByMessageIds(messageIds));
    }
}
