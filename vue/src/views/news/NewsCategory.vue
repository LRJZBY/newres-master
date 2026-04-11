<template>
  <div class="news-category-container">
    <Header />
    <div class="category-content">
      <!-- 分类标题 -->
      <div class="category-header">
        <h2>{{ categoryInfo.name }}</h2>
        <p>了解最新的{{ categoryInfo.name }}信息</p>
      </div>

      <div class="main-content">
        <!-- 左侧新闻列表 -->
        <div class="news-list-section">
          <div class="news-list">
            <div
              v-for="news in categoryNews"
              :key="news.id"
              class="news-item"
              @click="goToNewsDetail(news.id)"
            >
              <div class="news-image">
                <img
                  :src="news.img"
                  :alt="news.title"
                  @error="useDefaultImage"
                />
              </div>
              <div class="news-info">
                <h3 class="news-title">{{ news.title }}</h3>
                <p class="news-summary">{{ news.summary }}</p>
                <div class="news-meta">
                  <span class="time">
                    <el-icon><Timer /></el-icon>
                    {{ news.publishTime }}
                  </span>
                  <span class="views">
                    <el-icon><View /></el-icon>
                    {{ news.views }}
                  </span>
                  <span class="comments">
                    <el-icon><ChatLineRound /></el-icon>
                    {{ news.comments }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页器 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="totalNews"
              @current-change="handlePageChange"
              layout="prev, pager, next"
            />
          </div>
        </div>

        <!-- 右侧边栏 -->
        <div class="sidebar">
          <!-- 热点新闻 -->
          <div class="hot-news-section">
            <h3 class="section-title">热点新闻</h3>
            <div class="hot-news-list">
              <div
                v-for="(news, index) in hotNews"
                :key="news.id"
                class="hot-news-item"
                @click="goToNewsDetail(news.newsId)"
              >
                <span class="rank-number" :class="{ 'top-three': index < 3 }">{{
                  index + 1
                }}</span>
                <div class="hot-news-info">
                  <h4>{{ news.title }}</h4>
                </div>
              </div>
            </div>
          </div>

          <!-- 推荐新闻 -->
          <div class="recommended-news-section">
            <h3 class="section-title">推荐阅读</h3>
            <div class="recommended-news-list">
              <div
                v-for="recommendation in recommendedNews"
                :key="recommendation.id"
                class="recommended-news-item"
                @click="goToNewsDetail(recommendation.newsId)"
              >
                <img
                  :src="recommendation.cover"
                  :alt="recommendation.title"
                  class="recommended-news-image"
                  @error="useDefaultImage"
                />
                <div class="recommended-news-info">
                  <h4>{{ recommendation.title }}</h4>
                  <div class="recommendation-meta">
                    <span class="recommendation-time">{{
                      recommendation.publishTime
                    }}</span>
                    <span class="recommendation-views">
                      <el-icon><View /></el-icon>
                      {{ recommendation.viewCount }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { Timer, View, ChatLineRound } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const router = useRouter();
const route = useRoute();

// 分类信息
const categoryId = ref(route.query.categoryId);
const categoryInfo = ref({
  name: "加载中...",
  description: "加载中...",
});

// 分类新闻列表数据
const categoryNews = ref([]);

// 热点新闻数据
const hotNews = ref([]);

// 推荐新闻数据
const recommendedNews = ref([]);

// 获取热门新闻数据
const fetchHotNews = async () => {
  try {
    const response = await request.get("/manage/hotNews/list");
    if (response.code === 200) {
      hotNews.value = response.rows.map((item) => ({
        id: item.id,
        newsId: item.newsId,
        title: item.title,
      }));
    }
  } catch (error) {
    console.error("获取热门新闻数据失败:", error);
  }
};

// 获取混合推荐新闻
const fetchRecommendedNews = async () => {
  try {
    // 获取用户ID
    const userInfo = JSON.parse(localStorage.getItem("user") || "{}");
    if (!userInfo.userId) {
      console.log("用户未登录，无法获取个性化推荐");
      setDefaultRecommendations();
      return;
    }

    const response = await request.get(
      `/manage/recommendation/hybrid/${userInfo.userId}`,
      {
        params: {
          limit: 6, // 获取6条推荐新闻
        },
      }
    );

    if (response.code === 200) {
      // 确保推荐列表中不包含当前正在浏览的分类的新闻
      recommendedNews.value = response.rows.map((item) => ({
        id: item.newsId,
        newsId: item.newsId,
        title: item.title,
        cover: item.img || defaultImage,
        publishTime: item.createTime,
        viewCount: item.viewCount || 0,
      }));
      console.log("获取到推荐新闻数量:", recommendedNews.value.length);
    } else {
      setDefaultRecommendations();
    }
  } catch (error) {
    console.error("获取推荐新闻数据失败:", error);
    setDefaultRecommendations();
  }
};

// 设置默认推荐新闻
const setDefaultRecommendations = () => {
  recommendedNews.value = [
    {
      id: 1,
      newsId: 1,
      title: "重大政策调整：教育改革新方向",
      cover: defaultImage,
      publishTime: "2024-03-14",
      viewCount: 1256,
    },
    {
      id: 2,
      newsId: 2,
      title: "科技突破：人工智能助力医疗诊断",
      cover: defaultImage,
      publishTime: "2024-03-13",
      viewCount: 986,
    },
    {
      id: 3,
      newsId: 3,
      title: "经济动态：一季度GDP增长稳健",
      cover: defaultImage,
      publishTime: "2024-03-12",
      viewCount: 745,
    },
  ];
};

// 分页相关
const currentPage = ref(1);
const pageSize = ref(8);
const totalNews = ref(0);

// 默认图片
const defaultImage = new URL(
  "@/assets/imgs/default-news-image.png",
  import.meta.url
).href;

// 使用默认图片
const useDefaultImage = (event) => {
  event.target.src = defaultImage;
};

// 计算新闻摘要
const getSummary = (content) => {
  if (!content) return "";
  // 去除HTML标签
  const text = content.replace(/<\/?[^>]+>/g, "");
  if (text.length > 100) {
    return text.substring(0, 100) + "...";
  }
  return text;
};

// 获取分类新闻列表
const fetchCategoryNews = async () => {
  try {
    const response = await request.get("/manage/news/list", {
      params: {
        categoryId: categoryId.value,
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        status: 1,
      },
    });
    if (response.code === 200) {
      categoryNews.value = response.rows.map((item) => ({
        id: item.newsId, // 使用newsId作为ID
        title: item.title,
        summary: getSummary(item.content),
        img: item.img, // 使用img字段
        publishTime: item.createTime, // 使用createTime字段作为发布时间
        views: item.viewCount || 0, // 使用viewCount字段
        comments: item.commentCount || 0, // 使用commentCount字段
        likes: item.likeCount || 0, // 使用likeCount字段
        category: item.categoryName, // 添加分类名称
      }));
      totalNews.value = response.total;
      console.log("获取到的分类新闻:", categoryNews.value);
    } else {
      ElMessage.warning(response.msg || "获取新闻列表失败");
      categoryNews.value = []; // 清空列表
    }
  } catch (error) {
    console.error("获取分类新闻列表失败:", error);
    ElMessage.error("网络错误，请稍后再试");
    categoryNews.value = []; // 出错时清空列表
  }
};

// 获取分类信息
const fetchCategoryInfo = async () => {
  try {
    const response = await request.get(
      `/manage/categories/${categoryId.value}`
    );
    if (response.code === 200) {
      categoryInfo.value = {
        name: response.data.categoryName || "未知分类",
        description: response.data.remark || "暂无描述",
      };
      console.log("获取到的分类信息:", categoryInfo.value);
    } else {
      ElMessage.warning(response.msg || "获取分类信息失败");
      // 设置默认值
      categoryInfo.value = {
        name: "未知分类",
        description: "暂无数据",
      };
    }
  } catch (error) {
    console.error("获取分类信息失败:", error);
    ElMessage.error("网络错误，请稍后再试");
    // 设置默认值
    categoryInfo.value = {
      name: "未知分类",
      description: "暂无数据",
    };
  }
};

// 添加watch监听route.query的变化
watch(
  () => route.query.categoryId,
  (newCategoryId) => {
    if (newCategoryId) {
      console.log("分类ID变化:", newCategoryId);
      // 更新本地categoryId值
      categoryId.value = newCategoryId;
      // 重新获取数据
      fetchCategoryInfo();
      fetchCategoryNews();
      fetchHotNews();
      fetchRecommendedNews();
      // 重置分页
      currentPage.value = 1;
    }
  },
  { immediate: true } // 立即执行一次
);

// 页面初始化 - 保留现有的onMounted
onMounted(() => {
  // 初始数据加载已由watch处理，这里可以留空
  // 或者处理不依赖于categoryId的其他初始化
  if (!categoryId.value) {
    // 如果没有categoryId，可能是直接访问了分类页面，加载默认数据
    setDefaultRecommendations();
  } else {
    // 确保推荐新闻被加载
    fetchRecommendedNews();
  }
});

// 跳转到新闻详情
const goToNewsDetail = (newsId) => {
  router.push(`/newsDetail?newsId=${newsId}`);
};

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchCategoryNews();
};
</script>

<style scoped>
.news-category-container {
  min-height: 100vh;
  background-color: var(--bg-color);
}

.category-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.category-header {
  margin-bottom: 24px;
  text-align: center;
}

.category-header h2 {
  font-size: 24px;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.category-header p {
  color: var(--text-secondary);
}

.main-content {
  display: flex;
  gap: 24px;
}

/* 左侧新闻列表样式 */
.news-list-section {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.news-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.news-item:hover {
  background: var(--bg-light);
}

.news-image {
  width: 125px;
  height: 110px;
  border-radius: 8px;
  overflow: hidden;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-info {
  flex: 1;
}

.news-title {
  font-size: 18px;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.news-summary {
  color: var(--text-secondary);
  margin-bottom: 16px;
  line-height: 1.5;
}

.news-meta {
  display: flex;
  gap: 16px;
  color: var(--text-light);
  font-size: 14px;
}

/* 右侧边栏样式 */
.sidebar {
  flex: 0.35;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-title {
  font-size: 18px;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--primary-color);
}

.hot-news-section,
.recommended-news-section {
  width: 350px;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.hot-news-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  cursor: pointer;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.hot-news-item:last-child {
  border-bottom: none;
}

.rank-number {
  width: 24px;
  height: 24px;
  background: #909399;
  color: #fff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.rank-number.top-three {
  background: var(--primary-color);
}

.recommended-news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.recommended-news-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  cursor: pointer;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}

.recommended-news-item:last-child {
  border-bottom: none;
}

.recommended-news-item:hover .recommended-news-info h4 {
  color: var(--primary-color);
}

.recommended-news-image {
  width: 120px;
  height: 80px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.recommended-news-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.recommended-news-info h4 {
  font-size: 14px;
  font-weight: bold;
  margin: 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.recommendation-meta {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}

.recommendation-time {
  color: #999;
}

.recommendation-views {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
