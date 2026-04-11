<template>
  <div class="user-layout">
    <Header />
    <div class="user-container">
      <div class="user-sidebar">
        <el-menu
          :default-active="activeMenu"
          class="user-menu"
          @select="handleSelect"
        >
          <el-menu-item index="profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="my-news">
            <el-icon><Document /></el-icon>
            <span>我的新闻</span>
          </el-menu-item>
          <el-menu-item index="favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="history">
            <el-icon><Timer /></el-icon>
            <span>浏览历史</span>
          </el-menu-item>
          <el-menu-item index="likes">
            <el-icon><Pointer /></el-icon>
            <span>点赞记录</span>
          </el-menu-item>
          <el-menu-item index="preferences">
            <el-icon><Setting /></el-icon>
            <span>偏好设置</span>
          </el-menu-item>
          <el-menu-item index="feedback">
            <el-icon><Message /></el-icon>
            <span>问题反馈</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="user-content">
        <router-view></router-view>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import {
  User,
  Document,
  Star,
  Timer,
  Pointer,
  Message,
  Setting,
} from "@element-plus/icons-vue";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";

const router = useRouter();
const route = useRoute();

const activeMenu = computed(() => {
  return route.path.split("/").pop();
});

const handleSelect = (key) => {
  router.push(`/user/${key}`);
};
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.user-container {
  flex: 1;
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
  display: flex;
  gap: 20px;
  background-color: var(--background-light);
}

.user-sidebar {
  width: 220px; /* 固定侧边栏宽度 */
  flex-shrink: 0;
}

.user-menu {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.user-sidebar :deep(.el-menu) {
  background-color: transparent;
}

.user-sidebar :deep(.el-menu-item:focus) {
  background-color: transparent !important;
}

.user-sidebar :deep(.el-menu-item.is-active) {
  background-color: transparent !important;
  color: var(--primary-color);
  border-left: 2px solid var(--primary-color);
}

.user-sidebar :deep(.el-menu-item:hover) {
  background-color: var(--background-light) !important;
}

.user-content {
  flex: 1;
  min-width: 800px; /* 设置最小宽度，保持一致性 */
  max-width: 960px; /* 设置最大宽度，避免过宽 */
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px var(--shadow-color);
}
</style>
