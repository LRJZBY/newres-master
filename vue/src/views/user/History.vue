<template>
  <div class="history">
    <h2>浏览历史</h2>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-item">
        <el-input
          v-model="searchTitle"
          placeholder="搜索新闻标题"
          clearable
          @keyup.enter="fetchHistoryList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <el-button type="primary" @click="fetchHistoryList">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
      <el-button
        type="danger"
        @click="clearAllHistory"
        :disabled="!historyList.length"
      >
        清空浏览记录
      </el-button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 无数据状态 -->
    <el-empty v-else-if="!historyList.length" description="暂无浏览记录" />

    <!-- 历史记录列表 -->
    <div v-else class="history-list">
      <div
        v-for="item in historyList"
        :key="item.historyId"
        class="history-item"
      >
        <div class="history-layout">
          <div class="history-image" @click="goToDetail(item.newsId)">
            <img
              :src="item.img || defaultImage"
              :alt="item.title"
              @error="handleImageError"
            />
          </div>
          <div class="history-content" @click="goToDetail(item.newsId)">
            <h3 class="history-title">{{ item.title || "未知标题" }}</h3>
            <div class="history-time">
              <el-icon><Timer /></el-icon>
              <span>{{ formatDateTime(item.createTime) }}</span>
            </div>
          </div>
          <div class="history-actions">
            <el-button
              type="danger"
              link
              @click.stop="removeHistory(item.historyId)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="historyList.length" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
        layout="prev, pager, next"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Timer, Search } from "@element-plus/icons-vue";
import request from "@/utils/request";

const router = useRouter();
const historyList = ref([]);
const loading = ref(true);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchTitle = ref("");

// 默认图片
const defaultImage = new URL(
  "@/assets/imgs/default-news-image.png",
  import.meta.url
).href;

// 图片加载失败时使用默认图片
const handleImageError = (event) => {
  event.target.src = defaultImage;
};

// 格式化日期时间，精确到分钟和秒
const formatDateTime = (dateString) => {
  if (!dateString) return "未知时间";

  try {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds()).padStart(2, "0");

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  } catch (e) {
    return "日期格式错误";
  }
};

// 获取浏览历史列表
const fetchHistoryList = async () => {
  loading.value = true;

  // 获取当前登录用户信息
  const userInfo = JSON.parse(localStorage.getItem("user") || "{}");
  const userId = userInfo.userId;

  if (!userId) {
    ElMessage.warning("您尚未登录，请先登录");
    loading.value = false;
    return;
  }

  try {
    // 准备查询参数
    const params = {
      userId,
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    };

    // 如果有标题搜索关键词，添加到参数中
    if (searchTitle.value.trim()) {
      params.title = searchTitle.value.trim();
    }

    const response = await request.get("/manage/history/list", { params });

    if (response.code === 200) {
      historyList.value = response.rows.map((item) => ({
        ...item,
        img: item.img || defaultImage, // 确保有图片
      }));
      total.value = response.total;
    } else {
      ElMessage.warning(response.msg || "获取浏览历史失败");
      historyList.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error("获取浏览历史失败:", error);
    ElMessage.error("网络错误，请稍后再试");
    historyList.value = [];
    total.value = 0;
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

// 删除单条浏览记录
const removeHistory = (id) => {
  if (!id) {
    ElMessage.warning("记录ID不存在");
    return;
  }

  ElMessageBox.confirm("确定要删除这条浏览记录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      request
        .delete(`/manage/history/${id}`)
        .then((response) => {
          if (response.code === 200) {
            ElMessage.success("删除成功");
            fetchHistoryList(); // 重新加载列表
          } else {
            ElMessage.warning(response.msg || "删除失败");
          }
        })
        .catch(() => {
          ElMessage.error("网络错误，请稍后再试");
        });
    })
    .catch(() => {
      // 用户取消删除操作
    });
};

// 清空所有浏览记录
const clearAllHistory = () => {
  ElMessageBox.confirm("确定要清空全部浏览记录吗？此操作不可恢复！", "警告", {
    confirmButtonText: "确定清空",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      // 获取当前登录用户信息
      const userInfo = JSON.parse(localStorage.getItem("user") || "{}");
      const userId = userInfo.userId;

      if (!userId) {
        ElMessage.warning("用户未登录");
        return;
      }

      try {
        // 先获取所有该用户的浏览记录ID
        loading.value = true;
        const response = await request.get("/manage/history/list", {
          params: {
            userId,
            pageSize: 1000, // 设置一个较大的数值以获取所有记录
          },
        });

        if (response.code !== 200 || !response.rows || !response.rows.length) {
          ElMessage.warning("没有找到要删除的浏览记录");
          loading.value = false;
          return;
        }

        // 提取所有记录的ID
        const historyIds = response.rows.map((item) => item.historyId);

        // 使用批量删除接口
        const deleteResponse = await request.delete(
          `/manage/history/${historyIds.join(",")}`
        );

        if (deleteResponse.code === 200) {
          ElMessage.success("浏览记录已清空");
          fetchHistoryList(); // 重新加载列表
        } else {
          ElMessage.warning(deleteResponse.msg || "清空失败");
        }
      } catch (error) {
        console.error("清空浏览记录失败:", error);
        ElMessage.error("网络错误，请稍后再试");
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 用户取消清空操作
    });
};

// 重置筛选条件
const resetFilter = () => {
  searchTitle.value = ""; // 重置搜索标题
  currentPage.value = 1;
  fetchHistoryList();
};

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchHistoryList();
};

// 组件挂载时获取浏览历史
onMounted(() => {
  fetchHistoryList();
});
</script>

<style scoped>
.history {
  min-height: 100%;
  padding: 20px;
}

.filter-section {
  margin-bottom: 24px;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-item {
  min-width: 280px;
}

.loading-state {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  background: #fff;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  overflow: hidden;
}

.history-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.history-layout {
  display: flex;
  align-items: stretch;
  height: 85px;
}

.history-image {
  width: 105px;
  overflow: hidden;
  flex-shrink: 0;
  cursor: pointer;
}

.history-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.history-image:hover img {
  transform: scale(1.05);
}

.history-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 16px;
  cursor: pointer;
}

.history-title {
  font-size: 18px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 auto 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.history-time {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #9ca3af;
  font-size: 14px;
  margin-top: 10px;
}

.history-actions {
  display: flex;
  align-items: center;
  padding: 0 16px;
  margin-left: auto;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .history-layout {
    flex-direction: column;
    height: auto;
  }

  .history-image {
    width: 100%;
    height: 160px;
  }

  .history-actions {
    padding: 0 16px 16px;
  }
}

.history h2 {
  margin-bottom: 24px;
  color: #1f2937;
}
</style>
