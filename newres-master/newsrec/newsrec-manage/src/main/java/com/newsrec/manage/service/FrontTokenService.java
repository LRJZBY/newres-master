package com.newsrec.manage.service;

import com.newsrec.common.core.redis.RedisCache;
import com.newsrec.manage.domain.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class FrontTokenService {
    @Value("${token.secret}")
    private String secret;
    
    @Value("${token.expireTime}")
    private int expireTime;
    
    @Autowired
    private RedisCache redisCache;
    
    public String createToken(Users user) {
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getUserId())
                .claim("userType", "front")  // 标识前台用户
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireTime * 60 * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
                
        // 将用户信息存入Redis
        String userKey = "front_token:" + token;
        redisCache.setCacheObject(userKey, user, expireTime, TimeUnit.MINUTES);
        
        return token;
    }
    
    public Users getLoginUser(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
                    
            String userKey = "front_token:" + token;
            return redisCache.getCacheObject(userKey);
        } catch (Exception e) {
            return null;
        }
    }
    
    public void deleteToken(String token) {
        String userKey = "front_token:" + token;
        redisCache.deleteObject(userKey);
    }
} 