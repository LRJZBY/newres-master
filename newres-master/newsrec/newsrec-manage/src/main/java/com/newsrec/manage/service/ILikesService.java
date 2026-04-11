package com.newsrec.manage.service;


import com.newsrec.manage.domain.Likes;

import java.util.List;

/**
 * 点赞Service接口
 *
 * @author weiyan
 * @date 2025-03-25
 */
public interface ILikesService
{



    /**
     * 查询点赞
     *
     * @param likeId 点赞主键
     * @return 点赞
     */
    public Likes selectLikesByLikeId(Long likeId);

    /**
     * 查询点赞列表
     *
     * @param likes 点赞
     * @return 点赞集合
     */
    public List<Likes> selectLikesList(Likes likes);

    /**
     * 新增点赞
     *
     * @param likes 点赞
     * @return 结果
     */
    public int insertLikes(Likes likes);

    /**
     * 修改点赞
     *
     * @param likes 点赞
     * @return 结果
     */
    public int updateLikes(Likes likes);

    /**
     * 批量删除点赞
     *
     * @param likeIds 需要删除的点赞主键集合
     * @return 结果
     */
    public int deleteLikesByLikeIds(Long[] likeIds);

    /**
     * 删除点赞信息
     *
     * @param likeId 点赞主键
     * @return 结果
     */
    public int deleteLikesByLikeId(Long likeId);
}
