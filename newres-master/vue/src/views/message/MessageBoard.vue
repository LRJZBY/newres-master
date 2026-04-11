<template>
  <div class="message-board-container">
    <Header />

    <div class="message-content">
      <div class="message-header">
        <h2>留言板</h2>
        <p>欢迎在此分享您的想法和建议</p>
      </div>

      <!-- 发布留言区域 -->
      <div class="message-publish">
        <h3>发表留言</h3>
        <div v-if="!currentUser.userId" class="login-hint">
          <el-alert
            title="请先登录后再发表留言"
            type="info"
            description="登录后即可参与留言讨论"
            :closable="false"
            show-icon
          >
            <template #default>
              <el-button type="primary" size="small" @click="goToLogin"
                >去登录</el-button
              >
            </template>
          </el-alert>
        </div>
        <div v-else>
          <el-button type="primary" @click="showMessageDialog"
            >发布留言</el-button
          >
        </div>
      </div>

      <!-- 富文本编辑器对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="replyToMessage ? '回复留言' : '发布留言'"
        width="800px"
        :before-close="handleDialogClose"
      >
        <div v-if="replyToMessage" class="reply-preview">
          <div class="reply-header">
            <div class="reply-to-info">
              <el-avatar
                :size="32"
                :src="replyToMessage.avatar || defaultAvatar"
              ></el-avatar>
              <span class="reply-username">{{ replyToMessage.username }}</span>
              <span class="reply-time">{{
                formatTime(replyToMessage.createTime)
              }}</span>
            </div>
            <div class="reply-label">您正在回复：</div>
          </div>
          <div class="reply-content-container">
            <div class="reply-content" v-html="replyToMessage.content"></div>
          </div>
        </div>

        <el-form :model="messageForm" :rules="rules" ref="messageFormRef">
          <el-form-item prop="content">
            <div class="editor-wrapper">
              <div class="rich-editor">
                <Toolbar
                  style="border-bottom: 1px solid #ccc"
                  :editor="editorRef"
                  :defaultConfig="toolbarConfig"
                  :mode="mode"
                />
                <Editor
                  style="height: 300px; overflow-y: hidden"
                  v-model="messageForm.content"
                  :defaultConfig="editorConfig"
                  :mode="mode"
                  @onCreated="handleCreated"
                  @onChange="handleChange"
                />
              </div>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="handleDialogClose">取消</el-button>
            <el-button
              type="primary"
              @click="submitMessage"
              :loading="publishLoading"
            >
              {{ replyToMessage ? "回复" : "发布" }}
            </el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 留言列表区域 -->
      <div class="message-list">
        <h3>
          全部留言 <span class="message-count">{{ totalMessages }}条</span>
        </h3>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>

        <!-- 留言列表 -->
        <div v-else>
          <div v-if="messages.length === 0" class="no-messages">
            <el-empty description="暂无留言，快来发表第一条留言吧！"></el-empty>
          </div>

          <div v-else class="message-items">
            <!-- 使用循环遍历留言，替代MessageItem组件 -->
            <div
              v-for="message in messages"
              :key="message.messageId"
              class="message-item"
            >
              <!-- 留言主体 -->
              <div class="message-main">
                <div class="user-avatar">
                  <el-avatar
                    :size="48"
                    :src="getUserAvatar(message)"
                  ></el-avatar>
                </div>
                <div class="message-content">
                  <div class="message-meta">
                    <span class="username">{{
                      message.username || "匿名用户"
                    }}</span>
                    <span class="time">{{
                      formatTime(message.createTime)
                    }}</span>
                  </div>
                  <div class="message-text" v-html="message.content"></div>
                  <div class="message-actions">
                    <el-button type="text" @click="handleReply(message)">
                      <el-icon><ChatLineRound /></el-icon> 回复
                    </el-button>
                    <!-- 添加查看回复按钮，只有有子留言时才显示 -->
                    <el-button
                      v-if="message.children && message.children.length > 0"
                      type="text"
                      @click="toggleReplies(message.messageId)"
                    >
                      <el-icon><View /></el-icon>
                      {{
                        shownReplies.includes(message.messageId)
                          ? "收起回复"
                          : `查看回复(${message.children.length})`
                      }}
                    </el-button>
                    <el-button
                      v-if="canDelete(message)"
                      type="text"
                      @click="deleteMessage(message.messageId)"
                    >
                      <el-icon><Delete /></el-icon> 删除
                    </el-button>
                  </div>
                </div>
              </div>

              <!-- 子留言，默认隐藏，点击查看回复后才显示 -->
              <div
                v-if="
                  message.children &&
                  message.children.length > 0 &&
                  shownReplies.includes(message.messageId)
                "
                class="message-children"
              >
                <div
                  v-for="child in message.children"
                  :key="child.messageId"
                  class="message-child-item"
                >
                  <div class="message-child-main">
                    <div class="user-avatar">
                      <el-avatar
                        :size="36"
                        :src="getUserAvatar(child)"
                      ></el-avatar>
                    </div>
                    <div class="message-content">
                      <div class="message-meta">
                        <span class="username">{{
                          child.username || "匿名用户"
                        }}</span>
                        <span class="time">{{
                          formatTime(child.createTime)
                        }}</span>
                      </div>
                      <div class="message-text" v-html="child.content"></div>
                      <div class="message-actions">
                        <el-button type="text" @click="handleReply(child)">
                          <el-icon><ChatLineRound /></el-icon> 回复
                        </el-button>
                        <el-button
                          v-if="canDelete(child)"
                          type="text"
                          @click="deleteMessage(child.messageId)"
                        >
                          <el-icon><Delete /></el-icon> 删除
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:currentPage="currentPage"
            :page-size="pageSize"
            :total="totalMessages"
            layout="prev, pager, next"
            @current-change="handlePageChange"
            background
          />
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
import { ChatLineRound, Delete, View } from "@element-plus/icons-vue";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import request from "@/utils/request";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import "@wangeditor/editor/dist/css/style.css";

