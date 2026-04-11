<template>
  <div class="preferences">
    <h2>偏好设置</h2>
    <p class="description">
      设置您感兴趣的新闻分类及优先级，我们将为您提供更加个性化的新闻推荐
    </p>

    <!-- 顶部按钮区域 -->
    <div class="top-actions">
      <el-button
        type="primary"
        @click="openCategoryDialog"
        size="large"
        :disabled="userPreferences.length >= 5"
        :icon="Plus"
      >
        添加分类喜好
      </el-button>
      <div class="preference-tips">
        <el-tag type="info" effect="plain"
          >至少选择3个分类，最多5个，点击确定后将直接保存</el-tag
        >
      </div>
    </div>

    <div class="preferences-content">
      <!-- 无数据提示 -->
      <el-empty
        v-if="userPreferences.length === 0"
        description="您还没有设置任何分类喜好"
        :image-size="180"
      >
        <span class="empty-tip">设置分类喜好可以获得更加精准的新闻推荐</span>
      </el-empty>

      <!-- 分类偏好卡片列表 -->
      <div v-else class="preference-list">
        <el-row :gutter="20">
          <el-col
            v-for="(pref, index) in userPreferences"
            :key="index"
            :xs="24"
            :sm="12"
            :md="8"
          >
            <el-card class="preference-card" :body-style="{ padding: '0px' }">
              <div class="card-header">
                <span class="category-name">{{
                  getCategoryName(pref.categoryId)
                }}</span>
                <el-popconfirm
                  :title="`确定移除'${getCategoryName(
                    pref.categoryId
                  )}'分类吗？`"
                  confirm-button-text="确定"
                  cancel-button-text="取消"
                  @confirm="removePreference(index, pref)"
                >
                  <template #reference>
                    <el-button type="danger" size="small" circle>
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-popconfirm>
              </div>
              <div class="card-body">
                <div class="priority-slider">
                  <div class="priority-labels">
                    <span class="priority-title">优先级</span>
                    <span class="priority-value">{{
                      formatPriorityLabel(pref.priority)
                    }}</span>
                  </div>
                  <el-slider
                    v-model="pref.priority"
                    :min="1"
                    :max="3"
                    :step="1"
                    :marks="{ 1: '高', 2: '中', 3: '低' }"
                    :format-tooltip="formatPriorityTooltip"
                    @change="handlePriorityChange(pref, $event)"
                  ></el-slider>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 底部保存按钮 -->
      <div class="bottom-actions">
        <el-button
          type="primary"
          @click="refreshPreferences"
          :loading="refreshing"
          size="large"
          :icon="Refresh"
        >
          更新设置
        </el-button>
        <div
          class="validation-message"
          v-if="userPreferences.length > 0 && userPreferences.length < 3"
        >
          <el-alert
            title="您当前的设置不足3个分类，建议添加更多分类以获得更好的推荐"
            type="warning"
            :closable="false"
            show-icon
          />
        </div>
      </div>
    </div>

    <!-- 分类选择对话框 -->
    <el-dialog
      v-model="categoryDialogVisible"
      title="设置新闻分类喜好"
      width="60%"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <div class="category-selection">
        <el-alert
          title="请选择您感兴趣的新闻分类，并设置优先级。最少选择3个，最多5个分类。点击确定后将直接保存。"
          type="info"
          :closable="false"
          class="dialog-tip"
        />

        <div class="selection-container">
          <div class="categories-wrapper">
            <p class="section-title">
              所有分类
              <span class="selection-count"
                >(已选择 {{ selectedCategories.length }} 项)</span
              >
            </p>
            <div class="available-categories">
              <el-checkbox-group v-model="selectedCategories" :max="5">
                <el-checkbox
                  v-for="category in categories"
                  :key="category.categoryId"
                  :label="category.categoryId"
                  :disabled="
                    selectedCategories.length >= 5 &&
                    !selectedCategories.includes(category.categoryId)
                  "
                  class="category-checkbox"
                >
                  {{ category.categoryName }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
          </div>

          <div class="priorities-wrapper" v-if="selectedCategories.length > 0">
            <p class="section-title">设置优先级</p>
            <div class="priority-settings">
              <div
                v-for="categoryId in selectedCategories"
                :key="categoryId"
                class="priority-setting-item"
              >
                <span class="category-name">{{
                  getCategoryName(categoryId)
                }}</span>
                <div class="priority-control">
                  <el-radio-group
                    v-model="tempPreferences[categoryId]"
                    size="small"
                  >
                    <el-radio-button :label="1">低</el-radio-button>
                    <el-radio-button :label="2">中</el-radio-button>
                    <el-radio-button :label="3">高</el-radio-button>
                  </el-radio-group>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="categoryDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="confirmCategorySelection"
            :disabled="selectedCategories.length < 3"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import { Plus, Delete, Refresh } from "@element-plus/icons-vue";

// 分类相关
const categories = ref([]);
const userPreferences = ref([]);
const saving = ref(false);
const refreshing = ref(false);

// 对话框相关
const categoryDialogVisible = ref(false);
const selectedCategories = ref([]);
const tempPreferences = ref({});

// 获取用户ID
const getUserId = () => {
  const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
  return storedUser.userId;
};

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await request.get("/manage/categories/list");
    if (response.code === 200) {
      categories.value = response.rows;
      console.log("新闻分类", categories.value);
    }
  } catch (error) {
    console.error("获取分类列表失败:", error);
    ElMessage.error("获取分类列表失败");
  }
};

