<script setup>
import { goToNewsDetailWithHistory } from "@/utils/newsUtils";
import { useRouter } from "vue-router";

const router = useRouter();

// 新闻点击处理
const handleNewsClick = (news) => {
  goToNewsDetailWithHistory(router, news.newsId, news.title);
};
</script>

<template>
  <!-- 在点击事件中使用新的处理函数 -->
  <div class="news-item" @click="handleNewsClick(news)">
    <!-- 原有的新闻展示结构 -->
  </div>
</template>