const router = useRouter();

// 数据定义
const messages = ref([]);
const totalMessages = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(true);
const publishLoading = ref(false);
const replyToMessage = ref(null);
const defaultAvatar =
  "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

// 表单数据
const messageFormRef = ref(null);
const messageForm = reactive({
  content: "",
  parentId: null,
});

// 验证规则
const rules = {
  content: [
    { required: true, message: "请输入留言内容", trigger: "blur" },
    { min: 2, message: "留言内容不能少于2个字符", trigger: "blur" },
  ],
};

// 获取当前用户信息
const currentUser = ref({
  userId: null,
  username: "",
  avatar: null,
  role: "user",
});

// 检查是否可以删除留言
const canDelete = (message) => {
  if (!currentUser.value.userId) return false;
  return (
    currentUser.value.role === "admin" ||
    message.userId === currentUser.value.userId
  );
};

// 获取用户头像
const getUserAvatar = (message) => {
  if (message && message.avatar) {
    return message.avatar;
  }
  return defaultAvatar;
};

// 格式化时间显示
const formatTime = (time) => {
  if (!time) return "";

  try {
    const date = new Date(time);
    const now = new Date();
    const diff = now - date;

    if (diff < 60000) {
      return "刚刚";
    }
    if (diff < 3600000) {
      return `${Math.floor(diff / 60000)}分钟前`;
    }
    if (diff < 86400000) {
      return `${Math.floor(diff / 3600000)}小时前`;
    }
    if (diff < 2592000000) {
      return `${Math.floor(diff / 86400000)}天前`;
    }

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");

    return `${year}-${month}-${day} ${hours}:${minutes}`;
  } catch (error) {
    console.error("日期格式化错误:", error);
    return time;
  }
};

