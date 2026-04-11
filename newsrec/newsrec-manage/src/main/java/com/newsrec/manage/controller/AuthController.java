package com.newsrec.manage.controller;
import com.newsrec.common.core.domain.AjaxResult;
import com.newsrec.manage.domain.Users;
import com.newsrec.manage.domain.vo.LoginReqVO;
import com.newsrec.manage.domain.vo.RegisterReqVO;
import com.newsrec.manage.service.FrontTokenService;
import com.newsrec.manage.service.IUsersService;
import com.newsrec.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage/user")
public class AuthController {
    @Autowired
    private IUsersService usersService;

    @Autowired
    private FrontTokenService frontTokenService;

    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginReqVO loginReq) {
        // 根据用户名，搜索用户对象
        Users user = usersService.selectUserByUserName(loginReq.getUsername());
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }

        // 使用 SecurityUtils 工具类验证密码，保持一致的加密验证方式
        if (!SecurityUtils.matchesPassword(loginReq.getPassword(), user.getPassword())) {
            return AjaxResult.error("密码错误");
        }

        // 根据用户id和密码生成token
        String token = frontTokenService.createToken(user);
        user.setToken(token);
        return AjaxResult.success(user);
    }

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterReqVO registerReq) {
        if (usersService.selectUserByUserName(registerReq.getUsername()) != null) {
            return AjaxResult.error("用户名已存在");
        }
        Users newUser = new Users();
        newUser.setUsername(registerReq.getUsername());
        //密码加密
        newUser.setPassword(SecurityUtils.encryptPassword(registerReq.getPassword()));
        newUser.setEmail(registerReq.getEmail());
        usersService.insertUsers(newUser);
        return AjaxResult.success("注册成功");
    }
}