// 获取用户喜好设置
const fetchUserPreferences = async () => {
  try {
    const userId = getUserId();
    if (!userId) {
      ElMessage.warning("请先登录");
      return;
    }

    // 查询用户偏好列表 - 使用正确的参数格式
    const userPreferencesParam = { userId: userId };
    const response = await request.get("/manage/preferences/list", {
      params: userPreferencesParam,
    });

    if (response.code === 200 && response.rows && response.rows.length > 0) {
      // 更新标记，表示这些是从后端获取的记录
      userPreferences.value = response.rows.map((pref) => ({
        ...pref,
        fromBackend: true, // 标记为后端数据，避免重复保存
        modified: false, // 标记是否被修改
      }));
      console.log("用户喜好设置:", userPreferences.value);
    } else {
      // 如果API没有返回数据，初始化为空数组
      userPreferences.value = [];
      console.log("用户暂无喜好设置");
    }
  } catch (error) {
    console.error("获取用户喜好设置失败:", error);
    ElMessage.error("网络错误，请稍后再试");
    // 初始化为空数组
    userPreferences.value = [];
  }
};

// 打开分类选择对话框
const openCategoryDialog = () => {
  // 获取当前已选分类ID列表
  selectedCategories.value = userPreferences.value.map(
    (pref) => pref.categoryId
  );

  // 初始化临时优先级设置
  tempPreferences.value = {};
  userPreferences.value.forEach((pref) => {
    tempPreferences.value[pref.categoryId] = pref.priority;
  });

  // 为新选择的分类设置默认优先级
  categories.value.forEach((category) => {
    if (!tempPreferences.value[category.categoryId]) {
      tempPreferences.value[category.categoryId] = 2; // 默认中等优先级
    }
  });

  categoryDialogVisible.value = true;
};

// 处理对话框关闭
const handleDialogClose = () => {
  selectedCategories.value = [];
  tempPreferences.value = {};
};

