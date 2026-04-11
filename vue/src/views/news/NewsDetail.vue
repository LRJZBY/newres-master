<template>
  <Header />
  <div class="news-detail-container">
    <div class="news-content-layout">
      <!-- 左侧新闻详情内容 -->
      <div class="news-main-content">
        <!-- 新闻头部信息 -->
        <div class="news-header">
          <div class="news-title">{{ newsDetail.title }}</div>
          <div class="news-meta">
            <span class="author">
              <el-icon><Avatar /></el-icon>
              {{ newsDetail.username }}
            </span>
            <span class="publish-time">
              <el-icon><Timer /></el-icon>
              {{ newsDetail.create_time }}
            </span>
            <span class="category">
              <el-icon><Collection /></el-icon>
              {{ newsDetail.categoryName }}
            </span>
          </div>
        </div>

        <!-- 新闻封面图 -->
        <div class="news-cover-image" v-if="newsDetail.img">
          <el-image
            :src="newsDetail.img"
            fit="cover"
            @error="handleImageError"
          />
        </div>

        <!-- 新闻互动信息 -->
        <div class="news-interaction">
          <div class="interaction-item">
            <el-button
              icon="Pointer"
              :type="isLiked ? 'primary' : ''"
              @click="toggleLike"
            >
              点赞({{ newsDetail.likeCount }})
            </el-button>
          </div>
          <div class="interaction-item">
            <el-button
              icon="Star"
              :type="isCollected ? 'primary' : ''"
              @click="toggleCollect"
            >
              收藏({{ newsDetail.collectCount }})
            </el-button>
          </div>
          <div class="interaction-item">
            <el-button icon="ChatSquare" @click="scrollToComments()">
              评论({{ newsDetail.commentCount }})
            </el-button>
          </div>
          <div class="interaction-item">
            <el-button icon="View">
              浏览量：({{ newsDetail.viewCount }})
            </el-button>
          </div>
        </div>

        <!-- 新闻内容 -->
        <div class="news-content" v-html="newsDetail.content"></div>

        <!-- 评论区域 -->
        <el-card class="comment-section" ref="commentSectionRef">
          <template #header>
            <div class="comment-header">
              <h3>评论区 ({{ newsDetail.commentCount || 0 }})</h3>
            </div>
          </template>

          <!-- 发表评论 -->
          <div class="comment-form">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="3"
              placeholder="写下您的评论..."
              :maxlength="500"
              show-word-limit
            />
            <div class="comment-form-footer">
              <el-button
                type="primary"
                @click="submitComment"
                :loading="submitting"
              >
                发表评论
              </el-button>
            </div>
          </div>

          <!-- 评论列表 -->
          <div class="comments-list" v-loading="commentsLoading">
            <template v-if="comments.length > 0">
              <div
                v-for="comment in comments"
                :key="comment.commentId"
                class="comment-item"
              >
                <div class="comment-user">
                  <el-avatar
                    :size="40"
                    :src="comment.avatar || defaultAvatar"
                  />
                  <div class="comment-info">
                    <div class="comment-username">{{ comment.username }}</div>
                    <div class="comment-time">
                      {{ formatDateTime(comment.createTime) }}
                    </div>
                  </div>
                </div>
                <div class="comment-content" v-html="comment.content"></div>
                <div class="comment-actions">
                  <el-button
                    type="danger"
                    link
                    size="small"
                    v-if="canDeleteComment(comment)"
                    @click="handleDeleteComment(comment.commentId)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </template>
            <el-empty v-else description="暂无评论" />
          </div>

          <!-- 分页 -->
          <div class="pagination" v-if="total > 0">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              @current-change="handlePageChange"
              layout="prev, pager, next"
              background
            />
          </div>
        </el-card>
      </div>

      <!-- 右侧热门/推荐新闻 -->
      <div class="news-sidebar">
        <!-- 热点新闻 -->
        <div class="hot-news-section">
          <h3 class="section-title">热点新闻</h3>
          <div class="hot-news-list">
            <div
              v-for="(news, index) in hotNews"
              :key="news.newsId"
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
              v-for="recommendation in recommendationNews"
              :key="recommendation.id"
              class="recommended-news-item"
              @click="goToNewsDetail(recommendation.newsId)"
            >
              <img
                :src="recommendation.cover"
                :alt="recommendation.title"
                class="recommended-news-image"
                @error="handleImageError"
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
</template>

