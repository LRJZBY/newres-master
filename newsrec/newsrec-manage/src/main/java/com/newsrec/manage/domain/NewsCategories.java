package com.newsrec.manage.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.newsrec.common.annotation.Excel;
import com.newsrec.common.core.domain.BaseEntity;

/**
 * 新闻分类对象 tb_news_categories
 *
 * @author weiyan
 * @date 2025-02-20
 */
public class NewsCategories extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分类ID（主键，自增） */
    private Long categoryId;

    /** 分类名称（如“科技”、“体育”） */
    @Excel(name = "分类名称", readConverterExp = "如“科技”、“体育”")
    private String categoryName;

    /** 分类图标（图片URL或图标名称） */
    @Excel(name = "分类图标", readConverterExp = "图片URL或图标名称")
    private String categoryIcon;

    //新闻分类描述（如“科技新闻”）
    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }
    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName()
    {
        return categoryName;
    }
    public void setCategoryIcon(String categoryIcon)
    {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryIcon()
    {
        return categoryIcon;
    }

    //新闻分类下的新闻数量（添加的）
    private int newsTotal;
    public int getNewsTotal() {
        return newsTotal;
    }
    public void setNewsTotal(int newsTotal) {
        this.newsTotal = newsTotal;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("categoryIcon", getCategoryIcon())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
                .append("description", getDescription())
            .toString();
    }
}
