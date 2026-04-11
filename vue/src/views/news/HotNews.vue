<script setup>
import { ref, onMounted } from "vue";
import { newsApi } from "@/api";
import { ElMessage } from "element-plus";

const hotNews = ref([]);
const loading = ref(false);
const error = ref("");

const fetchHotNews = async () => {
  loading.value = true;
  error.value = "";

  try {
    const response = await newsApi.getHotNews();
    if (response.code === 200) {
      hotNews.value = response.rows;
    } else {
      error.value = response.msg || "获取热门新闻失败";
      ElMessage.error(error.value);
    }
  } catch (err) {
    console.error("获取热门新闻失败:", err);
    error.value = "网络请求失败，请稍后重试";
    ElMessage.error(error.value);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchHotNews();
});
</script>

<template>
  <div class="hot-news">
    <h2>热门新闻</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 错误提示 -->
    <el-alert
      v-if="error"
      :title="error"
      type="error"
      show-icon
      closable
      @close="error = ''"
    />

    <!-- 热门新闻列表 -->
    <div class="hot-news-list">
      <div
        v-for="news in hotNews"
        :key="news.newsId"
        class="hot-news-item"
        @click="goToNewsDetail(news.newsId)"
      >
        <div class="news-content">
          <h3>{{ news.title }}</h3>
          <p>{{ news.content }}</p>
          <div class="news-meta">
            <span class="news-time">{{ formatDate(news.createTime) }}</span>
            <span class="news-stats">
              <el-icon><View /></el-icon> {{ news.viewCount }}
              <el-icon><Star /></el-icon> {{ news.likeCount }}
              <el-icon><ChatDotRound /></el-icon> {{ news.commentCount }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