// 初始化用户信息
const initUserInfo = () => {
  const user = localStorage.getItem("user");
  if (user) {
    try {
      const userInfo = JSON.parse(user);
      currentUser.value = {
        userId: userInfo.userId,
        username:
          userInfo.username || localStorage.getItem("username") || "用户",
        avatar: userInfo.avatar || null,
        role: userInfo.role || "user",
      };

      if (!currentUser.value.userId) {
        console.warn("未能获取到用户ID，部分功能可能受限");
      }
    } catch (error) {
      console.error("解析用户信息失败:", error);
      ElMessage.warning("获取用户信息失败，请尝试重新登录");
    }
  } else {
    console.warn("用户未登录，留言功能将受限");
  }
};

// 获取留言列表 - 修改为使用前台接口
const fetchMessages = async () => {
  loading.value = true;
  try {
    // 使用前台接口
    const response = await request.get("/manage/messages/frontList", {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      },
    });

    if (response.code === 200) {
      totalMessages.value = response.total || 0;
      messages.value = response.rows || [];
    } else {
      ElMessage.warning(response.msg || "获取留言失败");
    }
  } catch (error) {
    console.error("获取留言列表失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  } finally {
    loading.value = false;
  }
};

// 格式化日期为后端需要的格式 yyyy-MM-dd HH:mm:ss
const formatDateForBackend = (date) => {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  const hours = String(d.getHours()).padStart(2, "0");
  const minutes = String(d.getMinutes()).padStart(2, "0");
  const seconds = String(d.getSeconds()).padStart(2, "0");

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 编辑器实例
const editorRef = shallowRef();
const mode = ref("default");

// 编辑器配置
const toolbarConfig = {
  excludeKeys: [
    "group-video",
    "group-code",
    "todo",
    "group-justify",
    "group-indent",
    "group-quote",
  ],
};

const editorConfig = {
  placeholder: "请输入留言内容...",
  MENU_CONF: {
    uploadImage: {
      server: "/api/common/upload", // 上传接口地址
      fieldName: "file",
      maxFileSize: 2 * 1024 * 1024, // 2MB
      maxNumberOfFiles: 5,
      allowedFileTypes: ["image/*"],
      meta: {},
      metaWithUrl: true,
      withCredentials: false,
      timeout: 5 * 1000,

      customUpload: async (file, insertFn) => {
        try {
          const formData = new FormData();
          formData.append("file", file);

          const response = await request.post("/common/upload", formData, {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          });

          if (response.code === 200) {
            // 上传成功，将图片插入到编辑器
            insertFn(response.url, file.name, response.url);
          } else {
            ElMessage.error(response.msg || "图片上传失败");
          }
        } catch (error) {
          console.error("上传图片失败:", error);
          ElMessage.error("网络错误，请稍后再试");
        }
      },
    },
  },
};

// 处理编辑器创建完成
const handleCreated = (editor) => {
  editorRef.value = editor;
};

// 处理编辑器内容变化
const handleChange = (editor) => {
  messageForm.content = editor.getHtml();
};

// 添加对话框相关的响应式变量
const dialogVisible = ref(false);

// 显示留言对话框
const showMessageDialog = () => {
  if (!currentUser.value.userId) {
    ElMessage.warning("请先登录后再发表留言");
    return;
  }
  replyToMessage.value = null;
  messageForm.parentId = null;
  dialogVisible.value = true;
};

// 处理对话框关闭
const handleDialogClose = () => {
  ElMessageBox.confirm("确认关闭？未保存的内容将会丢失", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      dialogVisible.value = false;
      messageForm.content = "";
      messageForm.parentId = null;
      replyToMessage.value = null;
      if (editorRef.value) {
        editorRef.value.clear();
      }
    })
    .catch(() => {});
};

// 处理回复
const handleReply = (message) => {
  if (!currentUser.value.userId) {
    ElMessage.warning("请先登录后再回复留言");
    return;
  }
  replyToMessage.value = message;
  messageForm.parentId = message.messageId;
  dialogVisible.value = true;
};

