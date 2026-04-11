package com.newsrec.quartz.task;

import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * 功能：定时任务
 * 作者：严伟
 * 日期：2025/1/30 9:55
 **/
@Component
public class MyTask {
    public void showTime() {
        System.out.println("执行了定时任务:"+new Date());

    }
}
