package com.newsrec.manage.service.impl;

import java.util.List;
import com.newsrec.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.newsrec.manage.mapper.FeedbackMapper;
import com.newsrec.manage.domain.Feedback;
import com.newsrec.manage.service.IFeedbackService;

import javax.annotation.Resource;

/**
 * 问题反馈Service业务层处理
 *
 * @author weiyan
 * @date 2025-03-17
 */
@Service
public class FeedbackServiceImpl implements IFeedbackService
{
    @Resource
    private FeedbackMapper feedbackMapper;

    @Override
    public Integer countPendingFeedback()
    {
        return feedbackMapper.countPendingFeedback();
    }


    /**
     * 查询问题反馈
     *
     * @param feedbackId 问题反馈主键
     * @return 问题反馈
     */
    @Override
    public Feedback selectFeedbackByFeedbackId(Long feedbackId)
    {
        return feedbackMapper.selectFeedbackByFeedbackId(feedbackId);
    }

    /**
     * 查询问题反馈列表
     *
     * @param feedback 问题反馈
     * @return 问题反馈
     */
    @Override
    public List<Feedback> selectFeedbackList(Feedback feedback)
    {
        return feedbackMapper.selectFeedbackList(feedback);
    }

    /**
     * 新增问题反馈
     *
     * @param feedback 问题反馈
     * @return 结果
     */
    @Override
    public int insertFeedback(Feedback feedback)
    {
        feedback.setCreateTime(DateUtils.getNowDate());
        return feedbackMapper.insertFeedback(feedback);
    }

    /**
     * 修改问题反馈
     *
     * @param feedback 问题反馈
     * @return 结果
     */
    @Override
    public int updateFeedback(Feedback feedback)
    {
        feedback.setUpdateTime(DateUtils.getNowDate());
        return feedbackMapper.updateFeedback(feedback);
    }

    /**
     * 批量删除问题反馈
     *
     * @param feedbackIds 需要删除的问题反馈主键
     * @return 结果
     */
    @Override
    public int deleteFeedbackByFeedbackIds(Long[] feedbackIds)
    {
        return feedbackMapper.deleteFeedbackByFeedbackIds(feedbackIds);
    }

    /**
     * 删除问题反馈信息
     *
     * @param feedbackId 问题反馈主键
     * @return 结果
     */
    @Override
    public int deleteFeedbackByFeedbackId(Long feedbackId)
    {
        return feedbackMapper.deleteFeedbackByFeedbackId(feedbackId);
    }
}
