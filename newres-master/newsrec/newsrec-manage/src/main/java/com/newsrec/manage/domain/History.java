package com.newsrec.manage.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.newsrec.common.annotation.Excel;
import com.newsrec.common.core.domain.BaseEntity;

/**
 * 浏览历史对象 tb_history
 *
 * @author weiyan
 * @date 2025-02-20
 */
public class History extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 浏览记录ID（主键，自增） */
    private Long historyId;

    /** 用户ID（外键） */
    @Excel(name = "用户ID", readConverterExp = "外=键")
    private Long userId;

    /** 新闻ID（外键） */
    @Excel(name = "新闻ID", readConverterExp = "外=键")
    private Long newsId;

    private String img;
    public void setImg(String img) {
        this.img = img;
    }
    public String getImg() {
        return img;
    }

    /** 浏览时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "浏览时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date viewedAt;

    public void setHistoryId(Long historyId)
    {
        this.historyId = historyId;
    }

    public Long getHistoryId()
    {
        return historyId;
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
    public void setViewedAt(Date viewedAt)
    {
        this.viewedAt = viewedAt;
    }

    public Date getViewedAt()
    {
        return viewedAt;
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
            .append("historyId", getHistoryId())
            .append("userId", getUserId())
            .append("newsId", getNewsId())
            .append("viewedAt", getViewedAt())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
