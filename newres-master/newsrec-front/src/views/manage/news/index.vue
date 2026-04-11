<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="新闻标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入新闻标题"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属分类" prop="categoryId">
        <el-select
          v-model="queryParams.categoryId"
          placeholder="请选择新闻分类"
          clearable
        >
          <el-option
            v-for="item in categoriesList"
            :key="item.categoryId"
            :label="item.categoryName"
            :value="item.categoryId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="新闻状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择新闻状态"
          clearable
        >
          <el-option
            v-for="dict in tb_examine"
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

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['manage:news:add']"
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
          v-hasPermi="['manage:news:edit']"
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
          v-hasPermi="['manage:news:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['manage:news:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        v-model:showSearch="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <!-- 新闻列表 -->
    <el-table
      v-loading="loading"
      :data="newsList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
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
      <el-table-column label="新闻图片" align="center" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.img" :width="80" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="新闻内容" align="center">
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
      <el-table-column
        label="发布人"
        align="center"
        prop="username"
        width="90"
      />
      <el-table-column
        label="分类"
        align="center"
        prop="categoryName"
        width="60"
      />
      <el-table-column
        label="评论"
        align="center"
        prop="commentCount"
        width="50"
      />
      <el-table-column
        label="浏览量"
        align="center"
        prop="viewCount"
        width="65"
      />
      <el-table-column
        label="点赞"
        align="center"
        prop="likeCount"
        width="50"
      />
      <el-table-column label="新闻状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="tb_examine" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
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
            v-hasPermi="['manage:news:edit']"
            >修改</el-button
          >
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['manage:news:remove']"
            >删除</el-button
          >
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
      title="新闻内容"
      v-model="dialogVisible"
      width="60%"
      :before-close="handleClose"
    >
      <div class="content-container" v-html="currentContent"></div>
    </el-dialog>

    <!-- 新闻编辑弹窗 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="newsRef" :model="form" :rules="rules" label-width="80px">
        <!-- 新闻标题 -->
        <el-form-item label="新闻标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入新闻标题" />
        </el-form-item>

        <!-- 新闻状态（仅在修改时显示） -->
        <el-form-item label="新闻状态" prop="status" v-if="form.newsId">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in tb_examine"
              :key="dict.value"
              :label="parseInt(dict.value)"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>

        <!-- 新闻图片 -->
        <el-form-item label="缩放图" prop="img">
          <image-upload v-model="form.img" />
        </el-form-item>

        <!-- 所属分类 -->
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择新闻分类">
            <el-option
              v-for="item in categoriesList"
              :key="item.categoryId"
              :label="item.categoryName"
              :value="item.categoryId"
            />
          </el-select>
        </el-form-item>

        <!-- <el-form-item label="发布人">
          <el-input v-model="form.username" placeholder="发布人" disabled />
        </el-form-item> -->

        <!-- 新闻内容 -->
        <el-form-item label="新闻内容">
          <editor v-model="form.content" :min-height="192" />
        </el-form-item>

        <!-- 备注 -->
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

<script setup name="News">
import {
  listNews,
  getNews,
  delNews,
  addNews,
  updateNews,
} from "@/api/manage/news";
import { listCategories } from "@/api/manage/categories";
import { ref, reactive, toRefs, onMounted, watch } from "vue";
import { useRoute } from "vue-router";

const { proxy } = getCurrentInstance();
const { tb_examine } = proxy.useDict("tb_examine");

const newsList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

// 富文本查看
const dialogVisible = ref(false);
const currentContent = ref("");
const openContentDialog = (content) => {
  currentContent.value = content;
  dialogVisible.value = true;
};
const handleClose = () => {
  dialogVisible.value = false;
};
//富文本查看

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 8,
    title: null,
    content: undefined,
    categoryId: null,
    username: null,
    status: null,
  },
  rules: {
    title: [{ required: true, message: "新闻标题不能为空", trigger: "blur" }],
    content: [{ required: true, message: "新闻内容不能为空", trigger: "blur" }],
    categoryId: [
      { required: true, message: "所属分类ID不能为空", trigger: "blur" },
    ],
    img: [{ required: true, message: "图片不能为空", trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

// 获取路由对象
const route = useRoute();

/** 查询新闻列表 */
function getList() {
  loading.value = true;
  listNews(queryParams.value).then((response) => {
    newsList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消操作
function cancel() {
  open.value = false;
  reset();
}

//表单重置
function reset() {
  form.value = {
    newsId: null,
    title: null,
    content: null,
    categoryId: null,
    userId: null,
    status: 0, //默认添加，为待审核状态
    remark: null,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
    img: null,
  };
  proxy.resetForm("newsRef");
}

// 搜索操作
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

// 重置查询
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选操作
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.newsId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

// 新增操作
function handleAdd() {
  reset();
  console.log(form.value);
  form.value.userId = 2; // 从后台管理系统添加：默认设置发布人 ID 为 2，admin管理员
  open.value = true;
  title.value = "添加新闻内容";
}

//修改编辑操作
function handleUpdate(row) {
  const _newsId = row.newsId || ids.value;
  getNews(_newsId).then((response) => {
    console.log(response.data); // 检查返回数据
    reset();
    form.value = response.data;
    console.log(form.value); // 检查表单数据
    open.value = true;
    title.value = "修改新闻内容";
  });
}

// 提交表单
function submitForm() {
  proxy.$refs["newsRef"].validate((valid) => {
    if (valid) {
      if (form.value.newsId) {
        updateNews(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addNews(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
      reset();
    }
  });
}

// 删除操作
function handleDelete(row) {
  const _newsIds = row.newsId || ids.value;
  proxy.$modal
    .confirm('是否确认删除新闻内容编号为"' + _newsIds + '"的数据项？')
    .then(() => delNews(_newsIds))
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

// 导出操作
function handleExport() {
  proxy.download(
    "manage/news/export",
    {
      ...queryParams.value,
    },
    `news_${new Date().getTime()}.xlsx`
  );
}

// 加载新闻分类
const categoriesList = ref([]);
function getCategoriesList() {
  listCategories().then((response) => {
    categoriesList.value = response.rows;
  });
}
getCategoriesList();

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
.content-container {
  max-width: 100%;
  overflow: auto;
}

.content-container img {
  max-width: 100%;
  height: auto;
}

.content-container p {
  word-wrap: break-word;
  white-space: normal;
}
</style>
