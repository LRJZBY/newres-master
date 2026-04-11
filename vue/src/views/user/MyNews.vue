<template>
  <div class="my-news">
    <div class="page-header">
      <h2>我的新闻</h2>
      <el-button type="primary" @click="showAddNewsDialog">
        <el-icon><Plus /></el-icon>
        发布新闻
      </el-button>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-input
        v-model="searchParams.title"
        placeholder="搜索标题"
        clearable
        class="filter-item"
        @keyup.enter="fetchMyNewsList"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select
        v-model="searchParams.categoryId"
        placeholder="选择分类"
        clearable
        class="filter-item"
      >
        <el-option
          v-for="category in categories"
          :key="category.categoryId"
          :label="category.categoryName"
          :value="category.categoryId"
        />
      </el-select>

      <el-select
        v-model="searchParams.status"
        placeholder="状态"
        clearable
        class="filter-item"
      >
        <el-option label="已发布" value="0" />
        <el-option label="待审核" value="1" />
        <el-option label="未通过" value="2" />
      </el-select>

      <div class="filter-buttons">
        <el-button type="primary" @click="fetchMyNewsList">
          <el-icon><Search /></el-icon>查询
        </el-button>
        <el-button @click="resetFilter">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>
    </div>

    <!-- 新闻列表 -->
    <div v-loading="loading">
      <div v-if="newsList.length > 0" class="news-list">
        <div v-for="item in newsList" :key="item.newsId" class="news-item">
          <div class="news-header">
            <h3 @click="viewNewsDetail(item.newsId)">{{ item.title }}</h3>
            <div class="news-status">
              <el-tag :type="getStatusType(item.status)">
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>
          </div>

          <div class="news-content">
            <div class="news-info">
              <p class="news-summary">{{ getSummary(item.content) }}</p>
              <div class="news-meta">
                <span
                  ><el-icon><Timer /></el-icon
                  >{{ formatDate(item.createTime) }}</span
                >
                <span
                  ><el-icon><View /></el-icon>{{ item.viewCount || 0 }}</span
                >
                <span
                  ><el-icon><Star /></el-icon>{{ item.likeCount || 0 }}</span
                >
                <span
                  ><el-icon><Collection /></el-icon
                  >{{ item.collectCount || 0 }}</span
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

            <div class="news-image" v-if="item.img">
              <img
                :src="getImageUrl(item.img)"
                :alt="item.title"
                @error="handleImageError"
              />
            </div>
            <div class="news-image no-image" v-else>
              <el-icon><Picture /></el-icon>
              <span>暂无图片</span>
            </div>
          </div>

          <div class="news-actions">
            <el-button type="primary" link @click="editNews(item)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button type="danger" link @click="deleteNews(item.newsId)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
            <!-- 只有当新闻状态为1（已发布，审核通过）时，才显示查看按钮 -->
            <el-button
              v-if="item.status === 1"
              type="success"
              link
              @click="viewNewsDetail(item.newsId)"
            >
              <el-icon><View /></el-icon>查看
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无新闻，立即发布您的第一篇新闻" />

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          layout="total, sizes, prev, pager, next, jumper"
          background
        />
      </div>
    </div>

    <!-- 新增/编辑新闻对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '发布新闻' : '编辑新闻'"
      width="50%"
      :before-close="handleDialogClose"
      class="news-dialog"
      destroy-on-close
    >
      <el-form
        ref="newsFormRef"
        :model="newsForm"
        :rules="newsRules"
        label-width="80px"
        class="news-form"
      >
        <el-form-item label="标题" prop="title" class="title-item">
          <el-input
            v-model="newsForm.title"
            placeholder="请输入新闻标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select
            v-model="newsForm.categoryId"
            placeholder="请选择新闻分类"
            style="width: 100%"
          >
            <el-option
              v-for="category in categories"
              :key="category.categoryId"
              :label="category.categoryName"
              :value="category.categoryId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="封面" prop="img">
          <div class="cover-upload">
            <el-upload
              class="avatar-uploader"
              :http-request="handleManualUpload"
              :show-file-list="false"
              :before-upload="beforeImageUpload"
              name="file"
            >
              <div class="upload-area">
                <img
                  v-if="newsForm.img"
                  :src="getImageUrl(newsForm.img)"
                  class="preview-img"
                />
                <div v-else class="upload-placeholder">
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <span>点击上传封面</span>
                </div>
              </div>
            </el-upload>
            <div class="upload-tip">
              推荐尺寸：750x400，小于2MB，支持jpg、png格式
            </div>
          </div>
        </el-form-item>

        <el-form-item label="内容" prop="content" class="content-item">
          <div class="editor-wrapper">
            <Toolbar
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              :mode="mode"
              class="editor-toolbar"
            />
            <Editor
              v-model="newsForm.content"
              :defaultConfig="editorConfig"
              :mode="mode"
              @onCreated="handleCreated"
              @onChange="handleChange"
              class="editor-content"
            />
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button
            type="primary"
            @click="submitNewsForm"
            :loading="submitting"
          >
            {{ dialogType === "add" ? "发布" : "更新" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, shallowRef, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import "@wangeditor/editor/dist/css/style.css";
import { useRouter } from "vue-router";

const loading = ref(false);
const submitting = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const newsList = ref([]);
const categories = ref([]);
const dialogVisible = ref(false);
const dialogType = ref("add"); // 'add' 或 'edit'
const newsFormRef = ref(null);
const editorRef = shallowRef();
const router = useRouter();

// 富文本编辑器配置
const mode = ref("default");
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
  placeholder: "请输入新闻内容...",
  MENU_CONF: {
    uploadImage: {
      server: "/api/common/upload",
      fieldName: "file",
      maxFileSize: 2 * 1024 * 1024,
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
  newsForm.content = editor.getHtml();
};

// 默认图片
const defaultImage = new URL(
  "@/assets/imgs/default-news-image.png",
  import.meta.url
).href;

// 搜索参数
const searchParams = reactive({
  title: "",
  categoryId: "",
  status: "",
});

// 新闻表单
const newsForm = reactive({
  newsId: null,
  title: "",
  categoryId: "",
  content: "",
  img: "",
  userId: null,
});

// 表单校验规则
const newsRules = {
  title: [
    { required: true, message: "请输入新闻标题", trigger: "blur" },
    {
      min: 5,
      max: 100,
      message: "标题长度应在5到100个字符之间",
      trigger: "blur",
    },
  ],
  categoryId: [
    { required: true, message: "请选择新闻分类", trigger: "change" },
  ],
  content: [
    { required: true, message: "请输入新闻内容", trigger: "blur" },
    { min: 10, message: "内容不能少于10个字符", trigger: "blur" },
  ],
};

// 获取我的新闻列表
const fetchMyNewsList = async () => {
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

    // 添加筛选条件
    if (searchParams.title) {
      params.title = searchParams.title;
    }

    if (searchParams.categoryId) {
      params.categoryId = searchParams.categoryId;
    }

    if (searchParams.status) {
      params.status = searchParams.status;
    }

    const response = await request.get("/manage/news/list", { params });

    if (response.code === 200) {
      newsList.value = response.rows;
      total.value = response.total;
    } else {
      ElMessage.error(response.msg || "获取新闻列表失败");
    }
  } catch (error) {
    console.error("获取新闻列表失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  } finally {
    loading.value = false;
  }
};

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await request.get("/manage/categories/list");

    if (response.code === 200) {
      categories.value = response.rows;
    } else {
      ElMessage.error(response.msg || "获取分类列表失败");
    }
  } catch (error) {
    console.error("获取分类列表失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  }
};

// 重置筛选条件
const resetFilter = () => {
  searchParams.title = "";
  searchParams.categoryId = "";
  searchParams.status = "";
  currentPage.value = 1;
  fetchMyNewsList();
};

// 手动处理图片上传
const handleManualUpload = async (options) => {
  const file = options.file;
  try {
    submitting.value = true; // 显示上传中状态

    const formData = new FormData();
    formData.append("file", file);

    const response = await request.post("/common/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
        Authorization: localStorage.getItem("token") || "",
      },
    });

    if (response.code === 200) {
      newsForm.img = response.url;
      ElMessage.success("图片上传成功");
    } else {
      ElMessage.error(response.msg || "图片上传失败");
    }
  } catch (error) {
    console.error("图片上传失败:", error);
    ElMessage.error("图片上传失败，请检查网络连接或文件格式");
  } finally {
    submitting.value = false; // 结束上传状态
  }
};

// 上传前检查图片
const beforeImageUpload = (file) => {
  const isImage = file.type === "image/jpeg" || file.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error("只能上传JPG或PNG格式的图片!");
    return false;
  }

  if (!isLt2M) {
    ElMessage.error("图片大小不能超过2MB!");
    return false;
  }

  return true;
};

