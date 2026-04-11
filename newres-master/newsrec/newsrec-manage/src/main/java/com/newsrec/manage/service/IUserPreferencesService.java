package com.newsrec.manage.service;

/**
 * 功能
 * 作者：严伟
 * 日期：2025/3/31 17:16
 **/

import java.util.List;
import com.newsrec.manage.domain.UserPreferences;

/**
 * 用户偏好Service接口
 *
 * @author weiyan
 * @date 2025-03-31
 */
public interface IUserPreferencesService
{
    /**
     * 查询用户偏好
     *
     * @param preferenceId 用户偏好主键
     * @return 用户偏好
     */
    public UserPreferences selectUserPreferencesByPreferenceId(Long preferenceId);

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
     * 批量删除用户偏好
     *
     * @param preferenceIds 需要删除的用户偏好主键集合
     * @return 结果
     */
    public int deleteUserPreferencesByPreferenceIds(Long[] preferenceIds);

    /**
     * 删除用户偏好信息
     *
     * @param preferenceId 用户偏好主键
     * @return 结果
     */
    public int deleteUserPreferencesByPreferenceId(Long preferenceId);
}