// 移除已选偏好并删除后端数据
const removePreference = async (index, preference) => {
  try {
    // 从前端列表中移除
    const removedPref = userPreferences.value.splice(index, 1)[0];

    // 如果是从后端加载的数据且有preferenceId，需要调用API删除
    if (removedPref.fromBackend && removedPref.preferenceId) {
      // 调用数组形式的删除端点
      const preferenceIds = [removedPref.preferenceId];
      const response = await request.delete(
        `/manage/preferences/${preferenceIds}`
      );

      if (response.code === 200) {
        ElMessage.success("分类偏好已移除");
      } else {
        // 如果删除失败，将记录放回列表
        userPreferences.value.splice(index, 0, removedPref);
        ElMessage.warning(response.msg || "移除失败，请重试");
      }
    } else {
      // 如果是本地临时添加的（没有preferenceId或不是从后端加载），仅需本地移除
      ElMessage.success("分类偏好已移除");
    }
  } catch (error) {
    console.error("删除用户偏好失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  }
};

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(
    (cat) => cat.categoryId === categoryId
  );
  return category ? category.categoryName : "未知分类";
};

// 处理优先级变化
const handlePriorityChange = async (preference, value) => {
  preference.priority = Number(value); // 确保转换为数字类型

  try {
    // 如果是从后端加载的数据，直接调用API更新
    if (preference.fromBackend && preference.preferenceId) {
      const updatePref = {
        preferenceId: Number(preference.preferenceId),
        userId: Number(getUserId()),
        categoryId: Number(preference.categoryId),
        priority: Number(preference.priority),
      };

      // 显示正在保存的状态
      const loadingMessage = ElMessage({
        message: "正在保存更改...",
        type: "info",
        duration: 0,
      });

      const response = await request.put("/manage/preferences", updatePref);

      // 关闭加载消息
      loadingMessage.close();

      if (response.code === 200) {
        ElMessage.success("优先级已更新");
      } else {
        ElMessage.warning(response.msg || "更新失败，请重试");
        // 如果失败，回滚更改
        preference.priority =
          preference.priority > 1
            ? preference.priority - 1
            : preference.priority + 1;
      }
    }
  } catch (error) {
    console.error(`更新优先级失败: 分类ID=${preference.categoryId}`, error);
    ElMessage.error("网络错误，请稍后再试");
    // 如果出错，回滚更改
    preference.priority =
      preference.priority > 1
        ? preference.priority - 1
        : preference.priority + 1;
  }

  console.log(
    `优先级已更新: 分类ID=${preference.categoryId}, 新优先级=${preference.priority}`
  );
};

// 确认分类选择并直接保存到后端
const confirmCategorySelection = async () => {
  // 验证是否选择了至少3个分类
  if (selectedCategories.value.length < 3) {
    ElMessage.warning("请至少选择3个分类");
    return;
  }

  try {
    // 显示加载状态
    saving.value = true;

    const userId = getUserId();
    if (!userId) {
      ElMessage.warning("用户未登录或无效");
      saving.value = false;
      return;
    }

    // 获取当前所有分类ID
    const currentCategoryIds = userPreferences.value.map(
      (pref) => pref.categoryId
    );

    // 找出需要新增的分类ID
    const newCategoryIds = selectedCategories.value.filter(
      (id) => !currentCategoryIds.includes(id)
    );

    // 找出需要删除的分类（当前有但选择中没有）
    const removedCategoryIds = currentCategoryIds.filter(
      (id) => !selectedCategories.value.includes(id)
    );

    // 找出需要更新的已有分类
    const updatedCategoryIds = selectedCategories.value.filter(
      (id) =>
        currentCategoryIds.includes(id) &&
        tempPreferences.value[id] !==
          userPreferences.value.find((p) => p.categoryId === id)?.priority
    );

    const savePromises = [];
    let hasChanges = false;

    // 处理新增的分类
    if (newCategoryIds.length > 0) {
      hasChanges = true;
      for (const categoryId of newCategoryIds) {
        const newPref = {
          userId: Number(userId),
          categoryId: Number(categoryId),
          priority: Number(tempPreferences.value[categoryId] || 2),
        };
        const addPromise = request.post("/manage/preferences", newPref);
        savePromises.push({ type: "add", promise: addPromise });
      }
    }

    // 处理需要更新的分类
    if (updatedCategoryIds.length > 0) {
      hasChanges = true;
      for (const categoryId of updatedCategoryIds) {
        const pref = userPreferences.value.find(
          (p) => p.categoryId === categoryId
        );
        if (pref && pref.preferenceId) {
          const updatePref = {
            preferenceId: Number(pref.preferenceId),
            userId: Number(userId),
            categoryId: Number(categoryId),
            priority: Number(tempPreferences.value[categoryId]),
          };
          const updatePromise = request.put("/manage/preferences", updatePref);
          savePromises.push({ type: "update", promise: updatePromise });
        }
      }
    }

    // 处理需要删除的分类
    if (removedCategoryIds.length > 0) {
      hasChanges = true;
      // 找出需要删除的preferenceId
      const removedPreferences = userPreferences.value.filter(
        (pref) =>
          removedCategoryIds.includes(pref.categoryId) && pref.preferenceId
      );

      for (const pref of removedPreferences) {
        const preferenceIds = [pref.preferenceId];
        const deletePromise = request.delete(
          `/manage/preferences/${preferenceIds}`
        );
        savePromises.push({ type: "delete", promise: deletePromise });
      }
    }

    // 如果没有任何变化，直接关闭对话框
    if (!hasChanges) {
      ElMessage.info("设置未发生变化");
      categoryDialogVisible.value = false;
      saving.value = false;
      return;
    }

    // 等待所有保存操作完成
    const results = await Promise.all(savePromises.map((item) => item.promise));
    const allSuccess = results.every((res) => res.code === 200);

    if (allSuccess) {
      ElMessage.success("喜好设置保存成功");

      // 关闭对话框
      categoryDialogVisible.value = false;

      // 重新获取最新的用户偏好设置，刷新状态
      await fetchUserPreferences();
    } else {
      ElMessage.warning("部分设置保存失败，请重试");
    }
  } catch (error) {
    console.error("保存用户喜好失败:", error);
    ElMessage.error("网络错误，请稍后再试");
  } finally {
    saving.value = false;
  }
};

// 格式化优先级tooltip文本
const formatPriorityTooltip = (val) => {
  const priorities = {
    1: "低优先级",
    2: "中等优先级",
    3: "高优先级",
  };
  return priorities[val] || val;
};

// 格式化优先级显示文本
const formatPriorityLabel = (val) => {
  const priorities = {
    1: "低",
    2: "中",
    3: "高",
  };
  return priorities[val] || val;
};

// 刷新用户喜好设置
const refreshPreferences = async () => {
  try {
    refreshing.value = true;
    await fetchUserPreferences();
    ElMessage.success("已刷新最新设置");
  } catch (error) {
    console.error("刷新设置失败:", error);
    ElMessage.error("刷新失败，请稍后再试");
  } finally {
    refreshing.value = false;
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchCategories(); // 获取分类列表
  fetchUserPreferences(); // 获取用户偏好设置
});
</script>

<style scoped>
.preferences {
  min-height: 100%;
}

.preferences h2 {
  margin-bottom: 8px;
  color: #1f2937;
  font-size: 24px;
}

.description {
  color: #6b7280;
  margin-bottom: 24px;
  font-size: 16px;
}

/* 顶部按钮区域 */
.top-actions {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
}

.preference-tips {
  font-size: 14px;
}

/* 主要内容区域 */
.preferences-content {
  background-color: #f8f9fa;
  border-radius: 10px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 无数据提示 */
.empty-tip {
  color: #909399;
  display: block;
  margin-top: 10px;
  font-size: 14px;
}

/* 分类喜好卡片列表 */
.preference-list {
  margin-top: 10px;
}

.preference-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.preference-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-header {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary-dark-2);
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.category-name {
  font-size: 16px;
}

.card-body {
  padding: 16px;
  background-color: white;
}

.priority-slider {
  padding: 8px 0;
}

.priority-labels {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.priority-title {
  font-size: 14px;
  color: #606266;
}

.priority-value {
  font-weight: bold;
  color: var(--el-color-primary);
}

/* 底部保存按钮 */
.bottom-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
  gap: 16px;
}

.validation-message {
  width: 100%;
  max-width: 400px;
}

/* 分类选择对话框 */
.category-selection {
  padding: 10px 0;
}

.dialog-tip {
  margin-bottom: 20px;
}

.selection-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-title {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
  font-weight: 500;
}

.selection-count {
  font-size: 14px;
  color: #909399;
  font-weight: normal;
}

.available-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.category-checkbox {
  margin-right: 12px;
  margin-bottom: 12px;
}

.priorities-wrapper {
  margin-top: 10px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.priority-settings {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.priority-setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: white;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .selection-container {
    flex-direction: column;
  }

  .priority-setting-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .priority-control {
    margin-top: 8px;
    width: 100%;
  }
}
</style>
