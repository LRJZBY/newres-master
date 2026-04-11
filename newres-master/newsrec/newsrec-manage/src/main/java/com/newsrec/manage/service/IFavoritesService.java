package com.newsrec.manage.service;


import com.newsrec.manage.domain.Favorites;

import java.util.List;

/**
 * 收藏Service接口
 *
 * @author weiyan
 * @date 2025-03-25
 */
public interface IFavoritesService
{
    /**
     * 查询收藏
     *
     * @param favoriteId 收藏主键
     * @return 收藏
     */
    public Favorites selectFavoritesByFavoriteId(Long favoriteId);

    /**
     * 查询收藏列表
     *
     * @param favorites 收藏
     * @return 收藏集合
     */
    public List<Favorites> selectFavoritesList(Favorites favorites);

    /**
     * 新增收藏
     *
     * @param favorites 收藏
     * @return 结果
     */
    public int insertFavorites(Favorites favorites);

    /**
     * 修改收藏
     *
     * @param favorites 收藏
     * @return 结果
     */
    public int updateFavorites(Favorites favorites);

    /**
     * 批量删除收藏
     *
     * @param favoriteIds 需要删除的收藏主键集合
     * @return 结果
     */
    public int deleteFavoritesByFavoriteIds(Long[] favoriteIds);

    /**
     * 删除收藏信息
     *
     * @param favoriteId 收藏主键
     * @return 结果
     */
    public int deleteFavoritesByFavoriteId(Long favoriteId);


    public int selectFavoritesByUserId(Long userId);
}
