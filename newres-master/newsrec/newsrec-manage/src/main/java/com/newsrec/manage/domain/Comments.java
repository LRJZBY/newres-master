package com.newsrec.manage.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.newsrec.common.annotation.Excel;
import com.newsrec.common.core.domain.BaseEntity;

/**
 * 评论对象 tb_comments
 *
 * @author weiyan
 * @date 2025-02-20
 */
public class Comments extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID（主键，自增） */
    private Long commentId;

    /** 用户ID（外键） */
    @Excel(name = "用户ID", readConverterExp = "外=键")
    private Long userId;

    /** 新闻ID（外键） */
    @Excel(name = "新闻ID", readConverterExp = "外=键")
    private Long newsId;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    //父级评论
    private int parentId;

    public int getParentId() {
        return parentId;
    }
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setCommentId(Long commentId)
    {
        this.commentId = commentId;
    }

    public Long getCommentId()
    {
        return commentId;
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
    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }


    //用于多表查询的字段：
    private String username;
    private String title;
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("commentId", getCommentId())
            .append("userId", getUserId())
            .append("newsId", getNewsId())
            .append("content", getContent())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
