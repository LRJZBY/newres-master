package com.newsrec.manage.controller;


import com.newsrec.common.core.controller.BaseController;
import com.newsrec.common.core.domain.AjaxResult;
import com.newsrec.common.core.page.TableDataInfo;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.manage.domain.Likes;
import com.newsrec.manage.service.ILikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 点赞Controller
 *
 * @author weiyan
 * @date 2025-03-25
 */
@RestController
@RequestMapping("/manage/likes")
public class LikesController extends BaseController
{
    @Autowired
    private ILikesService likesService;

    /**
     * 查询点赞列表
     */

    @GetMapping("/list")
    public TableDataInfo list(Likes likes)
    {
        startPage();
        List<Likes> list = likesService.selectLikesList(likes);
        return getDataTable(list);
    }

    /**
     * 导出点赞列表
     */

    @PostMapping("/export")
    public void export(HttpServletResponse response, Likes likes)
    {
        List<Likes> list = likesService.selectLikesList(likes);
        ExcelUtil<Likes> util = new ExcelUtil<Likes>(Likes.class);
        util.exportExcel(response, list, "点赞数据");
    }

    /**
     * 获取点赞详细信息
     */

    @GetMapping(value = "/{likeId}")
    public AjaxResult getInfo(@PathVariable("likeId") Long likeId)
    {
        return success(likesService.selectLikesByLikeId(likeId));
    }

    /**
     * 新增点赞
     */
    @PostMapping
    public AjaxResult add(@RequestBody Likes likes)
    {
        return toAjax(likesService.insertLikes(likes));
    }

    /**
     * 修改点赞
     */
    @PutMapping
    public AjaxResult edit(@RequestBody Likes likes)
    {
        return toAjax(likesService.updateLikes(likes));
    }

    /**
     * 删除点赞
     */
	@DeleteMapping("/{likeIds}")
    public AjaxResult remove(@PathVariable Long[] likeIds)
    {
        return toAjax(likesService.deleteLikesByLikeIds(likeIds));
    }
}
