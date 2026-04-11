package com.newsrec.manage.service.impl;

/**
 * 功能
 * 作者：严伟
 * 日期：2025/3/31 17:17
 **/


import java.util.List;
import com.newsrec.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.newsrec.manage.mapper.UserPreferencesMapper;
import com.newsrec.manage.domain.UserPreferences;
import com.newsrec.manage.service.IUserPreferencesService;

import javax.annotation.Resource;

/**
 * 用户偏好Service业务层处理
 *
 * @author weiyan
 * @date 2025-03-31
 */
@Service
public class UserPreferencesServiceImpl implements IUserPreferencesService
{
    @Resource
    private UserPreferencesMapper userPreferencesMapper;

    /**
     * 查询用户偏好
     *
     * @param preferenceId 用户偏好主键
     * @return 用户偏好
     */
    @Override
    public UserPreferences selectUserPreferencesByPreferenceId(Long preferenceId)
    {
        return userPreferencesMapper.selectUserPreferencesByPreferenceId(preferenceId);
    }

    /**
     * 查询用户偏好列表
     *
     * @param userPreferences 用户偏好
     * @return 用户偏好
     */
    @Override
    public List<UserPreferences> selectUserPreferencesList(UserPreferences userPreferences)
    {
        return userPreferencesMapper.selectUserPreferencesList(userPreferences);
    }

    /**
     * 新增用户偏好
     *
     * @param userPreferences 用户偏好
     * @return 结果
     */
    @Override
    public int insertUserPreferences(UserPreferences userPreferences)
    {
        userPreferences.setCreateTime(DateUtils.getNowDate());
        return userPreferencesMapper.insertUserPreferences(userPreferences);
    }

    /**
     * 修改用户偏好
     *
     * @param userPreferences 用户偏好
     * @return 结果
     */
    @Override
    public int updateUserPreferences(UserPreferences userPreferences)
    {
        userPreferences.setUpdateTime(DateUtils.getNowDate());
        return userPreferencesMapper.updateUserPreferences(userPreferences);
    }

    /**
     * 批量删除用户偏好
     *
     * @param preferenceIds 需要删除的用户偏好主键
     * @return 结果
     */
    @Override
    public int deleteUserPreferencesByPreferenceIds(Long[] preferenceIds)
    {
        return userPreferencesMapper.deleteUserPreferencesByPreferenceIds(preferenceIds);
    }

    /**
     * 删除用户偏好信息
     *
     * @param preferenceId 用户偏好主键
     * @return 结果
     */
    @Override
    public int deleteUserPreferencesByPreferenceId(Long preferenceId)
    {
        return userPreferencesMapper.deleteUserPreferencesByPreferenceId(preferenceId);
    }
}
