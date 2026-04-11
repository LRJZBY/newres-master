package com.newsrec.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.newsrec.manage.domain.vo.CommentsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.newsrec.common.core.controller.BaseController;
import com.newsrec.common.core.domain.AjaxResult;
import com.newsrec.manage.domain.Comments;
import com.newsrec.manage.service.ICommentsService;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.common.core.page.TableDataInfo;

/**
 * 评论Controller
 *
 * @author weiyan
 * @date 2025-02-20
 */
@RestController
@RequestMapping("/manage/comments")
public class CommentsController extends BaseController
{
    @Autowired
    private ICommentsService commentsService;

    /**
     * 查询评论列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Comments comments)
    {
        startPage();
        List<CommentsVo> list = commentsService.selectCommentsVoList(comments);
        return getDataTable(list);
    }

    /**
     * 导出评论列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, Comments comments)
    {
        List<Comments> list = commentsService.selectCommentsList(comments);
        ExcelUtil<Comments> util = new ExcelUtil<Comments>(Comments.class);
        util.exportExcel(response, list, "评论数据");
    }

    /**
     * 获取评论详细信息
     */
    @GetMapping(value = "/{commentId}")
    public AjaxResult getInfo(@PathVariable("commentId") Long commentId)
    {
        return success(commentsService.selectCommentsByCommentId(commentId));
    }

    /**
     * 新增评论
     */
    @PostMapping
    public AjaxResult add(@RequestBody Comments comments)
    {
        return toAjax(commentsService.insertComments(comments));
    }

    /**
     * 修改评论
     */
    @PutMapping
    public AjaxResult edit(@RequestBody Comments comments)
    {
        return toAjax(commentsService.updateComments(comments));
    }

    /**
     * 删除评论
     */
	@DeleteMapping("/{commentIds}")
    public AjaxResult remove(@PathVariable Long[] commentIds)
    {
        return toAjax(commentsService.deleteCommentsByCommentIds(commentIds));
    }
}
