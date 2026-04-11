package com.newsrec.manage.domain.vo;

import lombok.Data;

@Data
public class RegisterReqVO {
    private String username;
    private String password;
    private String email;
}