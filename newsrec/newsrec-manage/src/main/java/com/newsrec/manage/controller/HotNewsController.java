package com.newsrec.manage.controller;

/**
 * 功能
 * 作者：严伟
 * 日期：2025/3/13 17:53
 **/
import com.newsrec.common.core.controller.BaseController;
import com.newsrec.common.core.page.TableDataInfo;
import com.newsrec.manage.domain.dto.HotNewsDTO;
import com.newsrec.manage.domain.vo.NewsVo;
import com.newsrec.manage.service.INewsService;
import com.newsrec.manage.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/manage/hotNews")
public class HotNewsController extends BaseController {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private INewsService newsService;


    // 热榜新闻,继承基础，实现分页展示(获取热榜前6)
    @GetMapping("/list")
    public TableDataInfo getHotNews1() {
        startPage();
        List<String> hotNewsIds = redisUtils.getHotNews(6);
        List<HotNewsDTO> hotNewsDTOs = new ArrayList<>();
        for (String newsId : hotNewsIds) {
            NewsVo news = newsService.selectNewsVOByNewsId(Long.parseLong(newsId));
            if (news != null) {
                hotNewsDTOs.add(new HotNewsDTO(newsId, news.getTitle(),news.getContent()));
            }
        }
        return  getDataTable(hotNewsDTOs);
    }
}
