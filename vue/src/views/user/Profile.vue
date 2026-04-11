<template>
  <div class="profile">
    <h2>个人中心</h2>
    <div class="profile-content">
      <!-- 基本信息卡片 -->
      <el-card class="info-card">
        <div class="user-basic">
          <div class="avatar-container">
            <el-avatar :size="100" :src="userInfo.avatar" />
            <el-upload
              class="avatar-uploader"
              action="#"
              :http-request="handleAvatarChange"
              :before-upload="beforeAvatarUpload"
              :show-file-list="false"
              accept="image/*"
            >
              <el-button type="primary" link class="change-avatar">
                更换头像
              </el-button>
            </el-upload>
          </div>
          <div class="user-info">
            <h3>{{ userInfo.username }}</h3>
            <p>{{ userInfo.bio }}</p>
          </div>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          class="profile-form"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" />
          </el-form-item>
          <el-form-item label="个人简介" prop="bio">
            <el-input
              v-model="form.bio"
              type="textarea"
              :rows="3"
              placeholder="介绍一下自己吧"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="showConfirmDialog"
              >保存修改</el-button
            >
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 右侧区域 -->
      <div class="profile-right">
        <!-- 数据统计卡片 -->
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <h3>数据统计</h3>
            </div>
          </template>
          <div class="stats-grid">
            <div class="stat-item">
              <h4>我的新闻</h4>
              <p>{{ stats.news }}</p>
            </div>
            <div class="stat-item">
              <h4>获得点赞</h4>
              <p>{{ stats.likes }}</p>
            </div>
            <div class="stat-item">
              <h4>获得评论</h4>
              <p>{{ stats.comments }}</p>
            </div>
            <div class="stat-item">
              <h4>获得总收藏</h4>
              <p>{{ stats.favorites }}</p>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";

// 表单引用
const formRef = ref(null);

// 用户信息
const userInfo = reactive({
  username: "",
  avatar: "",
  bio: "",
});

// 表单数据
const form = reactive({
  userId: null, // 添加userId字段
  username: "",
  email: "",
  bio: "",
});

// 统计数据
const stats = reactive({
  news: 0,
  likes: 0,
  comments: 0,
  favorites: 0,
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
  email: [
    { required: true, message: "请输入邮箱地址", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
  ],
};

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    // 从localStorage获取用户ID
    const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
    if (!storedUser.userId) {
      ElMessage.warning("请先登录");
      return;
    }

    const response = await request.get(`/manage/users/${storedUser.userId}`);
    if (response.code === 200) {
      const userData = response.data;
      // 更新用户信息
      Object.assign(userInfo, {
        username: userData.username || "",
        avatar:
          userData.avatar ||
          "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
        bio: userData.remark || "这个人很懒，什么都没写~",
      });

      // 更新表单数据
      Object.assign(form, {
        userId: userData.userId,
        username: userData.username || "",
        email: userData.email || "",
        bio: userData.remark || "",
      });

      // 获取用户统计数据
      await fetchUserStats();
    } else {
      ElMessage.error(response.msg || "获取用户信息失败");
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  }
};

// 获取用户统计数据
const fetchUserStats = async () => {
  try {
    // 从localStorage获取用户ID
    const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
    if (!storedUser.userId) {
      ElMessage.warning("请先登录");
      return;
    }

    const response = await request.get("/manage/statistics/dashboard/front", {
      params: {
        userId: storedUser.userId,
      },
    });

    if (response.code === 200) {
      // 从 response.data 中获取数据
      const data = response.data;
      // 更新统计数据
      stats.news = data.news || 0;
      stats.likes = data.likes || 0;
      stats.comments = data.comments || 0;
      stats.favorites = data.favorites || 0;
      console.log("用户统计数据:", stats);
    } else {
      ElMessage.warning(response.msg || "获取统计数据失败");
      // 设置默认值
      stats.news = 0;
      stats.likes = 0;
      stats.comments = 0;
      stats.favorites = 0;
    }
  } catch (error) {
    console.error("获取用户统计数据失败:", error);
    ElMessage.error("网络错误，请稍后再试");
    // 设置默认值
    stats.news = 0;
    stats.likes = 0;
    stats.comments = 0;
    stats.favorites = 0;
  }
};

// 显示确认对话框
const showConfirmDialog = () => {
  ElMessageBox.confirm("确定要保存修改吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      handleSubmit();
    })
    .catch(() => {
      ElMessage.info("已取消保存");
    });
};

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    const updateData = {
      userId: form.userId,
      username: form.username,
      email: form.email,
      remark: form.bio,
    };

    const response = await request.put("/manage/users", updateData);

    if (response.code === 200) {
      // 更新localStorage中的用户信息
      const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
      const updatedUserInfo = { ...storedUser, ...updateData };
      localStorage.setItem("user", JSON.stringify(updatedUserInfo));

      // 触发自定义事件通知Header组件更新
      window.dispatchEvent(new Event("userInfoUpdated"));

      ElMessage.success("保存成功");
      fetchUserInfo();
    } else {
      ElMessage.error(response.msg || "保存失败");
    }
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message);
    } else {
      ElMessage.error("表单验证失败");
    }
  }
};

// 处理头像上传
const handleAvatarChange = async (options) => {
  const file = options.file;
  try {
    const formData = new FormData();
    formData.append("file", file);

    const response = await request.post("/common/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    if (response.code === 200) {
      // 更新用户头像
      const updateData = {
        userId: form.userId,
        avatar: response.url,
        username: form.username, // 确保包含其他必要字段
      };

      const updateResponse = await request.put("/manage/users", updateData);

      if (updateResponse.code === 200) {
        userInfo.avatar = response.url;
        ElMessage.success("头像更新成功");
      } else {
        ElMessage.error(updateResponse.msg || "头像更新失败");
      }
    } else {
      ElMessage.error(response.msg || "上传失败");
    }
  } catch (error) {
    console.error("上传头像失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  }
};

// 头像上传前的验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith("image/");
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error("上传头像图片只能是图片格式!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("上传头像图片大小不能超过 2MB!");
    return false;
  }
  return true;
};

// 组件挂载时获取用户信息
onMounted(() => {
  fetchUserInfo();
});
</script>

<style scoped>
.profile {
  min-height: 100%;
}

.profile h2 {
  margin-bottom: 24px;
  color: #1f2937;
}

.profile-content {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.info-card {
  flex: 1;
  min-width: 300px;
}

.user-basic {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
}

.avatar-container {
  text-align: center;
}

.change-avatar {
  margin-top: 8px;
}

.user-info {
  flex: 1;
}

.user-info h3 {
  margin: 0 0 8px;
  color: #1f2937;
}

.user-info p {
  color: #6b7280;
  margin: 0;
}

.profile-form {
  max-width: 500px;
}

.profile-right {
  width: 350px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stats-card {
  width: 300px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.stat-item h4 {
  margin: 0 0 8px;
  color: #6b7280;
  font-size: 14px;
}

.stat-item p {
  margin: 0;
  color: #1f2937;
  font-size: 24px;
  font-weight: bold;
}

/* 添加上传相关样式 */
.avatar-uploader {
  margin-top: 10px;
  text-align: center;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

/* 确保头像显示正常 */
.el-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 响应式设计调整 */
@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
  }

  .profile-right {
    width: 100%;
  }
}
</style>
