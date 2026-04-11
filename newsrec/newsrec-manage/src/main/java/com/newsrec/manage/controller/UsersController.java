package com.newsrec.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.newsrec.common.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.newsrec.common.annotation.Log;
import com.newsrec.common.core.controller.BaseController;
import com.newsrec.common.core.domain.AjaxResult;
import com.newsrec.common.enums.BusinessType;
import com.newsrec.manage.domain.Users;
import com.newsrec.manage.service.IUsersService;
import com.newsrec.common.utils.poi.ExcelUtil;
import com.newsrec.common.core.page.TableDataInfo;

/**
 * 用户Controller
 *
 * @author weiyan
 * @date 2025-02-20
 */
@RestController
@RequestMapping("/manage/users")
public class UsersController extends BaseController
{
    @Autowired
    private IUsersService usersService;

    /**
     * 查询用户列表
     */
//    @PreAuthorize("@ss.hasPermi('manage:users:list')")
    @GetMapping("/list")
    public TableDataInfo list(Users users)
    {
        startPage();
        List<Users> list = usersService.selectUsersList(users);
        return getDataTable(list);
    }

    /**
     * 导出用户列表
     */
    @PreAuthorize("@ss.hasPermi('manage:users:export')")
    @Log(title = "用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Users users)
    {
        List<Users> list = usersService.selectUsersList(users);
        ExcelUtil<Users> util = new ExcelUtil<Users>(Users.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 获取用户详细信息
     */
//    @PreAuthorize("@ss.hasPermi('manage:users:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(usersService.selectUsersByUserId(userId));
    }

    /**
     * 新增用户
     */
//    @PreAuthorize("@ss.hasPermi('manage:users:add')")
    @Log(title = "用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Users users)
    {
        return toAjax(usersService.insertUsers(users));
    }

    /**
     * 修改用户
     */
//    @PreAuthorize("@ss.hasPermi('manage:users:edit')")
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Users users)
    {
        return toAjax(usersService.updateUsers(users));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('manage:users:remove')")
    @Log(title = "用户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(usersService.deleteUsersByUserIds(userIds));
    }

    /**
     * 重置用户密码，加密123456
     */
//    @PreAuthorize("@ss.hasPermi('manage:users:edit')")
    @Log(title = "重置用户密码", businessType = BusinessType.UPDATE)
    @PutMapping("resetPwd/{userId}")
    public AjaxResult resetPwd(@PathVariable Long userId)
    {
        Users user = new Users();
        user.setUserId(userId);
        user.setPassword(SecurityUtils.encryptPassword("123456"));
        return toAjax(usersService.updateUsers(user));
    }


    //用户前台重置密码
    @PutMapping("resetPwd1/{userName}")
    public AjaxResult resetPwd(@PathVariable String userName)
    {
        // 1. 先查询用户是否存在
        Users existingUser = usersService.selectUserByUserName(userName);
        if (existingUser == null) {
            return error("用户不存在");
        }
        existingUser.setPassword(SecurityUtils.encryptPassword("123456"));
        return toAjax(usersService.updateUsers(existingUser));
    }
}
