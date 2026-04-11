package com.newsrec.manage.service.impl;

import java.util.List;
import com.newsrec.common.utils.DateUtils;
import com.newsrec.manage.domain.Likes;
import com.newsrec.manage.mapper.LikesMapper;
import com.newsrec.manage.service.ILikesService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 点赞Service业务层处理
 *
 * @author weiyan
 * @date 2025-03-25
 */
@Service
public class LikesServiceImpl implements ILikesService
{
    @Resource
    private LikesMapper likesMapper;


    /**
     * 查询点赞
     *
     * @param likeId 点赞主键
     * @return 点赞
     */
    @Override
    public Likes selectLikesByLikeId(Long likeId)
    {
        return likesMapper.selectLikesByLikeId(likeId);
    }

    /**
     * 查询点赞列表
     *
     * @param likes 点赞
     * @return 点赞
     */
    @Override
    public List<Likes> selectLikesList(Likes likes)
    {
        return likesMapper.selectLikesList(likes);
    }

    /**
     * 新增点赞
     *
     * @param likes 点赞
     * @return 结果
     */
    @Override
    public int insertLikes(Likes likes)
    {
        likes.setCreateTime(DateUtils.getNowDate());
        return likesMapper.insertLikes(likes);
    }

    /**
     * 修改点赞
     *
     * @param likes 点赞
     * @return 结果
     */
    @Override
    public int updateLikes(Likes likes)
    {
        likes.setUpdateTime(DateUtils.getNowDate());
        return likesMapper.updateLikes(likes);
    }

    /**
     * 批量删除点赞
     *
     * @param likeIds 需要删除的点赞主键
     * @return 结果
     */
    @Override
    public int deleteLikesByLikeIds(Long[] likeIds)
    {
        return likesMapper.deleteLikesByLikeIds(likeIds);
    }

    /**
     * 删除点赞信息
     *
     * @param likeId 点赞主键
     * @return 结果
     */
    @Override
    public int deleteLikesByLikeId(Long likeId)
    {
        return likesMapper.deleteLikesByLikeId(likeId);
    }
}
