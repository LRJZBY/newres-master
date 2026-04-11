<template>
  <div class="likes">
    <h2>我的点赞</h2>

    <div class="filter-section">
      <el-input
        v-model="searchTitle"
        placeholder="搜索标题"
        class="filter-item"
        clearable
        @keyup.enter="fetchLikesList"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-button type="primary" @click="fetchLikesList">
        <el-icon><Search /></el-icon>查询
      </el-button>

      <el-button @click="resetSearch">
        <el-icon><Refresh /></el-icon>重置
      </el-button>
    </div>

    <div v-loading="loading">
      <!-- 点赞列表 -->
      <div v-if="likesList.length > 0" class="likes-list">
        <div v-for="item in likesList" :key="item.likeId" class="like-item">
          <div class="like-content" @click="goToDetail(item.newsId)">
            <div class="like-image">
              <img
                :src="item.img || defaultImage"
                alt="新闻图片"
                @error="handleImageError"
              />
            </div>
            <div class="like-main">
              <h3 class="like-title">{{ item.title }}</h3>
              <p class="like-summary">{{ item.summary }}</p>
              <div class="like-meta">
                <span
                  ><el-icon><Timer /></el-icon
                  >{{ formatDate(item.createTime) }}</span
                >
                <span
                  ><el-icon><Folder /></el-icon
                  >{{ item.categoryName || "未分类" }}</span
                >
              </div>
            </div>
          </div>
          <div class="like-actions">
            <el-button
              type="danger"
              link
              @click="showCancelConfirm(item.likeId)"
            >
              取消点赞
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无点赞记录" />

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
          layout="prev, pager, next, total"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";

const router = useRouter();
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const likesList = ref([]);
const searchTitle = ref("");

// 默认图片
const defaultImage = new URL(
  "@/assets/imgs/default-news-image.png",
  import.meta.url
).href;

// 图片加载错误处理
const handleImageError = (event) => {
  event.target.src = defaultImage;
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString();
};

// 获取点赞列表
const fetchLikesList = async () => {
  loading.value = true;

  try {
    const userInfo = JSON.parse(localStorage.getItem("user") || "{}");
    if (!userInfo.userId) {
      ElMessage.warning("请先登录");
      return;
    }

    // 构建查询参数
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      userId: userInfo.userId,
    };

    // 如果有搜索标题，添加到参数中
    if (searchTitle.value.trim()) {
      params.title = searchTitle.value.trim();
    }

    const response = await request.get("/manage/likes/list", { params });

    if (response.code === 200) {
      likesList.value = response.rows.map((item) => ({
        ...item,
        img: item.img || defaultImage, // 确保有图片
      }));
      total.value = response.total;
    } else {
      ElMessage.error(response.msg || "获取点赞列表失败");
    }
  } catch (error) {
    console.error("获取点赞列表失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  } finally {
    loading.value = false;
  }
};

// 跳转到新闻详情
const goToDetail = (newsId) => {
  if (!newsId) {
    ElMessage.warning("新闻ID不存在");
    return;
  }
  router.push(`/newsDetail?newsId=${newsId}`);
};

// 显示取消点赞确认弹窗
const showCancelConfirm = (likeId) => {
  ElMessageBox.confirm("确定要取消点赞吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      // 用户确认取消点赞
      cancelLike(likeId);
    })
    .catch(() => {
      // 用户取消操作
      ElMessage.info("已取消操作");
    });
};

// 取消点赞
const cancelLike = async (likeId) => {
  try {
    const response = await request.delete(`/manage/likes/${likeId}`);

    if (response.code === 200) {
      ElMessage.success("取消点赞成功");
      fetchLikesList(); // 刷新列表
    } else {
      ElMessage.error(response.msg || "取消点赞失败");
    }
  } catch (error) {
    console.error("取消点赞失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  }
};

// 重置搜索
const resetSearch = () => {
  searchTitle.value = "";
  currentPage.value = 1;
  fetchLikesList();
};

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchLikesList();
};

onMounted(() => {
  fetchLikesList();
});
</script>

<style scoped>
.likes {
  min-height: 100%;
}

.likes h2 {
  margin-bottom: 24px;
  color: #1f2937;
  font-size: 20px;
}

.filter-section {
  margin-bottom: 24px;
  display: flex;
  gap: 16px;
}

.filter-item {
  width: 160px;
}

.likes-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.like-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: background-color 0.3s;
}

.like-item:hover {
  background-color: #f5f5f7;
}

.like-content {
  display: flex;
  gap: 12px;
  flex: 1;
}

.like-main {
  flex: 1;
}

.like-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: #1f2937;
  line-height: 1.4;
}

.like-summary {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.like-image {
  width: 100px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.like-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.like-meta {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  gap: 16px;
  color: #9ca3af;
  font-size: 13px;
}

.like-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.like-actions {
  margin-left: auto;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
