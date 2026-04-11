package com.newsrec.manage.domain;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.newsrec.common.annotation.Excel;
import com.newsrec.common.core.domain.BaseEntity;

/**
 * 用户偏好对象 tb_user_preferences
 *
 * @author weiyan
 * @date 2025-03-31
 */
public class UserPreferences extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 偏好记录ID（主键，自增） */
    private Long preferenceId;

    /** 用户ID（外键） */
    @Excel(name = "用户ID", readConverterExp = "外=键")
    private Long userId;

    /** 分类ID（外键） */
    @Excel(name = "分类ID", readConverterExp = "外=键")
    private Long categoryId;

    /** 优先级（1、2、3，1为最高优先级） */
    @Excel(name = "优先级", readConverterExp = "1,2,3;其中1为最高优先级")
    private Long priority;

    public void setPreferenceId(Long preferenceId)
    {
        this.preferenceId = preferenceId;
    }

    public Long getPreferenceId()
    {
        return preferenceId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }
    public void setPriority(Long priority)
    {
        this.priority = priority;
    }

    public Long getPriority()
    {
        return priority;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("preferenceId", getPreferenceId())
                .append("userId", getUserId())
                .append("categoryId", getCategoryId())
                .append("priority", getPriority())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
