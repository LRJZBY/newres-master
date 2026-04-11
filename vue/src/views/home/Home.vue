<template>
  <div class="home">
    <Header />
    <div class="content">
      <!-- 主要内容区域 -->
      <div class="main-content">
        <!-- 轮播图和公告区域 -->
        <div class="top-section">
          <div class="carousel-section">
            <el-carousel :interval="4000" height="520px">
              <el-carousel-item
                v-for="item in carouselItems.slice(0, 5)"
                :key="item.id"
              >
                <div
                  class="carousel-item-wrapper"
                  @click="goToNewsDetail(item.newsId)"
                >
                  <img
                    :src="item.img"
                    :alt="item.title"
                    class="carousel-image"
                  />
                  <div class="gradient-overlay"></div>
                  <div class="carousel-title">{{ item.remark }}</div>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>

          <div class="side-section">
            <!-- 系统公告 -->
            <div class="announcement-box">
              <h3>系统公告</h3>
              <ul class="announcement-list">
                <li
                  v-for="notice in displayedAnnouncements"
                  :key="notice.id"
                  @click="showAnnouncementDetail(notice)"
                  class="notice-item"
                >
                  <span class="notice-content">{{ notice.title }}</span>
                  <span class="notice-time">{{ notice.time }}</span>
                </li>
              </ul>
              <div
                class="announcement-pagination"
                v-if="announcements.length > announcementPageSize"
              >
                <el-pagination
                  v-model:current-page="announcementCurrentPage"
                  :page-size="announcementPageSize"
                  :total="announcements.length"
                  @current-change="handleAnnouncementPageChange"
                  layout="prev, pager, next"
                  small
                  background
                  hide-on-single-page
                />
              </div>
            </div>

            <!-- 新闻热榜 -->
            <div class="hot-news-box">
              <h3 class="section-title">热点新闻榜</h3>
              <ul class="hot-news-list">
                <li
                  v-for="(news, index) in hotNews"
                  :key="news.id"
                  @click="goToNewsDetail(news.newsId)"
                >
                  <span class="hot-rank" :class="{ 'top-three': index < 3 }">{{
                    index + 1
                  }}</span>
                  <el-tooltip
                    :content="news.title"
                    placement="top"
                    effect="dark"
                  >
                    <span class="hot-title" :title="news.title">
                      {{
                        news.title.length > 18
                          ? news.title.slice(0, 18) + "..."
                          : news.title
                      }}
                    </span>
                  </el-tooltip>
                </li>
              </ul>
            </div>

            <!-- 推荐阅读 -->
            <div class="recommendation-section">
              <h3 class="section-title">推荐阅读</h3>
              <div class="recommended-news-list">
                <div
                  v-for="recommendation in recommendationNews"
                  :key="recommendation.id"
                  class="recommended-news-item"
                  @click="goToNewsDetail(recommendation.newsId)"
                >
                  <img
                    :src="recommendation.cover"
                    :alt="recommendation.title"
                    class="recommended-news-image"
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

        <!-- 公告详情对话框 -->
        <el-dialog v-model="announcementDialogVisible" width="700px">
          <template #title>
            <div style="text-align: center; font-weight: bold">
              {{ currentAnnouncement.title }}
            </div>
          </template>
          <div class="announcement-container">
            <div class="announcement-time">{{ currentAnnouncement.time }}</div>
            <div
              class="announcement-content"
              v-html="currentAnnouncement.content"
            ></div>
          </div>
        </el-dialog>

        <!-- 新闻列表 -->
        <div class="bottom-section">
          <div class="news-section">
            <h2 class="news-title-header">新闻资讯</h2>
            <div class="news-list">
              <div
                v-for="news in newsList"
                :key="news.id"
                class="news-item"
                @click="goToNewsDetail(news.newsId)"
              >
                <div class="news-image">
                  <img :src="news.img" :alt="news.title" />
                </div>
                <div class="news-content">
                  <h3 class="news-title">{{ news.title }}</h3>
                  <p class="news-summary">{{ news.summary }}</p>
                  <div class="news-meta">
                    <span class="time">{{ news.createTime }}</span>
                    <span class="comments">
                      <el-icon><ChatLineRound /></el-icon>
                      {{ news.commentCount }}
                    </span>
                    <span class="views">
                      <el-icon><View /></el-icon>
                      {{ news.viewCount }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
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
        </div>
      </div>
    </div>
    <Footer />

    <!-- 分类偏好设置对话框 -->
    <el-dialog
      v-model="preferencesDialogVisible"
      :title="null"
      width="40%"
      :close-on-click-modal="false"
      :show-close="false"
      class="preferences-dialog"
      top="15vh"
    >
      <!-- 标题部分 -->
      <div class="preference-title">
        <h3 style="font-weight: bold; color: #ff9966">
          为您提供个性化的新闻推荐
        </h3>
        <h4>请选择您感兴趣的新闻分类，我们将为您提供更精准的内容</h4>
      </div>

      <div class="category-selection">
        <!-- 已选择计数 -->
        <div class="selection-count">
          <span class="count-text"
            >选择感兴趣的分类 ({{ selectedCategories.length }}/5)</span
          >
          <span class="min-required">至少选择3项</span>
        </div>

        <!-- 分类选择区域 -->
        <div class="available-categories">
          <el-checkbox-group v-model="selectedCategories" :max="5">
            <el-checkbox
              v-for="category in categories"
              :key="category.categoryId"
              :label="category.categoryId"
              :disabled="
                selectedCategories.length >= 5 &&
                !selectedCategories.includes(category.categoryId)
              "
              class="category-checkbox"
              border
            >
              {{ category.categoryName }}
            </el-checkbox>
          </el-checkbox-group>
        </div>
      </div>

      <!-- 底部按钮 -->
      <div class="dialog-action-buttons">
        <el-button @click="goToPreferences" plain class="later-button"
          >稍后设置</el-button
        >
        <el-button
          type="primary"
          @click="confirmCategorySelection"
          :disabled="selectedCategories.length < 3"
          :loading="saving"
          class="confirm-button"
        >
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute, onBeforeRouteUpdate } from "vue-router";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import {
  ChatLineRound,
  View,
  Plus,
  Delete,
  Refresh,
  StarFilled,
  Menu,
  Sort,
  Check,
  QuestionFilled,
} from "@element-plus/icons-vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const router = useRouter();
const route = useRoute();