<script setup>
import { ref, onMounted, watch, computed, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import {
  Avatar,
  Timer,
  Collection,
  Star,
  View,
  ChatSquare,
} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";

const router = useRouter();
const route = useRoute();
const newsId = ref(route.query.newsId);
const isShowFullTitle = false; // 控制是否显示完整标题

// 新闻详情数据
const newsDetail = ref({
  newsId: null,
  title: "",
  content: "",
  create_time: "",
  username: "",
  categoryName: "",
  img: "",
  likeCount: 0,
  collectCount: 0,
  commentCount: 0,
  viewCount: 0,
  comments: [],
});

// 热门新闻数据 - 使用静态数据保持和Home页一致
const hotNews = ref([]);

// 推荐新闻数据
const recommendationNews = ref([]);

// 用户交互状态
const isLiked = ref(false);
const isCollected = ref(false);
const showComment = ref(false);
const commentContent = ref("");

// 默认图片
const defaultImage = new URL(
  "@/assets/imgs/default-news-image.png",
  import.meta.url
).href;

// 图片加载失败时使用默认图片
const handleImageError = (event) => {
  event.target.src = defaultImage;
};

// 获取新闻详情
const fetchNewsDetail = async () => {
  if (!newsId.value) {
    ElMessage.error("新闻ID不能为空");
    return;
  }

  try {
    // 确保newsId是数字类型
    const id = parseInt(newsId.value);
    if (isNaN(id)) {
      ElMessage.error("新闻ID格式不正确");
      return;
    }

    const response = await request.get(`/manage/news/${id}`);
    if (response.code === 200) {
      const data = response.data;
      newsDetail.value = {
        newsId: data.newsId,
        title: data.title || "未知标题",
        content: data.content || "暂无内容",
        create_time: data.createTime || new Date().toLocaleDateString(),
        username: data.username || "未知作者",
        categoryName: data.categoryName || "未分类",
        img: data.img || "",
        likeCount: data.likeCount || 0,
        collectCount: data.collectCount || 0,
        commentCount: data.commentCount || 0,
        viewCount: data.viewCount || 0,
        comments: data.comments || [],
      };

      // 检查用户是否已点赞/收藏
      checkUserInteraction();

      console.log("获取到的新闻详情:", newsDetail.value);
    } else {
      ElMessage.warning(response.msg || "获取新闻详情失败");
    }
  } catch (error) {
    console.error("获取新闻详情失败:", error);
    ElMessage.error("网络错误，请稍后再试");
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
      // 确保推荐列表中不包含当前正在浏览的新闻
      const currentNewsId = parseInt(newsId.value);
      const filteredRecommendations = response.rows.filter(
        (item) => item.newsId !== currentNewsId
      );

      recommendationNews.value = filteredRecommendations.map((item) => ({
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

// 检查用户是否已点赞/收藏
const checkUserInteraction = async () => {
  try {
    if (!newsId.value) return;

    const userInfo = JSON.parse(localStorage.getItem("user") || "{}");
    if (!userInfo.userId) return;

    // 检查是否已点赞
    const likeResponse = await request.get("/manage/likes/list", {
      params: {
        userId: userInfo.userId,
        newsId: parseInt(newsId.value),
      },
    });

    if (
      likeResponse.code === 200 &&
      likeResponse.rows &&
      likeResponse.rows.length > 0
    ) {
      isLiked.value = true;
      // 保存点赞ID，用于取消点赞
      likeId.value = likeResponse.rows[0].likeId;
    } else {
      isLiked.value = false;
      likeId.value = null;
    }

    // 检查是否已收藏
    const favoriteResponse = await request.get("/manage/favorites/list", {
      params: {
        userId: userInfo.userId,
        newsId: parseInt(newsId.value),
      },
    });

    if (
      favoriteResponse.code === 200 &&
      favoriteResponse.rows &&
      favoriteResponse.rows.length > 0
    ) {
      isCollected.value = true;
      // 保存收藏ID，用于取消收藏
      favoriteId.value = favoriteResponse.rows[0].favoriteId;
    } else {
      isCollected.value = false;
      favoriteId.value = null;
    }
  } catch (error) {
    console.error("检查用户交互状态失败:", error);
  }
};

// 新增两个变量存储点赞ID和收藏ID
const likeId = ref(null);
const favoriteId = ref(null);

// 处理点赞
const toggleLike = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem("user") || "{}");
    if (!userInfo.userId) {
      ElMessage.warning("请先登录后再点赞");
      return;
    }

    if (!isLiked.value) {
      // 添加点赞
      const response = await request.post("/manage/likes", {
        userId: userInfo.userId,
        newsId: parseInt(newsId.value),
        createBy: userInfo.username,
      });

      if (response.code === 200) {
        isLiked.value = true;
        likeId.value = response.data; // 假设接口返回新增的点赞ID
        newsDetail.value.likeCount += 1;
        ElMessage.success("点赞成功");
        fetchInteractionCounts(); // 获取最新的交互数据
      } else {
        ElMessage.error(response.msg || "点赞失败");
      }
    } else {
      // 取消点赞
      if (!likeId.value) {
        ElMessage.error("无法获取点赞记录ID");
        return;
      }

      const response = await request.delete(`/manage/likes/${likeId.value}`);

      if (response.code === 200) {
        isLiked.value = false;
        likeId.value = null;
        newsDetail.value.likeCount = Math.max(
          0,
          newsDetail.value.likeCount - 1
        );
        ElMessage.success("已取消点赞");
        fetchInteractionCounts(); // 获取最新的交互数据
      } else {
        ElMessage.error(response.msg || "取消点赞失败");
      }
    }
  } catch (error) {
    console.error("处理点赞失败:", error);
    ElMessage.error("操作失败，请稍后再试");
  }
};

// 处理收藏
const toggleCollect = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem("user") || "{}");
    if (!userInfo.userId) {
      ElMessage.warning("请先登录后再收藏");
      return;
    }

    if (!isCollected.value) {
      // 添加收藏
      const response = await request.post("/manage/favorites", {
        userId: userInfo.userId,
        newsId: parseInt(newsId.value),
        createBy: userInfo.username,
      });

      if (response.code === 200) {
        isCollected.value = true;
        favoriteId.value = response.data; // 假设接口返回新增的收藏ID
        newsDetail.value.collectCount += 1;
        ElMessage.success("收藏成功");
        fetchInteractionCounts(); // 获取最新的交互数据
      } else {
        ElMessage.error(response.msg || "收藏失败");
      }
    } else {
      // 取消收藏
      if (!favoriteId.value) {
        ElMessage.error("无法获取收藏记录ID");
        return;
      }

      const response = await request.delete(
        `/manage/favorites/${favoriteId.value}`
      );

      if (response.code === 200) {
        isCollected.value = false;
        favoriteId.value = null;
        newsDetail.value.collectCount = Math.max(
          0,
          newsDetail.value.collectCount - 1
        );
        ElMessage.success("已取消收藏");
        fetchInteractionCounts(); // 获取最新的交互数据
      } else {
        ElMessage.error(response.msg || "取消收藏失败");
      }
    }
  } catch (error) {
    console.error("处理收藏失败:", error);
    ElMessage.error("操作失败，请稍后再试");
  }
};

