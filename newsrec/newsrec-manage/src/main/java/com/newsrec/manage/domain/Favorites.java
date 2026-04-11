package com.newsrec.manage.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.newsrec.common.annotation.Excel;
import com.newsrec.common.core.domain.BaseEntity;

/**
 * 收藏对象 tb_favorites
 *
 * @author weiyan
 * @date 2025-03-25
 */
public class Favorites extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 收藏记录ID（主键，自增） */
    private Long favoriteId;

    /** 用户ID（外键） */
    @Excel(name = "用户ID", readConverterExp = "外=键")
    private Long userId;

    /** 新闻ID（外键） */
    @Excel(name = "新闻ID", readConverterExp = "外=键")
    private Long newsId;


    // 新闻标题
    private String title;

    //新闻图片
    private String img;

    // 新闻内容
    private String content;

    //新闻分类
    private String categoryName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setFavoriteId(Long favoriteId)
    {
        this.favoriteId = favoriteId;
    }

    public Long getFavoriteId()
    {
        return favoriteId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setNewsId(Long newsId)
    {
        this.newsId = newsId;
    }

    public Long getNewsId()
    {
        return newsId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("favoriteId", getFavoriteId())
            .append("userId", getUserId())
            .append("newsId", getNewsId())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