// 轮播图数据
const carouselItems = ref([]);

// 系统公告数据
const announcements = ref([]);

// 热门新闻数据
const hotNews = ref([]);

// 新闻列表数据
const newsList = ref([]);

// 推荐新闻数据
const recommendationNews = ref([]);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(6);
const totalNews = ref(0);

// 公告分页相关
const announcementCurrentPage = ref(1);
const announcementPageSize = ref(3);
const displayedAnnouncements = ref([]);

// 公告详情对话框相关
const announcementDialogVisible = ref(false);
const currentAnnouncement = ref({
  title: "",
  content: "",
  time: "",
});

// 用户偏好设置相关
const preferencesDialogVisible = ref(false);
const categories = ref([]);
const userPreferences = ref([]);
const selectedCategories = ref([]);
const tempPreferences = ref({});
const saving = ref(false);

// 会话状态变量 - 使用localStorage持久化
const sessionDismissed = ref(
  localStorage.getItem("sessionDismissed") === "true"
);

// 默认图片
const defaultImage = new URL(
  "@/assets/imgs/default-news-image.png",
  import.meta.url
).href;

// 获取用户ID
const getUserId = () => {
  const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
  return storedUser.userId;
};

// 显示公告详情
const showAnnouncementDetail = (notice) => {
  currentAnnouncement.value = notice;
  announcementDialogVisible.value = true;
};

// 获取轮播图数据
const fetchCarouselItems = async () => {
  try {
    const response = await request.get("/manage/banner/list");
    if (response.code === 200) {
      carouselItems.value = response.rows.map((item) => ({
        id: item.id,
        newsId: item.newsId,
        img: item.img,
        title: item.title,
        remark: item.remark,
      }));
    }
  } catch (error) {
    console.error("获取轮播图数据失败:", error);
  }
};

