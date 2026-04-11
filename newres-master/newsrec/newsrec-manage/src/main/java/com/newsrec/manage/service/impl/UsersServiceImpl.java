package com.newsrec.manage.service.impl;

import java.util.List;
import com.newsrec.common.utils.DateUtils;
import com.newsrec.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.newsrec.manage.mapper.UsersMapper;
import com.newsrec.manage.domain.Users;
import com.newsrec.manage.service.IUsersService;

import javax.annotation.Resource;

/**
 * 用户Service业务层处理
 *
 * @author weiyan
 * @date 2025-02-20
 */
@Service
public class UsersServiceImpl implements IUsersService
{
    @Resource

    private UsersMapper usersMapper;

    @Override
    public  Users selectUserByUserName(String userName){
        return usersMapper.selectUserByUserName(userName);
    }


    @Override
    //统计用户数量
    public Integer getUserTotal() {return usersMapper.countUsers();}

    /**
     * 查询用户
     *
     * @param userId 用户主键
     * @return 用户
     */
    @Override
    public Users selectUsersByUserId(Long userId)
    {
        return usersMapper.selectUsersByUserId(userId);
    }

    /**
     * 查询用户列表
     *
     * @param users 用户
     * @return 用户
     */
    @Override
    public List<Users> selectUsersList(Users users)
    {
        return usersMapper.selectUsersList(users);
    }

    /**
     * 新增用户
     *
     * @param users 用户
     * @return 结果
     */
    @Override
    public int insertUsers(Users users)
    {
        // 确保只在未加密时才加密密码，避免重复加密
        if(users.getPassword() != null && !users.getPassword().startsWith("$2a$")) {
            users.setPassword(SecurityUtils.encryptPassword(users.getPassword()));
        }
        users.setCreateTime(DateUtils.getNowDate());
        return usersMapper.insertUsers(users);
    }

    /**
     * 修改用户
     *
     * @param users 用户
     * @return 结果
     */
    @Override
    public int updateUsers(Users users)
    {
        users.setUpdateTime(DateUtils.getNowDate());
        return usersMapper.updateUsers(users);
    }

    /**
     * 批量删除用户
     *
     * @param userIds 需要删除的用户主键
     * @return 结果
     */
    @Override
    public int deleteUsersByUserIds(Long[] userIds)
    {
        return usersMapper.deleteUsersByUserIds(userIds);
    }

    /**
     * 删除用户信息
     *
     * @param userId 用户主键
     * @return 结果
     */
    @Override
    public int deleteUsersByUserId(Long userId)
    {
        return usersMapper.deleteUsersByUserId(userId);
    }
}