// 显示新增新闻对话框
const showAddNewsDialog = () => {
  dialogType.value = "add";
  resetNewsForm();
  dialogVisible.value = true;
};

// 编辑新闻
const editNews = async (news) => {
  dialogType.value = "edit";
  resetNewsForm();

  // 先打开对话框
  dialogVisible.value = true;

  // 等待DOM更新完成后再设置编辑器内容
  await nextTick();

  // 填充表单数据
  newsForm.newsId = news.newsId;
  newsForm.title = news.title;
  newsForm.categoryId = news.categoryId;
  newsForm.img = news.img;

  // 确保编辑器实例已创建并设置内容
  setTimeout(() => {
    if (editorRef.value) {
      editorRef.value.setHtml(news.content || "");
      newsForm.content = news.content || "";
    }
  }, 100);
};

// 提交新闻表单
const submitNewsForm = async () => {
  if (!newsFormRef.value) return;

  try {
    await newsFormRef.value.validate();

    submitting.value = true;
    const userInfo = JSON.parse(localStorage.getItem("user") || "{}");

    // 填充用户信息
    newsForm.userId = userInfo.userId;
    newsForm.createBy = userInfo.username;

    let response;

    if (dialogType.value === "add") {
      // 新增
      response = await request.post("/manage/news", newsForm);
    } else {
      // 修改
      response = await request.put("/manage/news", newsForm);
    }

    if (response.code === 200) {
      ElMessage.success(dialogType.value === "add" ? "发布成功" : "更新成功");
      dialogVisible.value = false;
      fetchMyNewsList(); // 刷新列表
    } else {
      ElMessage.error(
        response.msg || (dialogType.value === "add" ? "发布失败" : "更新失败")
      );
    }
  } catch (error) {
    console.error("提交新闻失败:", error);
    ElMessage.error("表单验证失败或网络错误");
  } finally {
    submitting.value = false;
  }
};

