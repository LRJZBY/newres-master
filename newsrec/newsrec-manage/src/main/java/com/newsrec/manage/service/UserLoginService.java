package com.newsrec.manage.service;

import com.newsrec.manage.domain.Users;

public interface UserLoginService {
    /**
     * 用户登录认证
     */
    String login(String username, String password);
}