// 评论相关的状态
const comments = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);

// 默认头像
const defaultAvatar =
  "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

// 获取当前用户信息
const currentUser = computed(() => {
  try {
    return JSON.parse(localStorage.getItem("user") || "{}");
  } catch (e) {
    return {};
  }
});

// 判断是否可以删除评论
const canDeleteComment = (comment) => {
  return comment.userId === currentUser.value.userId;
};

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return "未知时间";
  try {
    const date = new Date(dateString);
    return date.toLocaleString();
  } catch (e) {
    return "时间格式错误";
  }
};

// 获取评论列表
const fetchComments = async () => {
  if (!newsId.value) return;

  commentsLoading.value = true;
  try {
    const response = await request.get("/manage/comments/list", {
      params: {
        newsId: newsId.value,
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      },
    });

    if (response.code === 200) {
      comments.value = response.rows || [];
      total.value = response.total || 0;
    } else {
      ElMessage.warning(response.msg || "获取评论失败");
      comments.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error("获取评论列表失败:", error);
    ElMessage.error("网络错误，请稍后再试");
    comments.value = [];
    total.value = 0;
  } finally {
    commentsLoading.value = false;
  }
};

// 提交评论
const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning("请输入评论内容");
    return;
  }

  if (!currentUser.value.userId) {
    ElMessage.warning("请先登录后再评论");
    return;
  }

  submitting.value = true;
  try {
    const comment = {
      newsId: newsId.value,
      userId: currentUser.value.userId,
      content: commentContent.value.trim(),
      createBy: currentUser.value.username,
    };

    const response = await request.post("/manage/comments", comment);

    if (response.code === 200) {
      ElMessage.success("评论发表成功");
      commentContent.value = ""; // 清空评论框
      currentPage.value = 1; // 重置到第一页
      fetchComments(); // 重新获取评论列表

      // 更新评论数量
      if (newsDetail.value) {
        newsDetail.value.commentCount =
          (newsDetail.value.commentCount || 0) + 1;
      }
    } else {
      ElMessage.error(response.msg || "评论发表失败");
    }
  } catch (error) {
    console.error("发表评论失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  } finally {
    submitting.value = false;
  }
};

