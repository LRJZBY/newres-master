package com.newsrec.manage.controller;


import com.newsrec.common.core.controller.BaseController;
import com.newsrec.common.core.domain.AjaxResult;
import com.newsrec.common.core.page.TableDataInfo;
import com.newsrec.common.enums.BusinessType;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.manage.domain.Favorites;
import com.newsrec.manage.service.IFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 收藏Controller
 *
 * @author weiyan
 * @date 2025-03-25
 */
@RestController
@RequestMapping("/manage/favorites")
public class FavoritesController extends BaseController
{
    @Autowired
    private IFavoritesService favoritesService;

    /**
     * 查询收藏列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Favorites favorites)
    {
        startPage();
        List<Favorites> list = favoritesService.selectFavoritesList(favorites);
        return getDataTable(list);
    }

    /**
     * 导出收藏列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, Favorites favorites)
    {
        List<Favorites> list = favoritesService.selectFavoritesList(favorites);
        ExcelUtil<Favorites> util = new ExcelUtil<Favorites>(Favorites.class);
        util.exportExcel(response, list, "收藏数据");
    }

    /**
     * 获取收藏详细信息
     */
    @GetMapping(value = "/{favoriteId}")
    public AjaxResult getInfo(@PathVariable("favoriteId") Long favoriteId)
    {
        return success(favoritesService.selectFavoritesByFavoriteId(favoriteId));
    }

    /**
     * 新增收藏
     */
    @PostMapping
    public AjaxResult add(@RequestBody Favorites favorites)
    {
        return toAjax(favoritesService.insertFavorites(favorites));
    }

    /**
     * 修改收藏
     */
    @PutMapping
    public AjaxResult edit(@RequestBody Favorites favorites)
    {
        return toAjax(favoritesService.updateFavorites(favorites));
    }

    /**
     * 删除收藏
     */
	@DeleteMapping("/{favoriteIds}")
    public AjaxResult remove(@PathVariable Long[] favoriteIds)
    {
        return toAjax(favoritesService.deleteFavoritesByFavoriteIds(favoriteIds));
    }
}