// 提交留言方法
const submitMessage = async () => {
  if (!currentUser.value.userId) {
    ElMessage.warning("请先登录后再发表留言");
    return;
  }

  await messageFormRef.value.validate(async (valid) => {
    if (valid) {
      publishLoading.value = true;
      try {
        const currentTime = formatDateForBackend(new Date());

        const messageData = {
          userId: currentUser.value.userId,
          username: currentUser.value.username,
          content: editorRef.value.getHtml(),
          parentId: messageForm.parentId,
          createTime: currentTime,
        };

        const response = await request.post("/manage/messages", messageData);

        if (response.code === 200) {
          ElMessage.success(
            messageForm.parentId ? "回复发布成功" : "留言发布成功"
          );
          messageForm.content = "";
          messageForm.parentId = null;
          replyToMessage.value = null;
          if (editorRef.value) {
            editorRef.value.clear();
          }
          dialogVisible.value = false;
          currentPage.value = 1;
          await fetchMessages();
        } else {
          ElMessage.error(response.msg || "留言发布失败");
        }
      } catch (error) {
        console.error("发布留言失败:", error);
        ElMessage.error("网络错误，请稍后再试");
      } finally {
        publishLoading.value = false;
      }
    }
  });
};

// 删除留言
const deleteMessage = async (messageId) => {
  try {
    await ElMessageBox.confirm(
      "确定要删除这条留言吗？删除后将无法恢复。",
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    const response = await request.delete(`/manage/messages/${messageId}`);

    if (response.code === 200) {
      ElMessage.success("删除成功");
      await fetchMessages();
    } else {
      ElMessage.error(response.msg || "删除失败");
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除留言失败:", error);
      ElMessage.error("删除失败，请稍后重试");
    }
  }
};

// 切换页码
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchMessages();
};

// 跳转到登录页
const goToLogin = () => {
  router.push("/login");
};

// 页面初始化
onMounted(() => {
  initUserInfo();
  fetchMessages();
});

// 组件销毁时清理编辑器实例
onBeforeUnmount(() => {
  editorRef.value = null;
});

// 添加响应式变量跟踪子留言显示状态
const shownReplies = ref([]);

// 添加处理子留言显示状态的逻辑
const toggleReplies = (messageId) => {
  if (shownReplies.value.includes(messageId)) {
    shownReplies.value = shownReplies.value.filter((id) => id !== messageId);
  } else {
    shownReplies.value.push(messageId);
  }
};
</script>

