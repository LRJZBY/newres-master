package com.newsrec.manage.domain.vo;

import com.newsrec.manage.domain.Banner;
import lombok.Data;

/**
 * 功能
 * 作者：严伟
 * 日期：2025/3/2 16:16
 **/
@Data
public class BannerVo extends Banner {


    //根据新闻id，返回给前台对应的新闻标题
    private String title;
}
