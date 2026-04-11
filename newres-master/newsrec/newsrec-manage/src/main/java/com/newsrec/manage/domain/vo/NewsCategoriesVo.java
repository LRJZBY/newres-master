package com.newsrec.manage.domain.vo;


import com.newsrec.manage.domain.NewsCategories;
import lombok.Data;


/**
 * 功能：分类的Vo视图，返回每个分类的新闻数量
 * 作者：严伟
 * 日期：2025/2/21 20:04
 **/

@Data
public class NewsCategoriesVo extends NewsCategories {
    private int newsCount;
}
