package com.newsrec.manage.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.newsrec.common.annotation.Excel;
import com.newsrec.common.core.domain.BaseEntity;

/**
 * 问题反馈对象 tb_feedback
 *
 * @author weiyan
 * @date 2025-03-17
 */
public class Feedback extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 反馈ID（主键，自增） */
    private Long feedbackId;

    /** 用户ID（外键） */
    @Excel(name = "用户ID", readConverterExp = "外=键")
    private Long userId;

    /** 反馈类型（如“功能建议”、“Bug报告”、“其他”） */
    @Excel(name = "反馈类型", readConverterExp = "如=“功能建议”、“Bug报告”、“其他”")
    private String feedbackType;

    /** 反馈内容 */
    @Excel(name = "反馈内容")
    private String content;

    /** 状态（0: 待回复,1:已回复） */
    @Excel(name = "状态", readConverterExp = "0=:,待=回复,1:已回复")
    private Long status;

    /** 管理员回复内容 */
    @Excel(name = "管理员回复内容")
    private String reply;

    //反馈问题的用户名称
    private String userName;

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }


    public void setFeedbackId(Long feedbackId)
    {
        this.feedbackId = feedbackId;
    }

    public Long getFeedbackId()
    {
        return feedbackId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setFeedbackType(String feedbackType)
    {
        this.feedbackType = feedbackType;
    }

    public String getFeedbackType()
    {
        return feedbackType;
    }
    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }
    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }
    public void setReply(String reply)
    {
        this.reply = reply;
    }

    public String getReply()
    {
        return reply;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("feedbackId", getFeedbackId())
            .append("userId", getUserId())
            .append("feedbackType", getFeedbackType())
            .append("content", getContent())
            .append("status", getStatus())
            .append("reply", getReply())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
