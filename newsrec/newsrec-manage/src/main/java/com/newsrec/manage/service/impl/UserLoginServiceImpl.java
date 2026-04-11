package com.newsrec.manage.service.impl;


import cn.hutool.jwt.JWTUtil;
import com.newsrec.common.exception.ServiceException;
import com.newsrec.common.utils.MessageUtils;
import com.newsrec.manage.domain.Users;
import com.newsrec.manage.service.UserLoginService;
import com.newsrec.manage.service.IUsersService;
import com.newsrec.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private IUsersService usersService;

    @Override
    public String login(String username, String password) {
        Users user = usersService.selectUserByUserName(username);
        if (user == null || !SecurityUtils.matchesPassword(password, user.getPassword())) {
            throw new ServiceException(MessageUtils.message("user.password.not.match"));
        }
        Map<String,Object> payload = new HashMap<>();
         payload.put("userId", user.getUserId());
         payload.put("username", username);
         return JWTUtil.createToken(payload, "your-secret-key".getBytes());
    }
}
