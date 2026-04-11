<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="用户名称" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="请输入用户名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['manage:messages:add']"
        >
          新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['manage:messages:edit']"
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
          v-hasPermi="['manage:messages:remove']"
        >
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['manage:messages:export']"
        >
          导出
        </el-button>
      </el-col>
      <right-toolbar
        v-model:showSearch="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="messagesList"
      @selection-change="handleSelectionChange"
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
        prop="username"
        width="150"
      />
      <el-table-column label="用户头像" align="center" prop="avatar">
        <template #default="scope">
          <image-preview :src="scope.row.avatar" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="留言内容" align="center">
        <template #default="scope">
          <el-button
            type="text"
            style="
              color: black;
              background-color: white;
              border: 1px solid #ccc;
              padding: 5px 10px;
            "
            @click="openContentDialog(scope.row.content)"
          >
            查看内容
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template #default="scope">
          <span>{{
            parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}:{s}")
          }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="备注" align="center" prop="remark" width="270" /> -->
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
            @click="handleReply(scope.row)"
            v-hasPermi="['manage:messages:edit']"
          >
            回复
          </el-button>
          <el-button
            v-else-if="scope.row.status === 1"
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['manage:messages:edit']"
          >
            修改
          </el-button>
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['manage:messages:remove']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 富文本显示弹窗 -->
    <el-dialog
      title="留言内容"
      v-model="dialogVisible"
      width="60%"
      :before-close="handleClose"
    >
      <div class="content-container" v-html="currentContent"></div>
    </el-dialog>

    <!-- 添加或修改留言对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form
        ref="messagesRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <!-- 用户下拉框 -->
        <el-form-item label="用户名称" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择用户">
            <el-option
              v-for="user in usersList"
              :key="user.userId"
              :label="user.username"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="留言内容">
          <editor v-model="form.content" :min-height="192" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入内容"
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

<script setup name="Messages">
import {
  listMessages,
  getMessages,
  delMessages,
  addMessages,
  updateMessages,
} from "@/api/manage/messages";
import { listUsers } from "@/api/manage/users";

const { proxy } = getCurrentInstance();

const messagesList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dialogVisible = ref(false);
const currentContent = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: null,
    content: null,
  },
  rules: {
    userId: [{ required: true, message: "用户ID不能为空", trigger: "blur" }],
    content: [{ required: true, message: "留言内容不能为空", trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询留言列表 */
function getList() {
  loading.value = true;
  listMessages(queryParams.value).then((response) => {
    messagesList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 打开内容查看对话框
function openContentDialog(content) {
  currentContent.value = content;
  dialogVisible.value = true;
}

// 关闭对话框
function handleClose() {
  dialogVisible.value = false;
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    messageId: null,
    userId: null,
    parentId: null,
    content: null,
    remark: null,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
  };
  proxy.resetForm("messagesRef");
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
  ids.value = selection.map((item) => item.messageId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加留言(后台测试版)";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _messageId = row.messageId || ids.value;
  getMessages(_messageId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改留言(后台测试版)";
  });
}

/** 回复按钮操作 */
function handleReply(row) {
  reset();
  form.value = { ...row }; // 复制当前行的数据到表单
  open.value = true;
  title.value = "回复留言(后台测试版)";
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["messagesRef"].validate((valid) => {
    if (valid) {
      if (form.value.messageId != null) {
        updateMessages(form.value).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addMessages(form.value).then((response) => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _messageIds = row.messageId || ids.value;
  proxy.$modal
    .confirm('是否确认删除留言编号为"' + _messageIds + '"的数据项？')
    .then(function () {
      return delMessages(_messageIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    "manage/messages/export",
    {
      ...queryParams.value,
    },
    `messages_${new Date().getTime()}.xlsx`
  );
}

// 加载用户数据
const usersList = ref([]);
function getUsersList() {
  listUsers().then((response) => {
    usersList.value = response.rows;
  });
}
getUsersList();

getList();
</script>

<style scoped>
.content-container {
  max-width: 100%;
  overflow: auto;
  padding: 20px;
  border-radius: 8px;
  background-color: #fff;
}

.content-container img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 10px auto;
}

.content-container p {
  word-wrap: break-word;
  white-space: normal;
  margin-bottom: 10px;
}

/* 表格内容样式 */
.el-table .cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
