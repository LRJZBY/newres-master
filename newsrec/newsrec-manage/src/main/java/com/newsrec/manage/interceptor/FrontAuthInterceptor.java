package com.newsrec.manage.interceptor;

import com.newsrec.common.utils.StringUtils;
import com.newsrec.manage.domain.Users;
import com.newsrec.manage.service.FrontTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class FrontAuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private FrontTokenService frontTokenService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 登录注册接口放行
        String uri = request.getRequestURI();
        if (uri.matches(".*/manage/user/(login|register)")) {
            return true;
        }
        
        // 获取token
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            response.setStatus(401);
            return false;
        }
        
        // 验证token
        Users user = frontTokenService.getLoginUser(token);
        if (user == null) {
            response.setStatus(401);
            return false;
        }
        
        return true;
    }
} 