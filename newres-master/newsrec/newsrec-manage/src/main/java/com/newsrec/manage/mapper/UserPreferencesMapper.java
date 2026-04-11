package com.newsrec.manage.mapper;

/**
 * 功能
 * 作者：严伟
 * 日期：2025/3/31 17:15
 **/
import java.util.List;
import com.newsrec.manage.domain.UserPreferences;
import com.newsrec.manage.domain.vo.NewsVo;
import org.apache.ibatis.annotations.Param;

/**
 * 用户偏好Mapper接口
 *
 * @author weiyan
 * @date 2025-03-31
 */
public interface UserPreferencesMapper
{
    /**
     * 查询用户偏好
     *
     * @param preferenceId 用户偏好主键
     * @return 用户偏好
     */
    public UserPreferences selectUserPreferencesByPreferenceId(@Param("preferenceId") Long preferenceId);

    /**
     * 查询用户偏好列表
     *
     * @param userPreferences 用户偏好
     * @return 用户偏好集合
     */
    public List<UserPreferences> selectUserPreferencesList(UserPreferences userPreferences);

    /**
     * 新增用户偏好
     *
     * @param userPreferences 用户偏好
     * @return 结果
     */
    public int insertUserPreferences(UserPreferences userPreferences);

    /**
     * 修改用户偏好
     *
     * @param userPreferences 用户偏好
     * @return 结果
     */
    public int updateUserPreferences(UserPreferences userPreferences);

    /**
     * 删除用户偏好
     *
     * @param preferenceId 用户偏好主键
     * @return 结果
     */
    public int deleteUserPreferencesByPreferenceId(@Param("preferenceId") Long preferenceId);

    /**
     * 批量删除用户偏好
     *
     * @param preferenceIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserPreferencesByPreferenceIds(@Param("preferenceIds") Long[] preferenceIds);

    /**
     * 根据用户ID获取用户偏好
     *
     * @param userId 用户ID
     * @return 用户偏好列表
     */
    public List<UserPreferences> selectUserPreferencesByUserId(@Param("userId") Long userId);

    /**
     * 获取用户浏览过的新闻ID列表
     *
     * @param userId 用户ID
     * @return 新闻ID列表
     */
    public List<Long> selectUserViewedNewsIds(@Param("userId") Long userId);

    /**
     * 获取用户点赞过的新闻ID列表
     *
     * @param userId 用户ID
     * @return 新闻ID列表
     */
    public List<Long> selectUserLikedNewsIds(@Param("userId") Long userId);

    /**
     * 获取用户收藏过的新闻ID列表
     *
     * @param userId 用户ID
     * @return 新闻ID列表
     */
    public List<Long> selectUserFavoriteNewsIds(@Param("userId") Long userId);

    /**
     * 获取所有用户ID
     *
     * @return 用户ID列表
     */
    public List<Long> selectAllUserIds();

    /**
     * 根据分类ID获取新闻
     *
     * @param categoryId 分类ID
     * @param limit 限制数量
     * @return 新闻列表
     */
    public List<NewsVo> selectNewsByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit);
}
