<script setup>
import { goToNewsDetailWithHistory } from "@/utils/newsUtils";
import { useRouter } from "vue-router";

const router = useRouter();

// 热门新闻点击处理
const handleHotNewsClick = (news) => {
  goToNewsDetailWithHistory(router, news.newsId, news.title);
};
</script>

<template>
  <div class="hot-news-item" @click="handleHotNewsClick(news)">
    <!-- 原有的热门新闻展示结构 -->
  </div>
</template>
