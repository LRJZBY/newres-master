<template>
  <Header />
  <div class="recommendation-news">
    <h1>推荐新闻</h1>
    <div class="news-list">
      <!-- 加载状态提示 -->
      <div v-if="isLoading" class="loading">正在加载新闻数据...</div>

      <div
        v-for="news in recommendationNews"
        :key="news.id"
        class="news-item"
        @click="goToNewsDetail(news.id)"
      >
        <img :src="news.image" :alt="news.title" class="news-image" />
        <div class="news-content">
          <h2 class="news-title">{{ news.title }}</h2>
          <p class="news-summary">{{ news.summary }}</p>
          <span class="news-time">{{ news.publishTime }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Header from "@/components/Header.vue";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const router = useRouter();
const recommendationNews = ref([]);
const isLoading = ref(true); // 添加加载状态

// 获取混合推荐新闻数据
const fetchRecommendationNews = async () => {
  try {
    // 获取用户ID
    const userInfo = JSON.parse(localStorage.getItem("user") || "{}");
    if (!userInfo.userId) {
      ElMessage.warning("请先登录后查看个性化推荐");
      router.push("/login");
      return;
    }

    const response = await request.get(
      `/manage/recommendation/hybrid/${userInfo.userId}`,
      {
        params: {
          limit: 10, // 获取10条推荐新闻
        },
      }
    );

    if (response.code === 200) {
      recommendationNews.value = response.rows.map((item) => ({
        id: item.newsId,
        newsId: item.newsId,
        title: item.title,
        summary: getSummary(item.content),
        image: item.img || getDefaultImage(),
        publishTime: item.createTime,
        viewCount: item.viewCount || 0,
      }));
      console.log("获取到推荐新闻数量:", recommendationNews.value.length);
    } else {
      ElMessage.warning(response.msg || "获取推荐新闻失败");
      setDefaultRecommendations();
    }
  } catch (error) {
    console.error("获取推荐新闻数据失败:", error);
    ElMessage.error("网络错误，请稍后再试");
    setDefaultRecommendations();
  } finally {
    isLoading.value = false;
  }
};

// 计算新闻摘要
const getSummary = (content) => {
  if (!content) return "暂无描述";
  // 去除HTML标签
  const text = content.replace(/<\/?[^>]+>/g, "");
  if (text.length > 120) {
    return text.substring(0, 120) + "...";
  }
  return text;
};

// 获取默认图片
const getDefaultImage = () => {
  return "https://blue-bishe.oss-cn-hangzhou.aliyuncs.com/newstrc-images/2025/03/12/67d18569aef2fd86ebc314e2.png";
};

// 设置默认推荐新闻
const setDefaultRecommendations = () => {
  recommendationNews.value = [
    {
      id: 1,
      newsId: 1,
      title: "重大政策调整：教育改革新方向",
      summary:
        "教育部近日发布了一系列教育改革新政策，旨在提升学生综合素质和创新能力。",
      image: getDefaultImage(),
      publishTime: "2024-03-14",
      viewCount: 1256,
    },
    {
      id: 2,
      newsId: 2,
      title: "科技突破：人工智能助力医疗诊断",
      summary:
        "最新研究表明，人工智能技术在医疗诊断中的应用取得了显著进展，准确率大幅提升。",
      image: getDefaultImage(),
      publishTime: "2024-03-13",
      viewCount: 986,
    },
    {
      id: 3,
      newsId: 3,
      title: "经济动态：一季度GDP增长稳健",
      summary:
        "国家统计局发布数据，一季度国内生产总值同比增长5.2%，经济运行总体平稳。",
      image: getDefaultImage(),
      publishTime: "2024-03-12",
      viewCount: 745,
    },
  ];
};

// 获取推荐新闻数据
onMounted(() => {
  fetchRecommendationNews();
});

// 跳转到新闻详情
const goToNewsDetail = (newsId) => {
  router.push(`/newsDetail?newsId=${newsId}`);
};
</script>

<style scoped>
.recommendation-news {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.news-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s ease;
}

.news-item:hover {
  transform: translateX(5px);
}

.news-image {
  width: 200px;
  height: 134px;
  object-fit: cover;
  border-radius: 4px;
}

.news-content {
  flex: 1;
}

.news-title {
  margin: 0 0 10px;
  font-size: 18px;
  color: #333;
}

.news-summary {
  margin: 0 0 10px;
  color: #666;
  line-height: 1.6;
}

.news-time {
  font-size: 12px;
  color: #999;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #999;
}
</style>
