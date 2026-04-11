<template>
  <header class="header">
    <div class="header-content">
      <div class="nav-area">
        <!-- Logo 区域 -->
        <div class="logo">
          <router-link to="/home" class="logo-link">
            <img
              src="@/assets/imgs/logo.png"
              alt="新闻推荐网"
              class="logo-image"
            />
            <span class="logo-text">新闻推荐网</span>
          </router-link>
        </div>

        <!-- 新闻分类 -->
        <div class="categories">
          <el-menu
            mode="horizontal"
            :default-active="activeCategory"
            @select="handleCategorySelect"
            :router="false"
          >
            <!-- 动态渲染新闻分类 -->
            <el-menu-item
              v-for="category in categories"
              :key="category.categoryId"
              :index="category.id"
              @click="handleItemClick(category.id)"
              @mouseover="hoverCategory = category.id"
              @mouseout="hoverCategory = null"
            >
              <img
                :src="category.categoryIcon"
                alt="分类图标"
                class="category-icon"
                :class="[
                  {
                    'active-icon':
                      category.id === activeCategory ||
                      category.id === hoverCategory,
                  },
                ]"
                @error="useDefaultIcon"
              />
              {{ category.categoryName }}
            </el-menu-item>
            <!-- 留言板放在最后 -->
            <el-menu-item index="message" @click="handleItemClick('message')">
              <el-icon><ChatDotRound /></el-icon>
              留言板
            </el-menu-item>
          </el-menu>
        </div>

        <!-- 右侧功能区 -->
        <div class="right-area">
          <!-- 搜索框 -->
          <el-input
            v-model="searchKeyword"
            placeholder="搜索新闻"
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #suffix>
              <el-icon class="search-icon" @click="handleSearch"
                ><Search
              /></el-icon>
            </template>
          </el-input>

          <!-- 用户信息 -->
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info">
              <el-avatar :size="45" :src="userAvatar" />
              <span class="username">{{ username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="my-news">
                  <el-icon><Document /></el-icon>我的新闻
                </el-dropdown-item>
                <el-dropdown-item command="favorites">
                  <el-icon><Star /></el-icon>我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="history">
                  <el-icon><Timer /></el-icon>浏览历史
                </el-dropdown-item>
                <el-dropdown-item command="likes">
                  <el-icon><Pointer /></el-icon>点赞记录
                </el-dropdown-item>
                <el-dropdown-item command="preferences">
                  <el-icon><Setting /></el-icon>
                  <span>偏好设置</span>
                </el-dropdown-item>
                <el-dropdown-item command="feedback">
                  <el-icon><Message /></el-icon>问题反馈
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const router = useRouter();
const searchKeyword = ref("");
const username = ref("");
const userAvatar = ref("");
const categories = ref([]);
const activeCategory = ref(null);
const hoverCategory = ref(null);

// 默认占位图标
const defaultCategoryIcon = new URL(
  "@/assets/imgs/default-icon.png",
  import.meta.url
).href;

// 图片加载失败时使用默认图标
const useDefaultIcon = (event) => {
  event.target.src = defaultCategoryIcon;
};

// 获取用户信息的方法
const fetchUserInfo = async () => {
  try {
    const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
    if (!storedUser.userId) {
      return;
    }

    const response = await request.get(`/manage/users/${storedUser.userId}`);
    if (response.code === 200) {
      const userData = response.data;
      username.value =
        userData.username || localStorage.getItem("username") || "用户";
      userAvatar.value =
        userData.avatar ||
        "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

      // 更新localStorage中的用户信息
      const updatedUserInfo = { ...storedUser, ...userData };
      localStorage.setItem("user", JSON.stringify(updatedUserInfo));
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
  }
};

// 监听localStorage中的user变化
const setupUserInfoListener = () => {
  window.addEventListener("storage", (e) => {
    if (e.key === "user") {
      fetchUserInfo();
    }
  });
};

// 创建自定义事件来触发用户信息更新
const setupCustomEventListener = () => {
  window.addEventListener("userInfoUpdated", () => {
    fetchUserInfo();
  });
};

// 获取新闻分类
const getNewsCategories = () => {
  request.get("/manage/categories/list").then((response) => {
    if (response.code === 200) {
      categories.value = response.rows.map((item) => ({
        id: item.categoryId.toString(),
        categoryId: item.categoryId,
        categoryName: item.categoryName,
        link: `/category/${item.categoryId}`,
        categoryIcon: item.categoryIcon || defaultCategoryIcon,
      }));
    }
  });
};

// 初始化时获取用户信息和新闻分类
onMounted(() => {
  fetchUserInfo();
  setupUserInfoListener();
  setupCustomEventListener();
  getNewsCategories();
});

// 处理分类点击事件
const handleItemClick = (index) => {
  if (index === "message") {
    router.push("/message");
  } else {
    activeCategory.value = index;
    router.push({
      path: "/newsCategory",
      query: { categoryId: index },
    });
  }
};

// 处理分类选择
const handleCategorySelect = (index) => {
  activeCategory.value = index;
};

// 处理搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: "/search",
      query: { keyword: encodeURIComponent(searchKeyword.value.trim()) },
    });
    searchKeyword.value = "";
  }
};

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === "logout") {
    // 清除登录信息
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    localStorage.removeItem("username");
    // 清除偏好设置会话状态
    localStorage.removeItem("sessionDismissed");
    ElMessage.success("已成功退出登录");
    router.push("/login");
  } else {
    // 导航到用户中心的其他页面
    router.push(`/user/${command}`);
  }
};
</script>

