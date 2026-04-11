<template>
  <div class="search-results-container">
    <Header />
    <div class="search-results-content">
      <div class="search-header">
        <h3>搜索结果</h3>
        <p>关键词 "{{ searchKeyword }}" 的搜索结果（共 {{ totalResults }} 条）</p>
      </div>

      <!-- 主内容区域 -->
      <div class="main-content">
        <!-- 左侧搜索结果列表 -->
        <div class="search-list-section">
          <!-- 加载中状态 -->
          <div v-if="loading" class="loading-state">
            <el-skeleton :rows="5" animated />
          </div>

          <!-- 搜索结果列表 -->
          <div v-else class="search-results-list">
            <div v-if="searchResults.length > 0">
              <div
                v-for="news in searchResults"
                :key="news.newsId"
                class="news-item"
                @click="goToNewsDetail(news.newsId)"
              >
                <div class="news-image" v-if="news.img">
                  <img :src="news.img" :alt="news.title" @error="handleImageError" />
                </div>
                <div class="news-content">
                  <h4 class="news-title">{{ news.title }}</h4>
                  <p class="news-summary">{{ news.summary }}</p>
                  <div class="news-meta">
                    <span class="news-time">
                      <el-icon><Timer /></el-icon>
                      {{ news.createTime }}
                    </span>
                    <span class="news-category">
                      <el-icon><Collection /></el-icon>
                      {{ news.categoryName || '未分类' }}
                    </span>
                    <span class="news-stats">
                      <el-icon><View /></el-icon> {{ news.viewCount || 0 }}
                      <el-icon><ChatLineRound /></el-icon> {{ news.commentCount || 0 }}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 无搜索结果状态 -->
            <div v-else class="no-results">
              <el-empty :description="`没有找到与'${searchKeyword}'相关的内容`" />
              <p class="search-suggestions">
                建议：
                <ul>
                  <li>请检查关键词拼写是否正确</li>
                  <li>尝试使用更通用的关键词</li>
                  <li>尝试减少关键词数量</li>
                </ul>
              </p>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="searchResults.length > 0" class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="totalResults"
              @current-change="handlePageChange"
              layout="prev, pager, next"
              background
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
                v-for="news in recommendedNews"
                :key="news.id"
                class="recommended-news-item"
                @click="goToNewsDetail(news.id)"
              >
                <img
                  :src="news.img"
                  :alt="news.title"
                  class="recommended-news-image"
                  @error="handleImageError"
                />
                <div class="recommended-news-info">
                  <h4>{{ news.title }}</h4>
                  <span class="views">{{ news.views }}阅读</span>
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
import { Timer, View, ChatLineRound, Collection } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const router = useRouter();
const route = useRoute();

// 搜索关键词
const searchKeyword = ref(decodeURIComponent(route.query.keyword || ""));

// 搜索结果数据
const searchResults = ref([]);
const totalResults = ref(0);
const loading = ref(true);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);

// 默认图片
const defaultImage = new URL(
  "@/assets/imgs/default-news-image.png", 
  import.meta.url
).href;

// 图片加载失败时使用默认图片
const handleImageError = (event) => {
  event.target.src = defaultImage;
};

// 热点新闻数据
const hotNews = ref([]);

