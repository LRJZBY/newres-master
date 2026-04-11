package com.newsrec.manage.service;

import java.util.List;
import com.newsrec.manage.domain.Feedback;

/**
 * 问题反馈Service接口
 *
 * @author weiyan
 * @date 2025-03-17
 */
public interface IFeedbackService
{

    public Integer countPendingFeedback();

    /**
     * 查询问题反馈
     *
     * @param feedbackId 问题反馈主键
     * @return 问题反馈
     */
    public Feedback selectFeedbackByFeedbackId(Long feedbackId);

    /**
     * 查询问题反馈列表
     *
     * @param feedback 问题反馈
     * @return 问题反馈集合
     */
    public List<Feedback> selectFeedbackList(Feedback feedback);

    /**
     * 新增问题反馈
     *
     * @param feedback 问题反馈
     * @return 结果
     */
    public int insertFeedback(Feedback feedback);

    /**
     * 修改问题反馈
     *
     * @param feedback 问题反馈
     * @return 结果
     */
    public int updateFeedback(Feedback feedback);

    /**
     * 批量删除问题反馈
     *
     * @param feedbackIds 需要删除的问题反馈主键集合
     * @return 结果
     */
    public int deleteFeedbackByFeedbackIds(Long[] feedbackIds);

    /**
     * 删除问题反馈信息
     *
     * @param feedbackId 问题反馈主键
     * @return 结果
     */
    public int deleteFeedbackByFeedbackId(Long feedbackId);
}