// 获取系统公告数据
const fetchAnnouncements = async () => {
  try {
    const response = await request.get("/system/notice/list");
    if (response.code === 200) {
      announcements.value = response.rows.map((item) => ({
        id: item.id,
        time: item.createTime,
        title: item.noticeTitle,
        content: item.noticeContent,
      }));
      // 初始化显示的公告
      updateDisplayedAnnouncements();
    }
  } catch (error) {
    console.error("获取系统公告数据失败:", error);
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

// 获取新闻列表数据
const fetchNewsList = async () => {
  try {
    const response = await request.get("/manage/news/list", {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        status: 1,
      },
    });
    if (response.code === 200) {
      newsList.value = response.rows.map((item) => ({
        newsId: item.newsId,
        title: item.title,
        summary: getSummary(item.content),
        img: item.img,
        createTime: item.createTime,
        commentCount: item.commentCount,
        viewCount: item.viewCount,
      }));
      totalNews.value = response.total;
      console.log("获取到的新闻列表数量:", totalNews.value);
    }
  } catch (error) {
    console.error("获取新闻列表数据失败:", error);
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
          limit: 10, // 获取10条推荐新闻
        },
      }
    );

    if (response.code === 200) {
      recommendationNews.value = response.rows.map((item) => ({
        id: item.newsId,
        newsId: item.newsId,
        title: item.title,
        cover: item.img || defaultImage,
        publishTime: item.createTime,
        viewCount: item.viewCount || 0,
      }));
      console.log("获取到推荐新闻数量:", recommendationNews.value.length);
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
  recommendationNews.value = [
    {
      id: 1,
      newsId: 1001,
      title: "推荐新闻标题 1",
      cover: defaultImage,
      publishTime: "2024-03-14",
      viewCount: 150,
    },
    {
      id: 2,
      newsId: 1002,
      title: "推荐新闻标题 2",
      cover: defaultImage,
      publishTime: "2024-03-14",
      viewCount: 120,
    },
    {
      id: 3,
      newsId: 1003,
      title: "推荐新闻标题 3",
      cover: defaultImage,
      publishTime: "2024-03-14",
      viewCount: 180,
    },
    {
      id: 4,
      newsId: 1004,
      title: "推荐新闻标题 4",
      cover: defaultImage,
      publishTime: "2024-03-14",
      viewCount: 200,
    },
    {
      id: 5,
      newsId: 1005,
      title: "推荐新闻标题 5",
      cover: defaultImage,
      publishTime: "2024-03-14",
      viewCount: 200,
    },
  ];
};

// 页面初始化
onMounted(() => {
  fetchCarouselItems();
  fetchAnnouncements();
  fetchHotNews();
  fetchNewsList();
  fetchRecommendedNews(); // 获取推荐新闻
  // 添加检查用户偏好设置
  checkUserPreferences();
});

// 监听路由变化，处理在首页刷新的情况
onBeforeRouteUpdate((to, from) => {
  // 如果是从其他页面返回首页，并且不是当前会话首次访问，则调用检查
  if (to.path === "/home" && from.path !== "/home") {
    // 仍然使用会话状态，只有未设置"稍后设置"时才检查
    if (!sessionDismissed.value) {
      checkUserPreferences();
    }
  }
});

// 检查用户是否已设置偏好
const checkUserPreferences = async () => {
  try {
    // 获取用户ID
    const userId = getUserId();
    if (!userId) {
      console.log("用户未登录，跳过偏好检查");
      return;
    }

    console.log("检查用户ID:", userId, "的偏好设置");

    // 如果当前会话已选择"稍后设置"，则不显示弹窗
    if (sessionDismissed.value) {
      console.log("当前会话已选择'稍后设置'，不显示弹窗");
      return;
    }

    // 先获取分类列表
    await fetchCategories();

    if (categories.value.length === 0) {
      console.log("未获取到分类数据，无法显示偏好设置");
      return;
    }

    // 查询用户偏好列表
    const response = await request.get("/manage/preferences/list", {
      params: { userId: userId },
    });

    console.log("用户偏好API响应:", response);

    // 只有在响应成功且有数据的情况下才认为用户有偏好设置
    if (response.code === 200 && response.rows && response.rows.length > 0) {
      // 用户已有偏好设置
      userPreferences.value = response.rows.map((pref) => ({
        ...pref,
        fromBackend: true,
        modified: false,
      }));
      console.log("用户已有偏好设置:", userPreferences.value.length, "项");
    } else {
      // 用户没有偏好设置，显示弹窗
      console.log("用户无偏好设置，打开设置对话框");
      // 先重置选择状态
      selectedCategories.value = [];
      tempPreferences.value = {};
      // 然后打开弹窗
      openPreferencesDialog();
    }
  } catch (error) {
    console.error("检查用户偏好设置失败:", error);
  }
};

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await request.get("/manage/categories/list");
    if (response.code === 200) {
      categories.value = response.rows;
      console.log("新闻分类列表:", categories.value);
    }
  } catch (error) {
    console.error("获取分类列表失败:", error);
  }
};

