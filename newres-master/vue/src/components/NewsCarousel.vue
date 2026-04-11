<script setup>
import { goToNewsDetailWithHistory } from "@/utils/newsUtils";
import { useRouter } from "vue-router";

const router = useRouter();

// 轮播图点击处理
const handleCarouselClick = (news) => {
  goToNewsDetailWithHistory(router, news.newsId, news.title);
};
</script>

<template>
  <el-carousel-item @click="handleCarouselClick(item)">
    <!-- 原有的轮播图展示结构 -->
  </el-carousel-item>
</template>