// 删除评论
const handleDeleteComment = (commentId) => {
  ElMessageBox.confirm("确定要删除这条评论吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const response = await request.delete(`/manage/comments/${commentId}`);

        if (response.code === 200) {
          ElMessage.success("评论删除成功");
          fetchComments(); // 重新获取评论列表

          // 更新评论数量
          if (newsDetail.value) {
            newsDetail.value.commentCount = Math.max(
              (newsDetail.value.commentCount || 0) - 1,
              0
            );
          }
        } else {
          ElMessage.error(response.msg || "删除失败");
        }
      } catch (error) {
        console.error("删除评论失败:", error);
        ElMessage.error("网络错误，请稍后再试");
      }
    })
    .catch(() => {
      // 用户取消删除操作
    });
};

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchComments();
};

// 跳转到新闻详情
const goToNewsDetail = (id) => {
  if (id === parseInt(newsId.value)) return; // 避免重复加载当前页面
  router.push(`/newsDetail?newsId=${id}`);
};

// 修改 recordHistory 函数，增加一个标记防止重复记录
let historyRecorded = false;

const recordHistory = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem("user") || "{}");
    // 检查是否已经记录过，或者用户未登录，或者没有新闻ID
    if (historyRecorded || !userInfo.userId || !newsId.value) return;

    // 标记为已记录，防止重复调用
    historyRecorded = true;

    // 添加浏览记录
    await request.post("/manage/history", {
      userId: userInfo.userId,
      newsId: parseInt(newsId.value),
      createBy: userInfo.username,
    });

    // 不需要手动更新浏览量，后端会处理
  } catch (error) {
    console.error("记录浏览历史失败:", error);
  }
};

// 在 ref 定义区域添加评论区域引用
const commentSectionRef = ref(null);

// 添加 commentsLoading 来单独控制评论区域的加载状态
const commentsLoading = ref(false);

// 添加滚动到评论区的方法
const scrollToComments = () => {
  nextTick(() => {
    if (commentSectionRef.value) {
      commentSectionRef.value.$el.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    }
  });
};

// 修改 onMounted 钩子，确保初始化时就加载评论
onMounted(() => {
  if (newsId.value) {
    fetchNewsDetail(); // 先获取新闻详情
    fetchHotNews(); // 获取热门新闻
    fetchRecommendedNews(); // 获取推荐新闻
    fetchComments(); // 获取评论列表

    // 延迟记录浏览历史，避免与获取详情的自动增加冲突
    setTimeout(() => {
      recordHistory(); // 记录浏览历史
    }, 100);
  }
});

// 修改 watch 函数，确保在 newsId 变化时也重新加载评论
watch(
  () => route.query.newsId,
  (newVal) => {
    if (newVal) {
      newsId.value = newVal;
      // 重置标记，允许为新的新闻记录历史
      historyRecorded = false;
      // 获取新闻详情
      fetchNewsDetail();
      // 获取评论列表
      fetchComments();
      // 延迟记录浏览历史
      setTimeout(() => {
        recordHistory();
      }, 100);
    }
  },
  { immediate: true }
);

