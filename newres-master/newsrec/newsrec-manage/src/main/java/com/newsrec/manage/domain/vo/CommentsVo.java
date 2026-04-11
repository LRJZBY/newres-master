package com.newsrec.manage.domain.vo;

import com.newsrec.manage.domain.Comments;
import lombok.Data;

/**
 * 功能
 * 作者：严伟
 * 日期：2025/3/4 1:36
 **/
@Data
public class CommentsVo extends Comments {

    //新闻标题
    private String title;

    //用户名称
    private String username;

    //评论的用户头像
    private String avatar;
}
