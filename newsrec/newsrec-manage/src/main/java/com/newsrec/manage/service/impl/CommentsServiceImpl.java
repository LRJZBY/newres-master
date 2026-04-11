package com.newsrec.manage.service.impl;

import java.util.List;
import com.newsrec.common.utils.DateUtils;
import com.newsrec.manage.domain.vo.CommentsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.newsrec.manage.mapper.CommentsMapper;
import com.newsrec.manage.domain.Comments;
import com.newsrec.manage.service.ICommentsService;

/**
 * 评论Service业务层处理
 *
 * @author weiyan
 * @date 2025-02-20
 */
@Service
public class CommentsServiceImpl implements ICommentsService
{
    @Autowired
    private CommentsMapper commentsMapper;

     @Override
    public Integer getCommentTotal() {
        return commentsMapper.countComments();
    }
    /**
     * 查询评论
     *
     * @param commentId 评论主键
     * @return 评论
     */
    @Override
    public Comments selectCommentsByCommentId(Long commentId)
    {
        return commentsMapper.selectCommentsByCommentId(commentId);
    }

    /**
     * 查询评论列表
     *
     * @param comments 评论
     * @return 评论
     */
    @Override
    public List<Comments> selectCommentsList(Comments comments)
    {
        return commentsMapper.selectCommentsList(comments);
    }

    /**
     * 新增评论
     *
     * @param comments 评论
     * @return 结果
     */
    @Override
    public int insertComments(Comments comments)
    {
        comments.setCreateTime(DateUtils.getNowDate());
        return commentsMapper.insertComments(comments);
    }

    /**
     * 修改评论
     *
     * @param comments 评论
     * @return 结果
     */
    @Override
    public int updateComments(Comments comments)
    {
        comments.setUpdateTime(DateUtils.getNowDate());
        return commentsMapper.updateComments(comments);
    }

    /**
     * 批量删除评论
     *
     * @param commentIds 需要删除的评论主键
     * @return 结果
     */
    @Override
    public int deleteCommentsByCommentIds(Long[] commentIds)
    {
        return commentsMapper.deleteCommentsByCommentIds(commentIds);
    }

    /**
     * 删除评论信息
     *
     * @param commentId 评论主键
     * @return 结果
     */
    @Override
    public int deleteCommentsByCommentId(Long commentId)
    {
        return commentsMapper.deleteCommentsByCommentId(commentId);
    }


    /**
     * 查询评论列表Vo
     *
     * @param comments 评论
     * @return 评论集合
     */
    @Override
    public List<CommentsVo> selectCommentsVoList(Comments comments){
        return commentsMapper.selectCommentsVoList(comments);
    };
}
