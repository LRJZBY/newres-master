<template>
  <div class="login-container">
    <div class="login-box">
      <!-- 左侧系统形象展示 -->
      <div class="login-left">
        <div class="system-info">
          <div class="logo-container">
            <img
              src="@/assets/imgs/logo.png"
              alt="新闻推荐系统"
              class="logo-image"
            />
          </div>
          <h1>新闻推荐系统</h1>
          <p>智能 · 个性 · 高效</p>
          <div class="system-features">
            <div class="feature-item">
              <el-icon><Reading /></el-icon>
              <span>智能推荐，个性定制</span>
            </div>
            <div class="feature-item">
              <el-icon><DataAnalysis /></el-icon>
              <span>大数据分析，精准投放</span>
            </div>
            <div class="feature-item">
              <el-icon><Connection /></el-icon>
              <span>实时更新，及时发布</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-right">
        <div class="login-form">
          <h2>用户登录</h2>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="0"
            @submit.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="用户名"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="w-full" @click="handleLogin">
                登录
              </el-button>
            </el-form-item>
            <div class="form-footer">
              <span>还没有账号？</span>
              <router-link to="/register" class="register-link"
                >立即注册</router-link
              >
              <div class="forgot-password">
                <a
                  href="javascript:;"
                  @click="showForgotPasswordDialog"
                  class="forgot-link"
                  >忘记密码？</a
                >
              </div>
            </div>
          </el-form>
        </div>
      </div>
    </div>

    <!-- 忘记密码弹窗 -->
    <el-dialog
      v-model="forgotPasswordVisible"
      title="找回密码"
      width="400px"
      center
      :close-on-click-modal="false"
    >
      <el-form
        ref="resetFormRef"
        :model="resetForm"
        :rules="resetRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="resetForm.username"
            placeholder="请输入您的用户名"
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="reset-tips">
        <el-alert
          title="密码将被重置为: 123456"
          type="warning"
          :closable="false"
          show-icon
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="forgotPasswordVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleResetPassword"
            :loading="resetLoading"
          >
            确认重置
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";

const router = useRouter();
const formRef = ref(null);
const resetFormRef = ref(null);

const form = reactive({
  username: "",
  password: "",
});

// 忘记密码相关
const forgotPasswordVisible = ref(false);
const resetLoading = ref(false);
const resetForm = reactive({
  username: "",
});

const rules = {
  username: [{ required: true, message: "请输入用户名/邮箱", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const resetRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
};

// 显示忘记密码弹窗
const showForgotPasswordDialog = () => {
  resetForm.username = "";
  forgotPasswordVisible.value = true;
};

// 处理重置密码
const handleResetPassword = () => {
  resetFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        resetLoading.value = true;
        const userName = resetForm.username;

        // 先查询用户是否存在
        const searchResponse = await request.get("/manage/users/list", {
          params: { username: userName },
        });

        if (!searchResponse.rows || searchResponse.rows.length === 0) {
          ElMessage.error("用户不存在，请检查用户名");
          resetLoading.value = false;
          return;
        }

        // 调用重置密码接口
        const response = await request.put(
          `/manage/users/resetPwd1/${userName}`
        );

        if (response.code === 200) {
          ElMessage.success("密码已重置为：123456");
          forgotPasswordVisible.value = false;
          // 清空表单
          resetForm.username = "";
        } else {
          ElMessage.error(response.msg || "重置密码失败");
        }
      } catch (error) {
        console.error("重置密码错误:", error);
        ElMessage.error(error.msg || "网络错误，请稍后再试");
      } finally {
        resetLoading.value = false;
      }
    }
  });
};

const handleLogin = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      // 调用后端登录接口
      request
        .post("/manage/user/login", {
          username: form.username,
          password: form.password,
        })
        .then((response) => {
          if (response.code === 200) {
            // 登录成功，存储token和用户信息
            localStorage.setItem("user", JSON.stringify(response.data));
            //token放入
            localStorage.setItem("token", response.data.token);
            localStorage.setItem("username", form.username);

            // 清除会话状态，确保显示偏好设置对话框
            localStorage.removeItem("sessionDismissed");

            ElMessage.success("登录成功！");
            setTimeout(() => {
              router.push("/home");
            }, 500);
          } else {
            // 登录失败，显示错误信息
            ElMessage.error(response.msg || "登录失败");
          }
        })
        .catch((error) => {
          ElMessage.error("网络错误，请稍后再试");
          console.error(error);
        });
    }
  });
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(
    135deg,
    var(--background-light) 0%,
    var(--background-dark) 100%
  );
}

.login-box {
  width: 1000px;
  height: 600px;
  display: flex;
  border-radius: 12px;
  box-shadow: 0 8px 24px var(--shadow-color);
  overflow: hidden;
  background: #fff;
}

.login-left {
  flex: 1;
  background: linear-gradient(
    135deg,
    var(--primary-color) 0%,
    var(--primary-dark) 100%
  );
  padding: 40px;
  display: flex;
  align-items: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.logo-container {
  margin-bottom: 24px;
  text-align: center;
}

.logo-image {
  width: 120px;
  height: 120px;
  object-fit: contain;
}

.system-info {
  width: 100%;
  position: relative;
  z-index: 2;
}

.system-info h1 {
  font-size: 32px;
  margin-bottom: 16px;
  text-align: center;
}

.system-info p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 40px;
  text-align: center;
  letter-spacing: 4px;
}

.system-features {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-top: 48px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  background: rgba(255, 255, 255, 0.15);
  padding: 12px 20px;
  border-radius: 8px;
  backdrop-filter: blur(4px);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateX(10px);
}

.feature-item .el-icon {
  font-size: 24px;
}

.login-right {
  flex: 1;
  padding: 40px;
  display: flex;
  align-items: center;
}

.login-form {
  width: 100%;
  max-width: 360px;
  margin: 0 auto;
}

.login-form h2 {
  font-size: 28px;
  color: var(--text-primary);
  margin-bottom: 32px;
  text-align: center;
}

:deep(.el-input__wrapper) {
  background-color: var(--background-light);
  border: 1px solid var(--border-color);
  padding: 8px 16px;
}

:deep(.el-input__inner) {
  height: 24px;
}

.form-footer {
  margin-top: 16px;
  text-align: center;
  color: #6b7280;
}

.register-link {
  color: var(--primary-color);
  text-decoration: none;
  margin-left: 4px;
}

.register-link:hover {
  text-decoration: underline;
}

.forgot-password {
  margin-top: 8px;
  text-align: right;
}

.forgot-link {
  color: var(--primary-color);
  text-decoration: none;
  font-size: 14px;
}

.forgot-link:hover {
  text-decoration: underline;
}

.reset-tips {
  margin: 15px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.w-full {
  width: 100%;
}

/* 添加背景装饰效果 */
.login-left::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle,
    rgba(255, 255, 255, 0.1) 0%,
    rgba(255, 255, 255, 0) 60%
  );
  animation: rotate 30s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

:deep(.el-button--primary) {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

:deep(.el-button--primary:hover) {
  background-color: var(--primary-dark);
  border-color: var(--primary-dark);
}
</style>