// 修改 fetchInteractionCounts 函数实现方式，移除对不存在接口的依赖
const fetchInteractionCounts = async () => {
  if (!newsId.value) return;

  try {
    // 直接重新获取新闻详情来获取最新的点赞和收藏数量
    const id = parseInt(newsId.value);
    if (isNaN(id)) return;

    const response = await request.get(`/manage/news/${id}`);

    if (response.code === 200 && response.data) {
      // 只更新交互相关的数据，不影响其他属性
      if (newsDetail.value) {
        newsDetail.value.likeCount = response.data.likeCount || 0;
        newsDetail.value.collectCount = response.data.collectCount || 0;
        newsDetail.value.viewCount = response.data.viewCount || 0;
        newsDetail.value.commentCount = response.data.commentCount || 0;
      }
    }
  } catch (error) {
    console.error("获取交互数据失败:", error);
    // 这里不显示错误给用户，避免影响用户体验
  }
};
</script>

<style scoped>
.news-detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px 40px;
  background-color: var(--bg-color, #f5f7fa);
}

.news-content-layout {
  display: flex;
  gap: 24px;
}

.news-main-content {
  flex: 7;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.news-sidebar {
  margin-top: 30px;
  flex: 3;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.news-header {
  margin-bottom: 20px;
}

.news-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 15px;
  line-height: 1.3;
  color: #333;
}

.news-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: #666;
  font-size: 14px;
  margin-bottom: 16px;
}

.news-cover-image {
  width: 95%;
  max-height: 700px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 20px;
}

.news-cover-image .el-image {
  width: 100%;
  height: 100%;
  max-height: 500px;
  object-fit: contain;
}

.news-interaction {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.news-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  margin-bottom: 24px;
  overflow-wrap: break-word;
}

/* 控制新闻内容中的图片大小 */
.news-content :deep(img) {
  max-width: 100%;
  height: auto;
  margin: 10px 0;
  border-radius: 4px;
}

.comment-section {
  margin-top: 20px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.comment-form {
  margin-bottom: 24px;
}

.comment-form-footer {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  padding: 16px;
  border-radius: 8px;
  background-color: #f9fafb;
  transition: background-color 0.3s;
}

.comment-item:hover {
  background-color: #f3f4f6;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-info {
  display: flex;
  flex-direction: column;
}

.comment-username {
  font-weight: 500;
  color: #1f2937;
}

.comment-time {
  font-size: 12px;
  color: #6b7280;
}

.comment-content {
  margin: 8px 0;
  color: #4b5563;
  line-height: 1.5;
  white-space: pre-wrap;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

/* 热门新闻和推荐阅读样式 - 与Home页完全一致 */
.hot-news-section,
.recommended-news-section {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  color: #333;
  border-bottom: 2px solid var(--primary-color);
}

/* 热门新闻样式 */
.hot-news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-news-item {
  display: flex;
  align-items: flex-start;
  padding: 8px 0;
  cursor: pointer;
  transition: all 0.2s;
}

.hot-news-item:hover {
  color: var(--primary-color, #409eff);
}

.rank-number {
  width: 24px;
  height: 24px;
  background: #ddd;
  color: #fff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.rank-number.top-three {
  background: var(--primary-color, #409eff);
}

.hot-news-info {
  flex: 1;
}

.hot-news-info h4 {
  font-size: 14px;
  margin: 0 0 5px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.views {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 推荐新闻样式 */
.recommended-news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.recommended-news-item {
  display: flex;
  gap: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.recommended-news-item:hover .recommended-news-info h4 {
  color: var(--primary-color, #409eff);
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

@media (max-width: 768px) {
  .news-content-layout {
    flex-direction: column;
  }

  .news-main-content,
  .news-sidebar {
    flex: none;
    width: 100%;
  }
}
.comment-content {
  margin: 8px 0;
  color: #4b5563;
  line-height: 1.5;
  white-space: pre-wrap;
}

/* 控制富文本中的图片大小 */
.comment-content img {
  max-width: 100%;
  height: auto;
  margin: 10px 0;
  border-radius: 4px;
}

/* 处理富文本中的链接样式 */
.comment-content a {
  color: #409eff;
  text-decoration: none;
}

.comment-content a:hover {
  text-decoration: underline;
}
</style>