<style scoped>
.header {
  background: linear-gradient(to right, #ff9966, #ff7733);
  box-shadow: 0 2px 8px rgba(255, 153, 102, 0.2);
  position: sticky;
  top: 0;
  z-index: 100;
  height: 70px;
}

.header-content {
  width: 100%;
  height: 100%;
}

.nav-area {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 10px;
  padding: 0 20px;
}

.logo {
  margin-right: 20px;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.logo-image {
  width: 40px;
  height: 40px;
  object-fit: contain;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  white-space: nowrap;
}

.categories {
  flex: 1;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
  background: transparent;
  display: flex;
  justify-content: space-between;
  gap: 4px;
}

:deep(.el-menu-item) {
  height: 70px;
  line-height: 70px;
  font-size: 15px;
  color: #ffffff;
  padding: 0 12px;
  border-radius: 6px;
  transition: all 0.2s ease;
  border-bottom: none !important;
  cursor: pointer;
  background: transparent !important;
}

:deep(.el-menu-item:hover) {
  background-color: #ffffff !important;
  color: #ff704d !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #ffffff !important;
  color: #ff704d !important;
  font-weight: bold;
  border-bottom: none !important;
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.2);
}

:deep(.el-menu-item:active) {
  background-color: #ffffff !important;
  color: #ff704d !important;
  transform: scale(0.98);
}

:deep(.el-menu--horizontal > .el-menu-item.is-active) {
  border-bottom: none !important;
  background-color: #ffffff !important;
  color: #ff704d !important;
  transition: all 0.2s ease;
}

.right-area {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-left: 30px;
  padding-right: 20px;
}

.search-input {
  width: 210px;
}

:deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  box-shadow: none !important;
  border-radius: 20px;
  padding: 0 15px;
}

:deep(.el-input__inner) {
  height: 36px;
  color: #333;
}

:deep(.el-input__inner::placeholder) {
  color: #999;
}

.search-icon {
  color: #ff6600;
  cursor: pointer;
  font-size: 18px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 25px;
  cursor: pointer;
  background-color: rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.username {
  font-size: 14px;
  color: #fff;
  max-width: 64px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.el-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 14px;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #fff3e6;
  color: #ff6600;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
}

:deep(.el-menu-item:last-child) {
  margin-right: 15px;
}

.category-icon {
  width: 24px;
  height: 24px;
  margin-right: 8px;
  vertical-align: middle;
  filter: invert(100%) sepia(100%) saturate(0%) brightness(100%) contrast(100%);
}

.active-icon .category-icon {
  filter: hue-rotate(30deg) brightness(150%);
}
</style>
