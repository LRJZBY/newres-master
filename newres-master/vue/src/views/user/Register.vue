<template>
  <div class="register-container">
    <div class="register-box">
      <!-- 左侧系统形象展示 -->
      <div class="register-left">
        <div class="system-info">
          <div class="logo-container">
            <img
              src="@/assets/imgs/logo.png"
              alt="新闻推荐系统"
              class="logo-image"
            />
          </div>
          <h1>新闻推荐系统</h1>
          <p>发现 · 分享 · 创新</p>
          <div class="system-features">
            <div class="feature-item">
              <el-icon><Reading /></el-icon>
              <span>海量资讯，一手掌握</span>
            </div>
            <div class="feature-item">
              <el-icon><DataAnalysis /></el-icon>
              <span>智能推荐，量身定制</span>
            </div>
            <div class="feature-item">
              <el-icon><Connection /></el-icon>
              <span>互动交流，分享观点</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="register-right">
        <div class="register-form">
          <h2>用户注册</h2>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="0"
            @submit.prevent="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="用户名"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="email">
              <el-input
                v-model="form.email"
                placeholder="邮箱"
                prefix-icon="Message"
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
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="确认密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="w-full" @click="handleRegister">
                注册
              </el-button>
            </el-form-item>
            <div class="form-footer">
              <span>已有账号？</span>
              <router-link to="/login" class="login-link">立即登录</router-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const router = useRouter();
const formRef = ref(null);

const form = reactive({
  username: "",
  email: "",
  password: "",
  confirmPassword: "",
});

const validatePass2 = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== form.password) {
    callback(new Error("两次输入密码不一致!"));
  } else {
    callback();
  }
};

const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
  email: [
    { required: true, message: "请输入邮箱地址", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入密码", trigger: "blur" },
    { validator: validatePass2, trigger: "blur" },
  ],
};

const handleRegister = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 调用后端注册接口
        const response = await request.post("/manage/user/register", {
          username: form.username,
          email: form.email,
          password: form.password,
        });

        if (response.code === 200) {
          ElMessage.success("注册成功！");
          router.push("/login");
        } else {
          ElMessage.error(response.msg || "注册失败");
        }
      } catch (error) {
        ElMessage.error("网络错误，请稍后再试");
        console.error(error);
      }
    }
  });
};
</script>

<style scoped>
.register-container {
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

.register-box {
  width: 1000px;
  height: 600px;
  display: flex;
  border-radius: 12px;
  box-shadow: 0 8px 24px var(--shadow-color);
  overflow: hidden;
  background: #fff;
}

.register-left {
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
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(10px);
}

.feature-item .el-icon {
  font-size: 24px;
}

/* 添加背景装饰效果 */
.register-left::before {
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

.register-right {
  flex: 1;
  padding: 40px;
  display: flex;
  align-items: center;
}

.register-form {
  width: 100%;
  max-width: 360px;
  margin: 0 auto;
}

.register-form h2 {
  font-size: 28px;
  color: #1f2937;
  margin-bottom: 32px;
  text-align: center;
}

:deep(.el-input__wrapper) {
  padding: 8px 16px;
  background-color: var(--background-light);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: var(--primary-light);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 1px var(--primary-light);
}

:deep(.el-input__inner) {
  height: 24px;
}

.form-footer {
  margin-top: 16px;
  text-align: center;
  color: #6b7280;
}

.login-link {
  color: var(--primary-color);
  text-decoration: none;
  margin-left: 4px;
}

.login-link:hover {
  text-decoration: underline;
}

.w-full {
  width: 100%;
}

:deep(.el-button--primary) {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  background-color: var(--primary-dark);
  border-color: var(--primary-dark);
}
</style>
