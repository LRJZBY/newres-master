package com.newsrec.manage.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.newsrec.common.annotation.Excel;
import com.newsrec.common.core.domain.BaseEntity;

/**
 * 轮播图对象 tb_banner
 *
 * @author weiyan
 * @date 2025-02-20
 */

@Data
public class Banner extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 轮播图ID */
    private Long bannerId;

    /** 图片 */
    @Excel(name = "图片")
    private String img;

    /** 新闻ID */
    @Excel(name = "新闻ID")
    private Long newsId;

    //新闻分类描述（如“科技新闻”）
    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setBannerId(Long bannerId)
    {
        this.bannerId = bannerId;
    }

    public Long getBannerId()
    {
        return bannerId;
    }
    public void setImg(String img)
    {
        this.img = img;
    }

    public String getImg()
    {
        return img;
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
            .append("bannerId", getBannerId())
            .append("img", getImg())
            .append("newsId", getNewsId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
                .append("description", getDescription())
            .toString();
    }
}
