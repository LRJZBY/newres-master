package com.newsrec;


import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 * @author ruoyi
 */
@EnableFileStorage //开启文件上传功能
@EnableScheduling  //定时任务：reids实现新闻热榜功能
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class NewsRecApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(NewsRecApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  新闻推荐系统启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}

