package com.newsrec.manage.domain.vo;

import com.newsrec.manage.domain.History;
import lombok.Data;

/**
 * 功能
 * 作者：严伟
 * 日期：2025/3/4 1:35
 **/
@Data
public class HistoryVo extends History {

    //新闻标题
    private String title;

    //用户名称
    private String username;
}
