package com.newsrec.manage.service.impl;


import com.newsrec.common.utils.DateUtils;
import com.newsrec.manage.domain.Favorites;
import com.newsrec.manage.mapper.FavoritesMapper;
import com.newsrec.manage.service.IFavoritesService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收藏Service业务层处理
 *
 * @author weiyan
 * @date 2025-03-25
 */
@Service
public class FavoritesServiceImpl implements IFavoritesService
{
    @Resource
    private FavoritesMapper favoritesMapper;

    @Override
    public int selectFavoritesByUserId(Long userId)
    {
        return favoritesMapper.selectFavoritesByUserId(userId);
    }

    /**
     * 查询收藏
     *
     * @param favoriteId 收藏主键
     * @return 收藏
     */
    @Override
    public Favorites selectFavoritesByFavoriteId(Long favoriteId)
    {
        return favoritesMapper.selectFavoritesByFavoriteId(favoriteId);
    }

    /**
     * 查询收藏列表
     *
     * @param favorites 收藏
     * @return 收藏
     */
    @Override
    public List<Favorites> selectFavoritesList(Favorites favorites)
    {
        return favoritesMapper.selectFavoritesList(favorites);
    }

    /**
     * 新增收藏
     *
     * @param favorites 收藏
     * @return 结果
     */
    @Override
    public int insertFavorites(Favorites favorites)
    {
        favorites.setCreateTime(DateUtils.getNowDate());
        return favoritesMapper.insertFavorites(favorites);
    }

    /**
     * 修改收藏
     *
     * @param favorites 收藏
     * @return 结果
     */
    @Override
    public int updateFavorites(Favorites favorites)
    {
        favorites.setUpdateTime(DateUtils.getNowDate());
        return favoritesMapper.updateFavorites(favorites);
    }

    /**
     * 批量删除收藏
     *
     * @param favoriteIds 需要删除的收藏主键
     * @return 结果
     */
    @Override
    public int deleteFavoritesByFavoriteIds(Long[] favoriteIds)
    {
        return favoritesMapper.deleteFavoritesByFavoriteIds(favoriteIds);
    }

    /**
     * 删除收藏信息
     *
     * @param favoriteId 收藏主键
     * @return 结果
     */
    @Override
    public int deleteFavoritesByFavoriteId(Long favoriteId)
    {
        return favoritesMapper.deleteFavoritesByFavoriteId(favoriteId);
    }
}
