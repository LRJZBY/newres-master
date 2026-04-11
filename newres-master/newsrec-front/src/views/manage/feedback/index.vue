<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="反馈类型" prop="feedbackType">
        <el-select
          v-model="queryParams.feedbackType"
          placeholder="请选择反馈类型"
          clearable
        >
          <el-option
            v-for="dict in reply_user"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
        >
          <el-option
            v-for="dict in reply_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['manage:feedback:edit']"
        >
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['manage:feedback:remove']"
        >
          删除
        </el-button>
      </el-col>
      <right-toolbar
        v-model:showSearch="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="feedbackList"
      @selection-change="handleSelectionChange"
      style="width: 100%"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        label="序号"
        type="index"
        width="50"
        align="center"
        prop="id"
      />
      <el-table-column
        label="用户名称"
        align="center"
        prop="userName"
        width="80"
      />
      <el-table-column label="反馈类型" align="center" prop="feedbackType">
        <template #default="scope">
          <dict-tag :options="reply_user" :value="scope.row.feedbackType" />
        </template>
      </el-table-column>
      <el-table-column
        label="反馈内容"
        align="center"
        prop="content"
        min-width="150"
      />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="scope.row.status === 0 ? 'danger' : 'success'">
            <dict-tag :options="reply_status" :value="scope.row.status" />
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="管理员回复内容"
        align="center"
        prop="reply"
        width="220"
      >
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="160"
      >
        <template #default="scope">
          <span>{{
            parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}:{s}")
          }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <!-- 根据状态显示不同的按钮 -->
          <el-button
            v-if="scope.row.status === 0"
            link
            type="primary"
            icon="Edit"
            @click="openReplyDialog(scope.row)"
          >
            回复
          </el-button>
          <el-button
            v-else-if="scope.row.status === 1"
            link
            type="primary"
            icon="Edit"
            @click="openReplyDialog(scope.row)"
          >
            修改
          </el-button>
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['manage:feedback:remove']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 回复/修改对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
    >
      <el-form
        ref="feedbackRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="回复内容" prop="reply">
          <el-input
            v-model="form.reply"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Feedback">
import {
  listFeedback,
  getFeedback,
  delFeedback,
  updateFeedback,
} from "@/api/manage/feedback";
import { useRoute } from "vue-router";
import { onMounted, watch } from "vue";

const { proxy } = getCurrentInstance();
const { reply_user, reply_status } = proxy.useDict(
  "reply_user",
  "reply_status"
);

const feedbackList = ref([]);
const dialogVisible = ref(false); // 对话框是否可见
const dialogTitle = ref(""); // 对话框标题
const form = ref({
  feedbackId: null,
  reply: null,
}); // 表单数据
const rules = ref({
  reply: [{ required: true, message: "回复内容不能为空", trigger: "blur" }],
}); // 表单验证规则

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    feedbackType: null,
    status: null,
  },
});

const { queryParams } = toRefs(data);

const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

// 获取路由对象
const route = useRoute();

/** 查询问题反馈列表 */
function getList() {
  loading.value = true;
  listFeedback(queryParams.value).then((response) => {
    feedbackList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  dialogVisible.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    feedbackId: null,
    reply: null,
  };
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.feedbackId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

// 打开回复/修改对话框
function openReplyDialog(row) {
  reset();
  form.value.feedbackId = row.feedbackId;
  form.value.reply = row.reply || "";
  dialogTitle.value = row.status === 0 ? "回复" : "修改";
  dialogVisible.value = true;
}

// 提交表单
function submitForm() {
  proxy.$refs["feedbackRef"].validate((valid) => {
    if (valid) {
      updateFeedback({
        feedbackId: form.value.feedbackId,
        reply: form.value.reply,
        status: 1,
      }).then((response) => {
        proxy.$modal.msgSuccess(
          dialogTitle.value === "回复反馈" ? "回复成功" : "修改成功"
        );
        dialogVisible.value = false;
        reset();
        getList();
      });
    }
  });
}

// 删除按钮操作
function handleDelete(row) {
  const _feedbackIds = row.feedbackId || ids.value;
  proxy.$modal
    .confirm('是否确认删除问题反馈编号为"' + _feedbackIds + '"的数据项？')
    .then(function () {
      return delFeedback(_feedbackIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

// 监听路由参数变化
watch(
  () => route.query,
  (newQuery) => {
    // 检查路由参数中是否有status
    if (newQuery.status !== undefined) {
      // 只有当status值变化时才重新加载
      if (queryParams.value.status !== Number(newQuery.status)) {
        queryParams.value.status = Number(newQuery.status);
        handleQuery(); // 使用参数进行查询
      }
    } else {
      // 如果没有status参数，但之前有，则清除筛选条件
      if (queryParams.value.status !== null) {
        queryParams.value.status = null;
        handleQuery();
      }
    }
  },
  { immediate: true } // 立即执行一次
);

getList();
</script>

<style scoped>
/* 根据状态设置行背景色 */
.el-table .pending-row {
  background-color: #ffcccc; /* 待回复的红色背景 */
}

.el-table .replied-row {
  background-color: #ccffcc; /* 已回复的绿色背景 */
}
</style>
