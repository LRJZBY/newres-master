package com.newsrec.manage.domain.dto;

/**
 * 功能
 * 作者：李金博
 * 日期：2026/3/14 0:56
 **/
import lombok.Data;

import java.io.Serializable;

@Data
public class HotNewsDTO implements Serializable {
    private String newsId;
    private String title;
    private String content;

    public HotNewsDTO(String newsId, String title) {
        this.newsId = newsId;
        this.title = title;
    }

    public HotNewsDTO(String newsId, String title, String content) {
        this.newsId = newsId;
        this.title = title;
        this.content = content;
    }
}
