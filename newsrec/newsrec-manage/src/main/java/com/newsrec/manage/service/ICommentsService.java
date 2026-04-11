package com.newsrec.manage.service;

import java.util.List;
import com.newsrec.manage.domain.Comments;
import com.newsrec.manage.domain.vo.CommentsVo;

/**
 * 评论Service接口
 *
 * @author weiyan
 * @date 2025-02-20
 */
public interface ICommentsService
{

    //评论条数
    public Integer getCommentTotal() ;


    /**
     * 查询评论
     *
     * @param commentId 评论主键
     * @return 评论
     */
    public Comments selectCommentsByCommentId(Long commentId);

    /**
     * 查询评论列表
     *
     * @param comments 评论
     * @return 评论集合
     */
    public List<Comments> selectCommentsList(Comments comments);

    /**
     * 新增评论
     *
     * @param comments 评论
     * @return 结果
     */
    public int insertComments(Comments comments);

    /**
     * 修改评论
     *
     * @param comments 评论
     * @return 结果
     */
    public int updateComments(Comments comments);

    /**
     * 批量删除评论
     *
     * @param commentIds 需要删除的评论主键集合
     * @return 结果
     */
    public int deleteCommentsByCommentIds(Long[] commentIds);

    /**
     * 删除评论信息
     *
     * @param commentId 评论主键
     * @return 结果
     */
    public int deleteCommentsByCommentId(Long commentId);


    /**
     * 查询评论列表Vo
     *
     * @param comments 评论
     * @return 评论集合
     */
    public List<CommentsVo> selectCommentsVoList(Comments comments);
}
