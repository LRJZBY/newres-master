package com.newsrec.manage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.newsrec.common.annotation.Excel;
import com.newsrec.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 留言对象 tb_messages
 *
 * @author weiyan
 * @date 2025-03-17
 */
@Data
public class Messages extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 留言ID（主键，自增） */
    private Long messageId;

    /** 用户ID（外键） */
    @Excel(name = "用户ID", readConverterExp = "外=键")
    private Long userId;

    /** 父级留言ID（用于多级评论，NULL表示顶级留言） */
    private Long parentId;

    /** 留言内容 */
    @Excel(name = "留言内容")
    private String content;

    private String avatar;

    private String username;

    @JsonIgnore // 避免循环引用
    private Users user;

    private List<Messages> children; // 子留言列表


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("messageId", getMessageId())
            .append("userId", getUserId())
            .append("parentId", getParentId())
            .append("content", getContent())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
