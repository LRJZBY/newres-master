package com.newsrec.manage.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.newsrec.common.annotation.Excel;
import com.newsrec.common.core.domain.BaseEntity;

/**
 * 新闻对象 tb_news
 *
 * @author weiyan
 * @date 2025-03-03
 */

public class News extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 新闻ID（主键，自增） */
    private Long newsId;

    /** 新闻标题 */
    @Excel(name = "新闻标题")
    private String title;

    /** 新闻内容 */
    @Excel(name = "新闻内容")
    private String content;

    /** 所属分类ID（外键） */
    @Excel(name = "所属分类ID", readConverterExp = "外=键")
    private Long categoryId;

    /** 发布人id */
    @Excel(name = "发布人id")
    private Long userId;

    /** 新闻状态（0: 待审核, 1: 审核通过, 2: 审核不通过） */
    @Excel(name = "新闻状态", readConverterExp = "0=:,待=审核,,1=:,审=核通过,,2=:,审=核不通过")
    private Long status;

    /** 新闻缩放图 */
    @Excel(name = "新闻缩放图")
    private String img;

    public void setNewsId(Long newsId)
    {
        this.newsId = newsId;
    }

    public Long getNewsId()
    {
        return newsId;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }
    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }
    public void setImg(String img)
    {
        this.img = img;
    }

    public String getImg()
    {
        return img;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("newsId", getNewsId())
                .append("title", getTitle())
                .append("content", getContent())
                .append("categoryId", getCategoryId())
                .append("userId", getUserId())
                .append("status", getStatus())
                .append("remark", getRemark())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("createBy", getCreateBy())
                .append("updateBy", getUpdateBy())
                .append("img", getImg())
                .toString();
    }
}