<style scoped>
.message-board-container {
  min-height: 100vh;
  background-color: var(--bg-color, #f5f7fa);
}

.message-content {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.message-header {
  text-align: center;
  margin-bottom: 30px;
}

.message-header h2 {
  font-size: 28px;
  color: var(--text-primary, #333);
  margin-bottom: 10px;
}

.message-header p {
  color: var(--text-secondary, #666);
  font-size: 16px;
}

.message-publish {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.message-publish h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: var(--text-primary, #333);
  font-size: 18px;
  font-weight: 500;
}

.editor-wrapper {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.message-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.message-list h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: var(--text-primary, #333);
  font-size: 18px;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.message-count {
  font-size: 14px;
  color: var(--text-secondary, #666);
  margin-left: 10px;
}

.loading-container {
  padding: 30px 0;
  text-align: center;
}

.no-messages {
  padding: 40px 0;
  text-align: center;
}

.message-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-item {
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 20px;
}

.message-item:last-child {
  border-bottom: none;
}

.message-main {
  display: flex;
  gap: 15px;
}

.message-content {
  flex: 1;
}

.message-meta {
  margin-bottom: 8px;
}

.username {
  font-weight: 500;
  color: var(--primary-color, #409eff);
  margin-right: 10px;
}

.time {
  font-size: 12px;
  color: var(--text-light, #909399);
}

.message-text {
  margin-bottom: 10px;
  line-height: 1.6;
  word-break: break-word;
}

.message-actions {
  display: flex;
  gap: 15px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 富文本内容样式 */
:deep(.message-text) {
  line-height: 1.6;
}

:deep(.message-text p) {
  margin: 0 0 10px;
}

:deep(.message-text img) {
  max-width: 100%;
  height: auto;
}

:deep(.message-text a) {
  color: var(--primary-color, #409eff);
  text-decoration: none;
}

:deep(.message-text a:hover) {
  text-decoration: underline;
}

/* 添加登录提示区域样式 */
.login-hint {
  padding: 10px;
  margin-bottom: 15px;
}

/* 添加子留言展开/收起的过渡动画 */
.message-children {
  margin-left: 50px;
  margin-top: 10px;
  border-left: 2px solid #f0f0f0;
  padding-left: 15px;
  transition: all 0.3s ease;
  overflow: hidden;
  max-height: 1000px; /* 足够容纳多条回复 */
}

/* 为回复按钮添加高亮效果 */
.message-actions .el-button {
  padding: 4px 8px;
  transition: all 0.2s;
}

.message-actions .el-button:hover {
  background-color: #f0f7ff;
  border-radius: 4px;
}

/* 添加计数器样式 */
.reply-count {
  background-color: #f0f7ff;
  color: var(--primary-color, #409eff);
  border-radius: 10px;
  padding: 0 6px;
  font-size: 12px;
  margin-left: 4px;
}

/* 改进子留言样式 */
.message-child-item {
  position: relative;
  padding: 12px 0;
  border-bottom: 1px dashed #f0f0f0;
  animation: fadeIn 0.3s ease;
}

.message-child-item:last-child {
  border-bottom: none;
}

.message-child-main {
  display: flex;
  gap: 10px;
}

/* 添加加载动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 添加过渡动画 */
.message-item {
  transition: background-color 0.2s ease;
}

.message-item:hover {
  background-color: #fafafa;
}

/* 添加对话框相关样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-dialog__body) {
  padding: 20px 30px;
}

.editor-wrapper {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

/* 确保对话框中的编辑器显示正常 */
:deep(.w-e-text-container) {
  z-index: 100;
}

:deep(.w-e-toolbar) {
  z-index: 101;
}

:deep(.el-dialog) {
  margin-top: 8vh !important;
}

/* 改进回复预览样式 */
.reply-preview {
  background-color: #f8f9fa;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 15px;
  border-left: 3px solid var(--primary-color, #409eff);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.reply-header {
  margin-bottom: 8px;
}

.reply-to-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
}

.reply-username {
  font-weight: 500;
  color: var(--primary-color, #409eff);
}

.reply-time {
  font-size: 12px;
  color: var(--text-light, #909399);
}

.reply-label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
  font-weight: 500;
}

.reply-content-container {
  padding: 8px 10px;
  background-color: #fff;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.reply-content {
  color: #303133;
  font-size: 13px; /* 缩小字体 */
  line-height: 1.5;
  max-height: 120px; /* 限制内容高度 */
  overflow-y: auto; /* 添加垂直滚动条 */
  position: relative;
}

.reply-content :deep(p) {
  margin: 0 0 6px;
}

.reply-content :deep(img) {
  max-width: 100%;
  max-height: 80px; /* 限制图片高度 */
  height: auto;
  object-fit: contain;
  margin: 3px 0;
  border-radius: 4px;
}

/* 修改弹窗样式 */
:deep(.el-dialog__header) {
  padding: 20px 30px;
  border-bottom: 1px solid #ebeef5;
}

/* 添加富文本编辑器的响应式调整 */
@media (max-width: 768px) {
  .message-content {
    padding: 10px;
  }

  .message-children {
    margin-left: 20px;
  }
}
</style>