// 推荐新闻数据
const recommendedNews = ref([]);

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

    const response = await request.get(`/manage/recommendation/hybrid/${userInfo.userId}`, {
      params: {
        limit: 5, // 获取5条推荐新闻
      },
    });

    if (response.code === 200) {
      recommendedNews.value = response.rows.map((item) => ({
        id: item.newsId,
        newsId: item.newsId,
        title: item.title,
        img: item.img || defaultImage,
        views: item.viewCount || 0,
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
      title: "未来科技发展趋势分析",
      img: defaultImage,
      views: 888,
    },
    {
      id: 2,
      newsId: 2,
      title: "全球经济新动向",
      img: defaultImage,
      views: 756,
    },
    {
      id: 3,
      newsId: 3,
      title: "人工智能技术突破",
      img: defaultImage,
      views: 635,
    }
  ];
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

// 获取搜索结果
const fetchSearchResults = async () => {
  if (!searchKeyword.value) {
    ElMessage.warning("请输入搜索关键词");
    return;
  }

  loading.value = true;

  try {
    const response = await request.get("/manage/news/list", {
      params: {
        title: searchKeyword.value,  // 按标题模糊搜索
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    });

    if (response.code === 200) {
      // 处理搜索结果数据
      searchResults.value = response.rows.map(item => ({
        newsId: item.newsId,
        title: item.title,
        summary:getSummary(item.content), 
        img: item.img,
        createTime: item.createTime,
        categoryName: item.categoryName,
        viewCount: item.viewCount || 0,
        commentCount: item.commentCount || 0
      }));
      
      totalResults.value = response.total;
      console.log("搜索结果:", searchResults.value);
    } else {
      ElMessage.warning(response.msg || "搜索失败");
      searchResults.value = [];
      totalResults.value = 0;
    }
  } catch (error) {
    console.error("搜索新闻失败:", error);
    ElMessage.error("网络错误，请稍后再试");
    searchResults.value = [];
    totalResults.value = 0;
  } finally {
    loading.value = false;
  }
};

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

// 跳转到新闻详情
const goToNewsDetail = (newsId) => {
  // 获取当前新闻标题，用于记录浏览历史
  const news = searchResults.value.find(item => item.newsId === newsId);
  const title = news ? news.title : '';
  
  // 获取当前登录用户信息
  const userInfo = JSON.parse(localStorage.getItem('user') || '{}');
  const userId = userInfo.userId;
  
  if (userId && newsId) {
    // 添加浏览记录
    const historyData = {
      userId: userId,
      newsId: newsId,
      title: title
    };
    
    // 调用添加浏览记录接口
    request.post('/manage/history', historyData)
      .then(response => {
        if (response.code !== 200) {
          console.warn('添加浏览记录失败:', response.msg);
        }
      })
      .catch(error => {
        console.error('添加浏览记录接口调用失败:', error);
      })
      .finally(() => {
        // 无论是否成功添加浏览记录，都跳转到新闻详情页
        router.push(`/newsDetail?newsId=${newsId}`);
      });
  } else {
    // 如果用户未登录或没有新闻ID，直接跳转
    router.push(`/newsDetail?newsId=${newsId}`);
  }
};

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchSearchResults();
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// 监听URL参数变化
watch(
  () => route.query.keyword,
  (newKeyword) => {
    if (newKeyword) {
      searchKeyword.value = decodeURIComponent(newKeyword);
      currentPage.value = 1; // 重置为第一页
      fetchSearchResults();
    }
  },
  { immediate: true }
);

// 组件挂载时获取搜索结果和热门新闻
onMounted(() => {
  if (searchKeyword.value) {
    fetchSearchResults();
  }
  fetchHotNews();
  fetchRecommendedNews(); // 获取推荐新闻
});
</script>

<style scoped>
.search-results-container {
  background-color: #f5f7fa;
  min-height: 100vh;
}

.search-results-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-header {
  margin-bottom: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.search-header h3 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.search-header p {
  font-size: 14px;
  color: #606266;
}

/* 添加主内容区域布局 */
.main-content {
  display: flex;
  gap: 24px;
}

/* 左侧搜索结果列表区域 */
.search-list-section {
  flex: 1;
}

.loading-state {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.search-results-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.news-item {
  display: flex;
  gap: 20px;
  background: #fff;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
}

.news-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.news-image {
  width: 150px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.news-title {
  font-size: 18px;
  margin-bottom: 10px;
  color: #303133;
  line-height: 1.4;
}

.news-summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
  flex-grow: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: #909399;
  font-size: 13px;
}

.news-time, .news-category, .news-stats {
  display: flex;
  align-items: center;
  gap: 4px;
}

.news-stats {
  display: flex;
  gap: 8px;
}

.no-results {
  background: #fff;
  padding: 40px 20px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.search-suggestions {
  margin-top: 20px;
  text-align: left;
  color: #606266;
}

.search-suggestions ul {
  margin-top: 10px;
  padding-left: 24px;
}

.search-suggestions li {
  margin-bottom: 6px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
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
  border-bottom: 2px solid var(--primary-color, #409eff);
}

.hot-news-section,
.recommended-news-section {
  width: 350px;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
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
  background: #ff9966;
}

.hot-news-info h4 {
  font-size: 14px;
  color: #303133;
}

.recommended-news-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  cursor: pointer;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.recommended-news-item:last-child {
  border-bottom: none;
}

.recommended-news-image {
  width: 120px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
}

.recommended-news-info h4 {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
}

.recommended-news-info .views {
  font-size: 12px;
  color: #909399;
}

/* 响应式设计调整 */
@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }

  .news-item {
    flex-direction: column;
  }
  
  .news-image {
    width: 100%;
    height: 180px;
  }

  .sidebar {
    width: 100%;
  }

  .hot-news-section,
  .recommended-news-section {
    width: 100%;
  }
}
</style>
