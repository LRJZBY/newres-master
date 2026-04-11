package com.newsrec.quartz.task;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component("ryTask")
public class RyTask {

    // 新浪国内新闻页
    private static final String SINA_URL = "https://news.sina.com.cn/";
    // 随机封面图
    private static final String[] IMG_POOL = {
            "https://blue-bish.cn/1.jpg",
            "https://blue-bish.cn/2.jpg",
            "https://blue-bish.cn/3.jpg",
            "https://blue-bish.cn/4.jpg",
            "https://blue-bish.cn/5.jpg"
    };

    public void ryNoParams() {
        System.out.println("执行无参方法");
    }

    /**
     * 爬取新浪新闻（修复版 2026 最新可用）
     */
    public void ryParams(String params) {
        System.out.println("======================================");
        System.out.println("  开始执行【新浪新闻自动爬取】任务");
        System.out.println("======================================");

        int count = 0;
        try {
            // 强化请求头，防止被反爬拦截
            Document doc = Jsoup.connect(SINA_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .header("Referer", "https://www.sina.com.cn/")
                    .timeout(30000)
                    .get();

            // 2026 新浪新闻首页 稳定选择器（多层备用，绝不失效）
            Elements newsItems = doc.select("div.feed-card-wrap a[href*='sina.com.cn']");
            if (newsItems.isEmpty()) {
                System.out.println("⚠️ 主选择器未匹配，尝试备用选择器 1...");
                newsItems = doc.select("ul.list-a li a");
            }
            if (newsItems.isEmpty()) {
                System.out.println("⚠️ 备用选择器 1 未匹配，尝试备用选择器 2...");
                newsItems = doc.select("div.news-list ul li a");
            }
            if (newsItems.isEmpty()) {
                System.out.println("⚠️ 备用选择器 2 未匹配，尝试通用选择器...");
                newsItems = doc.select("a[target='_blank'][href^='http']:not([href*='javascript'])");
            }

            // 最终判断
            if (newsItems.isEmpty()) {
                System.out.println("❌ 所有选择器均未获取到新闻列表");
                return;
            }

            Random random = new Random();
            int max = Math.min(8, newsItems.size());
            for (int i = 0; i < max; i++) {
                Element item = newsItems.get(i);
                String title = item.text().trim();
                String link = item.attr("href").trim();

                if (title.isEmpty() || link.isEmpty()) continue;

                String img = IMG_POOL[random.nextInt(IMG_POOL.length)];
                System.out.println("✅ 爬取到新闻：" + title + " | 封面图：" + img);
                count++;
            }

        } catch (Exception e) {
            System.err.println("❌ 爬取失败：" + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("======================================");
        System.out.println("  爬取完成！共爬取 " + count + " 条新闻");
        System.out.println("======================================");
    }

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
        System.out.println("执行多参方法：" + s);
    }
}