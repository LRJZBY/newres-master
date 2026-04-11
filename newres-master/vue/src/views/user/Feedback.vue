<template>
  <el-card class="feedback-card">
    <template #header>
      <div class="card-header">
        <h3>问题反馈</h3>
      </div>
    </template>

    <!-- 反馈表单 -->
    <el-form
      ref="feedbackFormRef"
      :model="feedbackForm"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="反馈类型" prop="feedbackType">
        <el-select
          v-model="feedbackForm.feedbackType"
          placeholder="请选择反馈类型"
        >
          <el-option label="功能建议" value="0" />
          <el-option label="Bug报告" value="1" />
          <el-option label="用户体验" value="2" />
          <el-option label="其他" value="3" />
        </el-select>
      </el-form-item>

      <el-form-item label="反馈内容" prop="content">
        <el-input
          v-model="feedbackForm.content"
          type="textarea"
          :rows="4"
          placeholder="请详细描述您的问题或建议..."
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitFeedback" :loading="submitting">
          提交反馈
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 历史反馈列表 -->
    <div class="feedback-history">
      <div class="history-header">
        <h4>历史反馈</h4>
      </div>

      <el-table v-loading="loading" :data="feedbackList" style="width: 100%">
        <el-table-column prop="feedbackType" label="类型" width="100">
          <template #default="{ row }">
            {{ getFeedbackTypeName(row.feedbackType) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="content"
          label="反馈内容"
          show-overflow-tooltip
        />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : 'success'">
              {{ row.status === 0 ? "待回复" : "已回复" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewFeedbackDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 反馈详情对话框 -->
    <el-dialog v-model="dialogVisible" title="反馈详情" width="500px">
      <div class="feedback-detail">
        <div class="detail-item">
          <span class="label">反馈类型：</span>
          <span>{{ getFeedbackTypeName(currentFeedback.feedbackType) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">反馈内容：</span>
          <p class="content">{{ currentFeedback.content }}</p>
        </div>
        <div class="detail-item" v-if="currentFeedback.reply">
          <span class="label">管理员回复：</span>
          <p class="reply">{{ currentFeedback.reply }}</p>
        </div>
        <div class="detail-item">
          <span class="label">提交时间：</span>
          <span>{{ formatDateTime(currentFeedback.createTime) }}</span>
        </div>
      </div>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const feedbackFormRef = ref(null);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const feedbackList = ref([]);
const currentFeedback = ref({});

// 表单数据
const feedbackForm = reactive({
  feedbackType: "",
  content: "",
});

// 表单验证规则
const rules = {
  feedbackType: [
    { required: true, message: "请选择反馈类型", trigger: "change" },
  ],
  content: [
    { required: true, message: "请输入反馈内容", trigger: "blur" },
    { min: 10, message: "反馈内容不能少于10个字符", trigger: "blur" },
  ],
};

// 获取反馈类型名称
const getFeedbackTypeName = (type) => {
  const types = {
    0: "功能建议",
    1: "Bug报告",
    2: "用户体验",
    3: "其他",
  };
  return types[type] || "未知类型";
};

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return "未知时间";
  try {
    const date = new Date(dateString);
    return date.toLocaleString();
  } catch (e) {
    return "时间格式错误";
  }
};

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackFormRef.value) return;

  try {
    await feedbackFormRef.value.validate();

    submitting.value = true;
    const userInfo = JSON.parse(localStorage.getItem("user") || "{}");

    const response = await request.post("/manage/feedback", {
      userId: userInfo.userId,
      feedbackType: feedbackForm.feedbackType,
      content: feedbackForm.content,
      createBy: userInfo.username,
    });

    if (response.code === 200) {
      ElMessage.success("反馈提交成功");
      feedbackForm.feedbackType = "";
      feedbackForm.content = "";
      fetchFeedbackList(); // 刷新列表
    } else {
      ElMessage.error(response.msg || "提交失败");
    }
  } catch (error) {
    console.error("提交反馈失败:", error);
    ElMessage.error("表单验证失败或网络错误");
  } finally {
    submitting.value = false;
  }
};

// 获取反馈列表
const fetchFeedbackList = async () => {
  loading.value = true;
  try {
    const userInfo = JSON.parse(localStorage.getItem("user") || "{}");
    const response = await request.get("/manage/feedback/list", {
      params: {
        userId: userInfo.userId,
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      },
    });
    console.log(userInfo.userId);
    if (response.code === 200) {
      feedbackList.value = response.rows;
      total.value = response.total;
    } else {
      ElMessage.warning(response.msg || "获取反馈列表失败");
    }
  } catch (error) {
    console.error("获取反馈列表失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  } finally {
    loading.value = false;
  }
};

// 查看反馈详情
const viewFeedbackDetail = (feedback) => {
  currentFeedback.value = feedback;
  dialogVisible.value = true;
};

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchFeedbackList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchFeedbackList();
};

onMounted(() => {
  fetchFeedbackList();
});
</script>

<style scoped>
.feedback-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.feedback-history {
  margin-top: 30px;
}

.history-header {
  margin-bottom: 20px;
}

.history-header h4 {
  margin: 0;
  color: #374151;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.feedback-detail {
  padding: 10px;
}

.detail-item {
  margin-bottom: 15px;
}

.detail-item .label {
  font-weight: bold;
  color: #374151;
}

.detail-item .content,
.detail-item .reply {
  margin: 10px 0;
  white-space: pre-wrap;
  color: #4b5563;
}

.detail-item .reply {
  background-color: #f3f4f6;
  padding: 10px;
  border-radius: 4px;
}
</style>