// 打开偏好设置对话框
const openPreferencesDialog = () => {
  // 初始化选择的分类
  selectedCategories.value = [];

  // 初始化临时优先级设置
  tempPreferences.value = {};

  // 为所有分类设置默认的中等优先级
  categories.value.forEach((category) => {
    tempPreferences.value[category.categoryId] = 2; // 默认中等优先级
  });

  preferencesDialogVisible.value = true;
};

// 确认分类选择并保存到后端
const confirmCategorySelection = async () => {
  // 验证是否选择了至少3个分类
  if (selectedCategories.value.length < 3) {
    ElMessage.warning("请至少选择3个分类");
    return;
  }

  try {
    // 显示加载状态
    saving.value = true;

    const userId = getUserId();
    if (!userId) {
      ElMessage.warning("用户未登录或无效");
      saving.value = false;
      return;
    }

    const savePromises = [];

    // 处理新增的分类
    for (const categoryId of selectedCategories.value) {
      const newPref = {
        userId: Number(userId),
        categoryId: Number(categoryId),
        priority: Number(tempPreferences.value[categoryId] || 2),
      };
      const addPromise = request.post("/manage/preferences", newPref);
      savePromises.push(addPromise);
    }

    // 等待所有保存操作完成
    const results = await Promise.all(savePromises);
    const allSuccess = results.every((res) => res.code === 200);

    if (allSuccess) {
      ElMessage.success("喜好设置保存成功");
      // 关闭对话框
      preferencesDialogVisible.value = false;

      // 重新获取推荐新闻
      await fetchRecommendedNews();

      // 延迟一点时间后刷新新闻列表，确保推荐系统有时间更新
      setTimeout(async () => {
        await fetchNewsList();
      }, 1000);
    } else {
      ElMessage.warning("部分设置保存失败，请重试");
    }
  } catch (error) {
    console.error("保存用户喜好失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  } finally {
    saving.value = false;
  }
};

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(
    (cat) => cat.categoryId === categoryId
  );
  return category ? category.categoryName : "未知分类";
};

// 根据分类ID生成一致的颜色
const getCategoryColor = (categoryId) => {
  // 预定义的颜色数组，与系统风格协调
  const colors = [
    "#FF9966", // 主题色调
    "#FF8C42",
    "#FF7733",
    "#FF6633",
    "#FFA07A",
    "#FF7F50",
    "#E9967A",
    "#FF8C69",
    "#FF9E7D",
    "#FFAD86",
  ];

  // 使用分类ID作为索引来选择颜色，确保相同ID始终获得相同颜色
  const index = categoryId % colors.length;
  return colors[index];
};

// 跳转到偏好设置页面
const goToPreferences = () => {
  // 标记当前会话已选择"稍后设置"，存储到localStorage中
  sessionDismissed.value = true;
  localStorage.setItem("sessionDismissed", "true");
  console.log("已设置当前会话的稍后设置状态");

  // 关闭弹窗并跳转
  preferencesDialogVisible.value = false;
  router.push("/user/preferences");
};

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchNewsList();
};

// 跳转到新闻详情
const goToNewsDetail = (newsId) => {
  console.log("正在跳转到新闻详情：", newsId);
  router.push(`/newsDetail?newsId=${newsId}`);
};

// 处理公告分页变化
const handleAnnouncementPageChange = (page) => {
  announcementCurrentPage.value = page;
  updateDisplayedAnnouncements();
};

// 更新显示的公告
const updateDisplayedAnnouncements = () => {
  const start =
    (announcementCurrentPage.value - 1) * announcementPageSize.value;
  const end = start + announcementPageSize.value;
  displayedAnnouncements.value = announcements.value.slice(start, end);
};
</script>

<style scoped>
.content {
  max-width: 1400px;
  margin: 20px auto;
  padding: 0 40px;
  background-color: var(--background-light);
}

.top-section {
  display: flex;
  height: 560px;
  gap: 20px;
  margin-bottom: 2px; /* 减小底部边距 */
  padding: 15px;
  background: #fff;
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
}

.carousel-section {
  flex: 7;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: var(--shadow-md);
}

.carousel-item-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.5));
  pointer-events: none;
}

