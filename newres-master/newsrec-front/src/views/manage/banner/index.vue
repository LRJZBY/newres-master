<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="新闻标题" prop="newsId">
        <el-select
          v-model="queryParams.newsId"
          placeholder="请选择新闻"
          clearable
          @change="handleQuery"
        >
          <el-option
            v-for="item in newsList"
            :key="item.newsId"
            :label="item.title"
            :value="item.newsId"
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
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['manage:banner:add']"
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
          v-hasPermi="['manage:banner:edit']"
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
          v-hasPermi="['manage:banner:remove']"
          >删除</el-button
        >
      </el-col>
      <!-- 添加：点击刷新的按钮，调用refreshBanner接口，实现轮播图的手动刷新 -->
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Refresh" @click="handleRefresh"
          >更新</el-button
        >
      </el-col>

      <right-toolbar
        v-model:showSearch="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="bannerList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="轮播图ID" align="center" prop="bannerId" /> -->
      <el-table-column
        label="序号"
        type="index"
        width="50"
        align="center"
        prop="id"
      />
      <el-table-column label="图片" align="center" prop="img" width="140">
        <template #default="scope">
          <image-preview :src="scope.row.img" :width="80" :height="70" />
        </template>
      </el-table-column>
      <el-table-column label="新闻标题" align="center" prop="title" />
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
      <el-table-column label="推送标语" align="center" prop="remark" />
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
            v-hasPermi="['manage:banner:edit']"
            >修改</el-button
          >
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['manage:banner:remove']"
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

    <!-- 添加或修改轮播图对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="bannerRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="图片" prop="img">
          <image-upload v-model="form.img" />
        </el-form-item>
        <!-- <el-form-item label="新闻ID" prop="newsId">
          <el-input v-model="form.newsId" placeholder="请输入新闻ID" />
        </el-form-item> -->
        <el-form-item label="所属新闻" prop="newsId">
          <el-select
            v-model="form.newsId"
            placeholder="请选择新闻"
            style="width: 100%"
          >
            <el-option
              v-for="item in newsList"
              :key="item.newsId"
              :label="item.title"
              :value="item.newsId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="推送标语" prop="remark">
          <el-input v-model="form.remark" placeholder="推送标语" />
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

<script setup name="Banner">
import {
  listBanner,
  getBanner,
  delBanner,
  addBanner,
  updateBanner,
  refreshBanner,
} from "@/api/manage/banner";

import { listNews } from "@/api/manage/news";

const { proxy } = getCurrentInstance();

const bannerList = ref([]);
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
    img: null,
    newsId: null,
  },
  rules: {
    img: [{ required: true, message: "图片不能为空", trigger: "blur" }],
    newsId: [{ required: true, message: "新闻ID不能为空", trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询轮播图列表 */
function getList() {
  loading.value = true;
  listBanner(queryParams.value).then((response) => {
    bannerList.value = response.rows;
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
    bannerId: null,
    img: null,
    newsId: null,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
    remark: null,
  };
  proxy.resetForm("bannerRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  // queryParams.value.newsId = null; // 重置新闻标题筛选条件
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.bannerId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加轮播图";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _bannerId = row.bannerId || ids.value;
  getBanner(_bannerId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改轮播图";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["bannerRef"].validate((valid) => {
    if (valid) {
      if (form.value.bannerId != null) {
        updateBanner(form.value).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addBanner(form.value).then((response) => {
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
  const _bannerIds = row.bannerId || ids.value;
  proxy.$modal
    .confirm('是否确认删除轮播图编号为"' + _bannerIds + '"的数据项？')
    .then(function () {
      return delBanner(_bannerIds);
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
    "manage/banner/export",
    {
      ...queryParams.value,
    },
    `banner_${new Date().getTime()}.xlsx`
  );
}

/** 刷新按钮操作 */
function handleRefresh() {
  refreshBanner().then((response) => {
    proxy.$modal.msgSuccess("刷新成功");
    getList();
  });
}

//导入新闻列表
/* 查询所有的条件对象 */
const loadAllParams = reactive({
  pageNum: 1,
  pageSize: 1000,
  status: 1,
});

/* 查询新闻的列表，通用 */
const newsList = ref([]);
function getNewsList() {
  listNews(loadAllParams)
    .then((response) => {
      newsList.value = response.rows || [];
    })
    .catch((error) => {
      console.error("加载新闻列表失败:", error);
    });
}
getNewsList(); // 初始化加载新闻列表

getList();
</script>
