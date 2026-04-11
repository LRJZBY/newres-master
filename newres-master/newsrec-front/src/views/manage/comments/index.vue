<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="评论内容" prop="content">
        <el-input
          v-model="queryParams.content"
          placeholder="请输入评论内容"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发布人" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="请输入用户"
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
          v-hasPermi="['manage:comments:add']"
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
          v-hasPermi="['manage:comments:edit']"
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
          v-hasPermi="['manage:comments:remove']"
          >删除</el-button
        >
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['manage:comments:export']"
          >导出</el-button
        >
      </el-col> -->
      <right-toolbar
        v-model:showSearch="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="commentsList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="评论ID" align="center" prop="commentId" /> -->
      <el-table-column
        label="序号"
        type="index"
        width="50"
        align="center"
        prop="id"
      />
      <el-table-column label="发布人" align="center" prop="username" />
      <!-- <el-table-column label="新闻标题" align="center" prop="title" /> -->
      <el-table-column label="新闻标题" align="center" width="150">
        <template #default="scope">
          <el-tooltip :content="scope.row.title" placement="top" effect="dark">
            <span :title="scope.row.title" class="title-text">
              {{
                scope.row.title.slice(0, 8) +
                (scope.row.title.length > 8 ? "..." : "")
              }}
            </span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="评论内容" align="center" width="450px">
        <template #default="scope">
          <el-tooltip
            :content="scope.row.content"
            placement="top"
            effect="dark"
          >
            <span :title="scope.row.content" class="content-text">
              {{
                scope.row.content.length > 20
                  ? scope.row.content.slice(0, 20) + "..."
                  : scope.row.content
              }}
            </span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
      >
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
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
            v-hasPermi="['manage:comments:edit']"
            >修改</el-button
          >
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['manage:comments:remove']"
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

    <!-- 添加或修改评论对话框 -->
    <el-dialog :title="title" v-model="open" width="550px" append-to-body>
      <el-form
        ref="commentsRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
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
        <el-form-item label="评论内容">
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

<script setup name="Comments">
import {
  listComments,
  getComments,
  delComments,
  addComments,
  updateComments,
} from "@/api/manage/comments";

//获取其他表的数据
import { listUsers } from "@/api/manage/users";
import { listNews } from "@/api/manage/news";

const { proxy } = getCurrentInstance();

const commentsList = ref([]);
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
    content: null, // 添加评论内容查询字段
  },
  rules: {
    userId: [{ required: true, message: "用户ID不能为空", trigger: "blur" }],
    newsId: [{ required: true, message: "新闻ID不能为空", trigger: "blur" }],
    content: [{ required: true, message: "评论内容不能为空", trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询评论列表 */
function getList() {
  loading.value = true;
  listComments(queryParams.value).then((response) => {
    commentsList.value = response.rows;
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
    commentId: null,
    userId: null,
    newsId: null,
    content: null,
    remark: null,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
  };
  proxy.resetForm("commentsRef");
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
  ids.value = selection.map((item) => item.commentId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加评论（后台测试版）";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _commentId = row.commentId || ids.value;
  getComments(_commentId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改评论（后台测试版）";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["commentsRef"].validate((valid) => {
    if (valid) {
      if (form.value.commentId != null) {
        updateComments(form.value).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addComments(form.value).then((response) => {
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
  const _commentIds = row.commentId || ids.value;
  proxy.$modal
    .confirm('是否确认删除评论编号为"' + _commentIds + '"的数据项？')
    .then(function () {
      return delComments(_commentIds);
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
    "manage/comments/export",
    {
      ...queryParams.value,
    },
    `comments_${new Date().getTime()}.xlsx`
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