.carousel-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px;
  color: white;
  font-size: 20px;
  font-weight: bold;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
  background: linear-gradient(transparent, rgba(255, 153, 102, 0.8));
}

.side-section {
  flex: 3;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.announcement-box {
  background: linear-gradient(to bottom right, #fff, var(--background-light));
  border: 1px solid var(--border-color);
  padding: 12px 12px 8px 12px; /* 减小底部内边距 */
  border-radius: 8px;
  box-shadow: 0 2px 12px var(--shadow-color);
  transition: all 0.3s ease;
  flex: 1;
  height: 290px; /* 进一步减小固定高度 */
  display: flex; /* 确保flex布局 */
  flex-direction: column;
}

.announcement-box h3 {
  margin-top: 0;
  margin-bottom: 8px; /* 减小标题底部间距 */
  font-size: 18px;
  padding-bottom: 8px;
}

.hot-news-box {
  background: linear-gradient(to bottom right, #fff, var(--background-light));
  border: 1px solid var(--border-color);
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 12px var(--shadow-color);
  transition: all 0.3s ease;
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-bottom: 15px; /* 添加底部间距，为推荐阅读留出空间 */
}

.announcement-box:hover,
.hot-news-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px var(--shadow-color);
}

.announcement-list {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
  height: auto; /* 自适应高度 */
  overflow-y: auto; /* 添加滚动条 */
  margin-bottom: 3px; /* 减小与分页的间距 */
}

.hot-news-list {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
}

.announcement-list li {
  margin-bottom: 6px; /* 减小行间距 */
  padding: 4px 0; /* 减小上下内边距 */
  font-size: 14px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.announcement-list li:hover {
  transform: translateY(-2px);
}

.notice-item {
  padding: 6px; /* 减小内边距 */
  border-radius: 4px;
  transition: all 0.3s ease;
  margin: 0 0 0 0;
}

.notice-item:hover {
  background-color: var(--background-light);
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.notice-content {
  display: block; /* 标题单独一行 */
  font-size: 13px; /* 稍微减小字体 */
  margin-bottom: 2px; /* 与时间的间距 */
}

.notice-time {
  color: #909399;
  margin-right: 10px;
  font-size: 12px; /* 减小时间字体 */
}

.announcement-pagination {
  margin-top: 0; /* 移除顶部间距 */
  display: flex;
  justify-content: center;
}

.announcement-pagination :deep(.el-pagination) {
  --el-pagination-button-size: 22px; /* 减小分页按钮尺寸 */
  --el-pagination-button-margin: 0 2px;
}

.announcement-pagination
  :deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: var(--primary-color);
  color: white;
}

.announcement-pagination
  :deep(.el-pagination.is-background .el-pager li:not(.is-active):hover) {
  color: var(--primary-color);
}

.hot-news-list {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
}

/* 移除滚动条效果 */
.hot-news-box:hover .hot-news-list {
  overflow-y: hidden; /* 移除滚动条 */
}

.hot-news-list li {
  display: flex;
  align-items: center;
  padding: 10px;
  gap: 10px;
  border-radius: 6px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.hot-news-list li:hover {
  background-color: var(--background-light);
  transform: translateX(5px);
}

.hot-rank {
  width: 20px;
  text-align: center;
  color: #909399;
}

.top-three {
  color: var(--primary-color);
  font-weight: bold;
}

.bottom-section {
  display: flex;
  gap: 20px;
  margin-top: 1px; /* 最小化顶部间距 */
}

.news-section {
  width: 920px; /* 设置固定宽度 */
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.sidebar {
  width: 300px; /* 设置固定宽度 */
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.news-title-header {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary-color);
  color: #333;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px; /* 减小底部间距 */
  padding-bottom: 8px; /* 减小底部内边距 */
  border-bottom: 2px solid var(--primary-color);
  color: #333;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 15px;
}

.news-item {
  display: flex;
  gap: 20px;
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  cursor: pointer;
}

.news-item:active {
  background-color: transparent !important;
}

.news-item:hover {
  transform: translateX(5px);
  box-shadow: var(--shadow-sm);
}

.news-image {
  width: 200px;
  height: 134px;
  overflow: hidden;
  border-radius: 8px;
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
  margin: 0 0 10px;
  font-size: 18px;
  color: #303133;
}

.news-title:hover {
  color: var(--primary-color);
}

.news-summary {
  margin: 0 0 10px;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.news-meta {
  margin-top: auto;
  color: var(--text-secondary);
  font-size: 12px;
  display: flex;
  gap: 15px;
}

.news-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.recommendation-section {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 10px; /* 减小底部间距 */
  margin-top: 5px; /* 添加顶部间距 */
}

.recommendation-section .section-title {
  margin-bottom: 15px; /* 为推荐阅读特别调整标题底部间距 */
}

.recommended-news-list {
  display: flex;
  flex-direction: column;
  gap: 5px; /* 增加间距 */
  max-height: 1200px; /* 限制最大高度 */
  overflow-y: auto; /* 添加滚动条 */
}

.recommended-news-item {
  display: flex;
  gap: 15px; /* 增加图片与内容间距 */
  padding: 15px 5px; /* 增加上下内边距，减少左右内边距 */
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
  width: 100px; /* 增加图片尺寸 */
  height: 70px; /* 增加图片尺寸 */
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.recommended-news-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 3px 0; /* 添加内边距 */
}

.recommended-news-info h4 {
  font-size: 14px; /* 增加标题字体 */
  font-weight: bold;
  margin: 0 0 8px 0; /* 增加与元信息的间距 */
  line-height: 1.5;
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
  font-size: 11px; /* 减小元信息字体 */
}

.recommendation-time {
  color: #999;
}

.recommendation-views {
  display: flex;
  align-items: center;
  gap: 4px;
}

h2,
h3 {
  margin: 0 0 10px;
  color: #303133;
}

h3 {
  font-size: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.pagination :deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: var(--primary-color);
}

.hot-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: calc(100% - 30px); /* 调整宽度以适应布局 */
}

/* 响应式布局 */
@media screen and (max-width: 1200px) {
  .content {
    padding: 0 20px;
  }

  .bottom-section {
    flex-direction: column;
  }

  .news-section,
  .sidebar {
    width: 100%;
  }

  .news-image {
    width: 100%;
  }
}

/* 公告详情对话框样式 */
.announcement-container {
  padding: 20px;
}

.announcement-time {
  text-align: center;
  color: #909399;
  margin-bottom: 35px;
  font-size: 14px;
}

.announcement-content {
  line-height: 1.6;
  word-break: break-word;
}

.announcement-content p {
  margin: 0 0 10px;
}

.announcement-content img {
  max-width: 100%;
  height: auto;
}

.announcement-content a {
  color: var(--primary-color, #409eff);
  text-decoration: none;
}

.announcement-content a:hover {
  text-decoration: underline;
}

/* 偏好设置弹窗样式 */
.preferences-dialog :deep(.el-dialog) {
  border-radius: 4px;
  overflow: hidden;
}

.preferences-dialog :deep(.el-dialog__header) {
  display: none; /* 隐藏标题栏 */
}

.preferences-dialog :deep(.el-dialog__body) {
  padding: 0;
}

/* 弹窗标题部分 - 橙色背景 */
.preference-title {
  padding: 20px;
  text-align: center;
}

.preference-title h3 {
  font-size: 18px;
  margin: 0 0 15px 0;
  font-weight: 500;
}

.preference-title p {
  font-size: 14px;
  margin: 0;
  font-weight: normal;
  opacity: 0.9;
}

/* 分类选择区域 */
.category-selection {
  padding: 20px;
  background-color: #f8f9fa;
}

/* 已选计数 */
.selection-count {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.count-text {
  font-size: 15px;
  color: #333;
}

.min-required {
  font-size: 13px;
  padding: 2px 8px;
  background-color: rgba(255, 153, 102, 0.1);
  border-radius: 12px;
}

/* 分类选择样式 */
.available-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.category-checkbox {
  margin-bottom: 8px;
}

.category-checkbox :deep(.el-checkbox__label) {
  font-size: 14px;
}

/* 底部按钮区域 */
.dialog-action-buttons {
  display: flex;
  justify-content: space-between;
  padding: 15px 20px;
  border-top: 1px solid #ebeef5;
  background-color: #fff;
}

.later-button {
  color: #606266;
  border-color: #dcdfe6;
}

.confirm-button {
  background-color: #409eff;
}

/* 优化复选框样式 */
.category-checkbox :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #ff9966;
}

.category-checkbox :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #ff9966;
  border-color: #ff9966;
}
</style>
