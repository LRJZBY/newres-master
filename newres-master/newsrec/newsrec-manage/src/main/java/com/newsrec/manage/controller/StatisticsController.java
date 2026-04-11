package com.newsrec.manage.controller;

import com.newsrec.common.core.controller.BaseController;
import com.newsrec.common.core.domain.AjaxResult;
import com.newsrec.manage.service.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能:主要用于系统的数据统计
 * 作者：严伟
 * 日期：2025/3/4 18:10
 **/

@RestController
@RequestMapping("/manage/statistics")
public class StatisticsController extends BaseController {
    @Autowired
    private ICommentsService commentsService;

    @Autowired
    private INewsService newsService;

    @Autowired
    private IUsersService usersService;

    @Autowired
    private IFeedbackService feedbackService;

    @Autowired
    private IMessagesService messagesService;
    @GetMapping("/dashboard")
    public DashboardData getDashboardData() {
        Integer newsTotal = newsService.getNewsTotal();
        Integer commentTotal = commentsService.getCommentTotal();
        Integer userTotal = usersService.getUserTotal();
        Integer pendingTotal = newsService.getPendingTotal();
        Integer messageTotal = messagesService.countMessage();
        Integer feedbackTotal = feedbackService.countPendingFeedback();
        return new DashboardData(newsTotal, commentTotal, userTotal, pendingTotal, messageTotal, feedbackTotal);
    }


    //前台的面板数据渲染，我的收藏，获得点赞，获得评论，获得的浏览量
    @GetMapping("/dashboard/front")
    public AjaxResult getDashboardDataFront(Long userId) {
        Integer favoriteTotal =newsService.countNewsFavoriteByUserId(userId);
        Integer likeTotal =newsService.countNewsLikeByUserId(userId);
        Integer commentTotal =newsService.countNewsCommentByUserId(userId);
        Integer newsTotal =newsService.countNewsByUserId(userId);
        return  success(new DashboardDataFront(favoriteTotal, likeTotal, commentTotal, newsTotal));
    }
}

//后台数据面板渲染
@Data
class DashboardData {
    private Integer newsTotal;
    private Integer commentTotal;
    private Integer userTotal;
    private Integer pendingTotal;
    //留言条数
    private Integer messageTotal;

    //待回复反馈条数
    private Integer feedbackTotal;

    public DashboardData(Integer newsTotal, Integer commentTotal, Integer userTotal, Integer pendingTotal,Integer messageTotal,Integer feedbackTotal) {
        this.newsTotal = newsTotal;
        this.commentTotal = commentTotal;
        this.userTotal = userTotal;
        this.pendingTotal = pendingTotal;
        this.messageTotal = messageTotal;
        this.feedbackTotal = feedbackTotal;
    }
}
//前台的面板数据渲染，我的收藏，获得点赞，获得评论，我的新闻数量
@Data
class DashboardDataFront {
    private Integer favorites;
    private Integer likes;
    private Integer comments;
    private Integer news;

    public DashboardDataFront(Integer favorites, Integer likes, Integer comments, Integer news) {
        this.favorites = favorites;
        this.likes = likes;
        this.comments = comments;
        this.news = news;
    }
}