// 删除新闻
const deleteNews = (newsId) => {
  ElMessageBox.confirm("确定要删除这条新闻吗？删除后无法恢复", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const response = await request.delete(`/manage/news/${newsId}`);

        if (response.code === 200) {
          ElMessage.success("删除成功");
          fetchMyNewsList(); // 刷新列表
        } else {
          ElMessage.error(response.msg || "删除失败");
        }
      } catch (error) {
        console.error("删除新闻失败:", error);
        ElMessage.error("网络错误，请稍后再试");
      }
    })
    .catch(() => {
      // 取消删除
    });
};

// 查看新闻详情
const viewNewsDetail = (newsId) => {
  router.push({
    path: "/newsDetail",
    query: { newsId },
  });
};

// 处理对话框关闭
const handleDialogClose = (done) => {
  if (submitting.value) {
    ElMessage.warning("正在提交，请稍候...");
    return;
  }
  done();
};

// 重置新闻表单
const resetNewsForm = () => {
  newsForm.newsId = null;
  newsForm.title = "";
  newsForm.categoryId = "";
  newsForm.content = "";
  newsForm.img = "";
  newsForm.userId = null;

  // 重置编辑器内容
  if (editorRef.value) {
    editorRef.value.clear();
  }

  // 重置表单验证
  if (newsFormRef.value) {
    newsFormRef.value.resetFields();
  }
};

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
  fetchMyNewsList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchMyNewsList();
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: "待审核",
    1: "已发布",
    2: "未通过",
  };
  return statusMap[status] || "未知状态";
};

