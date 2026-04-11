package com.newsrec.manage.mapper;


import com.newsrec.manage.domain.Favorites;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 收藏Mapper接口
 *
 * @author weiyan
 * @date 2025-03-25
 */
public interface FavoritesMapper
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
     * 删除收藏
     *
     * @param favoriteId 收藏主键
     * @return 结果
     */
    public int deleteFavoritesByFavoriteId(Long favoriteId);

    /**
     * 批量删除收藏
     *
     * @param favoriteIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFavoritesByFavoriteIds(Long[] favoriteIds);

    //根据用户id，查询收藏数量
    @Select("select count(*) from tb_favorites where user_id = #{userId}")
    public int selectFavoritesByUserId(Long userId);
}
