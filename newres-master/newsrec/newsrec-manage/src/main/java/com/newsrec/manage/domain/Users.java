package com.newsrec.manage.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.newsrec.common.annotation.Excel;
import com.newsrec.common.core.domain.BaseEntity;

/**
 * 用户对象 tb_users
 *
 * @author weiyan
 * @date 2025-02-20
 */
public class Users extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID（主键，自增） */
    private Long userId;

    /** 用户名 */
    @Excel(name = "用户名")
    private String username;

    /** 密码（加密存储） */
    private String password;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    /** 用户头像（图片URL或文件路径） */
    @Excel(name = "用户头像", readConverterExp = "图=片URL或文件路径")
    private String avatar;


    private String token;
    public String getToken() {
		return token;
	}
    public void setToken(String token) {
		this.token = token;
	}

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getEmail()
    {
        return email;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPhone()
    {
        return phone;
    }
    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("username", getUsername())
            .append("password", getPassword())
            .append("email", getEmail())
            .append("phone", getPhone())
            .append("avatar", getAvatar())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
