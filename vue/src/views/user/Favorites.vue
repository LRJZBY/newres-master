<template>
  <div class="favorites">
    <h2>我的收藏</h2>

    <div class="filter-section">
      <el-input
        v-model="searchTitle"
        placeholder="搜索标题"
        clearable
        @keyup.enter="fetchFavoritesList"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <div style="margin-top: 10px"></div>
      <el-button type="primary" @click="fetchFavoritesList">
        <el-icon><Search /></el-icon>查询
      </el-button>

      <el-button @click="resetSearch">
        <el-icon><Refresh /></el-icon>重置
      </el-button>
    </div>

    <div v-loading="loading">
      <!-- 收藏列表 -->
      <div v-if="favoriteList.length > 0" class="favorite-list">
        <div
          v-for="item in favoriteList"
          :key="item.favoriteId"
          class="favorite-item"
        >
          <img
            :src="item.img || defaultImage"
            alt="新闻图片"
            width="110px"
            @click="goToDetail(item.newsId)"
          />
          <div class="favorite-content">
            <h3 @click="goToDetail(item.newsId)">{{ item.title }}</h3>
            <p>{{ item.summary }}</p>
            <div class="favorite-meta">
              <span
                ><el-icon><Timer /></el-icon
                >{{ formatDate(item.createTime) }}</span
              >
              <span
                ><el-icon><View /></el-icon>{{ item.viewCount || 0 }}</span
              >
              <span
                ><el-icon><ChatLineRound /></el-icon
                >{{ item.commentCount || 0 }}</span
              >
              <span
                ><el-icon><Folder /></el-icon
                >{{ item.categoryName || "未分类" }}</span
              >
            </div>
          </div>
          <div class="favorite-actions">
            <el-button
              type="danger"
              link
              @click="showRemoveConfirm(item.favoriteId)"
            >
              取消收藏
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无收藏记录" />

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
const favoriteList = ref([]);
const searchTitle = ref("");

// 默认图片
const defaultImage = new URL(
  "@/assets/imgs/default-news-image.png",
  import.meta.url
).href;

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString();
};

// 获取收藏列表
const fetchFavoritesList = async () => {
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

    const response = await request.get("/manage/favorites/list", { params });

    if (response.code === 200) {
      favoriteList.value = response.rows;
      total.value = response.total;
    } else {
      ElMessage.error(response.msg || "获取收藏列表失败");
    }
  } catch (error) {
    console.error("获取收藏列表失败:", error);
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

// 显示取消收藏确认弹窗
const showRemoveConfirm = (favoriteId) => {
  ElMessageBox.confirm("确定要取消收藏吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      // 用户确认取消收藏
      removeFavorite(favoriteId);
    })
    .catch(() => {
      // 用户取消操作
      ElMessage.info("已取消操作");
    });
};

// 取消收藏
const removeFavorite = async (favoriteId) => {
  try {
    const response = await request.delete(`/manage/favorites/${favoriteId}`);

    if (response.code === 200) {
      ElMessage.success("取消收藏成功");
      fetchFavoritesList(); // 刷新列表
    } else {
      ElMessage.error(response.msg || "取消收藏失败");
    }
  } catch (error) {
    console.error("取消收藏失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  }
};

// 重置搜索
const resetSearch = () => {
  searchTitle.value = "";
  currentPage.value = 1;
  fetchFavoritesList();
};

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchFavoritesList();
};

onMounted(() => {
  fetchFavoritesList();
});
</script>

<style scoped>
.favorites {
  min-height: 100%;
}

.favorites h2 {
  margin-bottom: 24px;
  color: #1f2937;
}

.filter-section {
  margin-bottom: 25px;
}

.favorite-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.favorite-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.favorite-content {
  flex: 0.9;
}

.favorite-content h3 {
  margin: 0 0 8px;
  color: #1f2937;
  cursor: pointer;
}

.favorite-content h3:hover {
  color: #1890ff;
}

.favorite-content p {
  margin: 0 0 8px;
  color: #6b7280;
}

.favorite-meta {
  display: flex;
  gap: 16px;
  color: #9ca3af;
  font-size: 12px;
}

.favorite-meta span {
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
