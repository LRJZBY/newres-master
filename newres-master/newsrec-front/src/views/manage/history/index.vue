<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <!-- <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="新闻ID" prop="newsId">
        <el-input
          v-model="queryParams.newsId"
          placeholder="请输入新闻ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item> -->
      <el-form-item label="用户名称" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="请输入用户名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="新闻标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入新闻标题"
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['manage:history:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['manage:history:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['manage:history:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['manage:history:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        v-model:showSearch="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="historyList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="浏览记录ID" align="center" prop="historyId" /> -->
      <el-table-column
        label="序号"
        type="index"
        width="50"
        align="center"
        prop="id"
      />
      <el-table-column label="用户" align="center" prop="username" />
      <el-table-column
        label="新闻标题"
        align="center"
        prop="title"
        width="460px"
      />
      <el-table-column
        label="浏览时间"
        align="center"
        prop="createTime"
        width="155"
      >
        <template #default="scope">
          <span>{{
            parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}:{s}")
          }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="备注"
        align="center"
        prop="remark"
        width="220px"
      />
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['manage:history:edit']"
            >修改</el-button
          >
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['manage:history:remove']"
            >删除</el-button
          >
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

    <!-- 添加或修改浏览历史对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="historyRef" :model="form" :rules="rules" label-width="80px">
        <!-- 用户下拉框 -->
        <el-form-item label="用户" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择用户">
            <el-option
              v-for="user in usersList"
              :key="user.userId"
              :label="user.username"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>

        <!-- 新闻下拉框 -->
        <el-form-item label="新闻" prop="newsId">
          <el-select v-model="form.newsId" placeholder="请选择新闻">
            <el-option
              v-for="news in newsList"
              :key="news.newsId"
              :label="news.title"
              :value="news.newsId"
            />
          </el-select>
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

<script setup name="History">
import {
  listHistory,
  getHistory,
  delHistory,
  addHistory,
  updateHistory,
} from "@/api/manage/history";

//获取其他表的数据
import { listUsers } from "@/api/manage/users";
import { listNews } from "@/api/manage/news";

const { proxy } = getCurrentInstance();

const historyList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: null,
    newsId: null,
    username: null, // 用户名称查询条件
    title: null, // 新闻标题查询条件
  },
  rules: {
    userId: [{ required: true, message: "用户ID不能为空", trigger: "blur" }],
    newsId: [{ required: true, message: "新闻ID不能为空", trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询浏览历史列表 */
function getList() {
  loading.value = true;
  listHistory(queryParams.value).then((response) => {
    historyList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    historyId: null,
    userId: null,
    newsId: null,
    viewedAt: null,
    remark: null,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
  };
  proxy.resetForm("historyRef");
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
  ids.value = selection.map((item) => item.historyId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加浏览历史（后台测试版本）";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _historyId = row.historyId || ids.value;
  getHistory(_historyId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改浏览历史（后台测试版本）";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["historyRef"].validate((valid) => {
    if (valid) {
      if (form.value.historyId != null) {
        updateHistory(form.value).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addHistory(form.value).then((response) => {
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
  const _historyIds = row.historyId || ids.value;
  proxy.$modal
    .confirm('是否确认删除浏览历史编号为"' + _historyIds + '"的数据项？')
    .then(function () {
      return delHistory(_historyIds);
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
    "manage/history/export",
    {
      ...queryParams.value,
    },
    `history_${new Date().getTime()}.xlsx`
  );
}

/* 查询所有的条件对象 */
const loadAllParams = reactive({
  pageNum: 1,
  pageSize: 1000,
  status: 1,
});

// 加载新闻数据
const newsList = ref([]);
function getNewsList() {
  listNews(loadAllParams).then((response) => {
    newsList.value = response.rows;
  });
}
getNewsList();

//加载用户数据
const usersList = ref([]);
function getUsersList() {
  listUsers().then((response) => {
    usersList.value = response.rows;
  });
}
getUsersList();

getList();
</script>
