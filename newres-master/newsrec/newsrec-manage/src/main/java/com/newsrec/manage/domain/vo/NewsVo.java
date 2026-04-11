package com.newsrec.manage.domain.vo;

import com.newsrec.manage.domain.News;
import lombok.Data;

/**
 * 功能
 * 作者：严伟
 * 日期：2025/3/2 21:27
 **/

@Data
public class NewsVo extends News {

    //发布人
    private String username;

    //所属分类
   private String categoryName;

    //评论数量
    private int commentCount;

    //浏览量
    private int viewCount;

    //点赞数目
    private int likeCount;

    //收藏数目
    private int collectCount;
}