// 获取状态类型（用于标签颜色）
const getStatusType = (status) => {
  const typeMap = {
    0: "warning",
    1: "success",
    2: "danger",
  };
  return typeMap[status] || "info";
};

// 获取内容摘要
const getSummary = (content) => {
  if (!content) return "暂无内容";
  // 移除HTML标签，取前100个字符
  const plainText = content.replace(/<[^>]+>/g, "");
  return plainText.length > 50 ? plainText.substring(0, 50) + "..." : plainText;
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "";
  try {
    const date = new Date(dateString);
    return date.toLocaleString();
  } catch (error) {
    return dateString;
  }
};

// 获取图片URL的辅助函数
const getImageUrl = (url) => {
  if (!url) return defaultImage;

  // 检查URL是否已经是完整的URL
  if (url.startsWith("http://") || url.startsWith("https://")) {
    return url;
  }

  // 如果是相对路径，确保它是正确的路径
  if (!url.startsWith("/")) {
    url = "/" + url;
  }

  // 获取基础路径
  const baseURL = import.meta.env.VITE_APP_BASE_API || "";
  return `${baseURL}${url}`;
};

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = defaultImage;
};

onMounted(() => {
  fetchCategories();
  fetchMyNewsList();
});
</script>

<style scoped>
.my-news {
  width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  color: #1f2937;
  font-size: 22px;
}

.filter-section {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 24px;
}

.filter-item {
  width: 200px;
}

.filter-buttons {
  display: flex;
  gap: 12px;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.news-item {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  margin-bottom: 16px;
}

.news-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.news-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.news-header h3 {
  margin: 0;
  color: #1f2937;
  font-size: 18px;
  cursor: pointer;
}

.news-header h3:hover {
  color: #1890ff;
}

.news-content {
  display: flex;
  gap: 20px;
  margin: 16px 0;
}

.news-info {
  flex: 1;
  overflow: hidden;
}

.news-summary {
  margin: 0 0 12px;
  color: #4b5563;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.news-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: #6b7280;
  font-size: 13px;
}

.news-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.news-image {
  width: 180px;
  height: 90px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.news-image img:hover {
  transform: scale(1.05);
}

.news-image.no-image {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
}

.news-image.no-image .el-icon {
  font-size: 36px;
  margin-bottom: 2px;
}

.news-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 5px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.avatar-upload-container {
  display: flex;
  flex-direction: column;
}

.avatar-uploader {
  width: 130px;
  height: 160px;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.upload-text {
  margin: 0;
  text-align: center;
}

.avatar {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.upload-tip {
  margin-top: 3px;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

/* 弹出窗口样式优化 */
.news-dialog :deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
  margin: 5vh auto !important;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.news-dialog :deep(.el-dialog__header) {
  padding: 20px 30px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.news-dialog :deep(.el-dialog__body) {
  padding: 20px 30px;
  flex: 1;
  overflow-y: auto;
}

.news-dialog :deep(.el-dialog__footer) {
  padding: 20px 30px;
  border-top: 1px solid #e4e7ed;
  background-color: #f5f7fa;
}

.news-form {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.title-item {
  margin-bottom: 15px;
}

.content-item {
  flex: 1;
  margin-bottom: 0;
}

.editor-wrapper {
  height: 450px;
  display: flex;
  flex-direction: column;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  border-bottom: 1px solid #dcdfe6;
  background-color: #f5f7fa;
}

.editor-content {
  flex: 1;
  overflow-y: auto;
}

.cover-upload {
  width: 100%;
  margin-bottom: 10px;
}

.upload-area {
  width: 100%;
  height: 150px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
}

.upload-area:hover {
  border-color: var(--el-color-primary);
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  text-align: center;
  color: #8c939d;
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

/* 确保编辑器在对话框中正常显示 */
:deep(.w-e-text-container) {
  z-index: 100;
}

:deep(.w-e-toolbar) {
  z-index: 101;
}

/* 优化表单项样式 */
:deep(.el-form-item__label) {
  font-weight: 500;
}
</style>
