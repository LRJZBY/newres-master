package com.newsrec.manage.mapper;

import java.util.List;
import com.newsrec.manage.domain.Feedback;
import org.apache.ibatis.annotations.Select;

/**
 * 问题反馈Mapper接口
 *
 * @author weiyan
 * @date 2025-03-17
 */
public interface FeedbackMapper
{

    //待回复的问题反馈数量
    @Select("SELECT COUNT(*) FROM tb_feedback WHERE status = 0")
    Integer countPendingFeedback();


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
     * 删除问题反馈
     *
     * @param feedbackId 问题反馈主键
     * @return 结果
     */
    public int deleteFeedbackByFeedbackId(Long feedbackId);

    /**
     * 批量删除问题反馈
     *
     * @param feedbackIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFeedbackByFeedbackIds(Long[] feedbackIds);
}
