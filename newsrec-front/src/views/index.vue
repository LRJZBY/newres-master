<template>
  <div class="app-container">
    <!-- 顶部统计盒子 -->
    <el-row :gutter="24" class="mb-40">
      <el-col :span="4">
        <el-card
          class="box-card"
          @click="navigateTo('/newsmanage/news', { status: 1 })"
          :class="{ clickable: true }"
        >
          <div class="box-content">
            <img class="icon-box" src="../assets/images/news.png" />
            <div>
              <p style="font-size: 14px">新闻总数</p>
              <h3 style="font-size: 28px">{{ newsTotal }}</h3>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card
          class="box-card"
          @click="navigateTo('/newsmanage/comments')"
          :class="{ clickable: true }"
        >
          <div class="box-content">
            <img class="icon-box" src="../assets/images/comments.png" />
            <div>
              <p style="font-size: 13px">评论总数</p>
              <h3 style="font-size: 28px">{{ commentTotal }}</h3>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card
          class="box-card"
          @click="navigateTo('/Information/users')"
          :class="{ clickable: true }"
        >
          <div class="box-content">
            <img class="icon-box" src="../assets/images/users.png" />
            <div>
              <p style="font-size: 13px">用户总数</p>
              <h3 style="font-size: 28px">{{ userTotal }}</h3>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card
          class="box-card"
          @click="navigateTo('/newsmanage/news', { status: 0 })"
          :class="{ clickable: true }"
        >
          <div class="box-content">
            <img class="icon-box" src="../assets/images/pending.png" />
            <div>
              <p style="font-size: 13px">待审核新闻</p>
              <h3 style="font-size: 28px">{{ pendingTotal }}</h3>
            </div>
          </div>
        </el-card>
      </el-col>
      <!-- 新增：留言总数 -->
      <el-col :span="4">
        <el-card
          class="box-card"
          @click="navigateTo('/Information/messages')"
          :class="{ clickable: true }"
        >
          <div class="box-content">
            <img class="icon-box" src="../assets/images/messages.png" />
            <div>
              <p style="font-size: 13px">留言总数</p>
              <h3 style="font-size: 28px">{{ messageTotal }}</h3>
            </div>
          </div>
        </el-card>
      </el-col>
      <!-- 新增：待处理反馈 -->
      <el-col :span="4">
        <el-card
          class="box-card"
          @click="navigateTo('/Information/feedback', { status: 0 })"
          :class="{ clickable: true }"
        >
          <div class="box-content">
            <img class="icon-box" src="../assets/images/feedback.png" />
            <div>
              <p style="font-size: 13px">待处理反馈</p>
              <h3 style="font-size: 28px">{{ feedbackTotal }}</h3>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="mt-40">
      <el-col :span="14" class="news-list-col">
        <!-- 展示热点新闻的排名榜（竖着） -->
        <el-card class="box-card">
          <div class="card-header">
            <span class="header-title">热点新闻</span>
          </div>
          <div class="news-cards">
            <div
              v-for="(news, index) in hotNewsList"
              :key="news.newsId"
              class="news-card"
              :class="{ 'top-three': index < 3 }"
              @click="showNewsDetail(news)"
            >
              <div
                class="rank-badge"
                :style="{ color: index < 3 ? '#ff7d00' : '#999' }"
              >
                {{ index + 1 }}
              </div>
              <div class="news-content">
                <h3 class="news-title">{{ news.title }}</h3>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <!-- 扇形图表展示分类 -->
      <el-col :span="10">
        <div id="pieChart" style="width: 100%; height: 400px"></div>
      </el-col>
    </el-row>

    <!-- 新闻详情弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentNews.title"
      width="60%"
      :before-close="handleClose"
    >
      <div class="content-container" v-html="currentNews.content"></div>
    </el-dialog>
  </div>
</template>

<script setup name="Index">
import { ref, onBeforeMount, onMounted, watch } from "vue";
import * as echarts from "echarts";
import { getData } from "../api/manage/news";
import { listCategories1 } from "../api/manage/categories";
import { getHotNews } from "../api/manage/news";
import { useRouter } from "vue-router";

// 获取路由实例
const router = useRouter();

// 导航方法
function navigateTo(path, query = {}) {
  router.push({
    path: path,
    query: query,
  });
}

// 数据获取，6个数据封装成json
const DataList = ref([]);
const newsTotal = ref(0);
const commentTotal = ref(0);
const userTotal = ref(0);
const pendingTotal = ref(0);
const messageTotal = ref(0); // 新增：留言总数
const feedbackTotal = ref(0); // 新增：待解决反馈

// 加载数据
function dataList() {
  getData().then((response) => {
    DataList.value = response;
    // 将数据绑定到对应的变量
    newsTotal.value = DataList.value.newsTotal ?? 0;
    commentTotal.value = DataList.value.commentTotal ?? 0;
    userTotal.value = DataList.value.userTotal ?? 0;
    pendingTotal.value = DataList.value.pendingTotal ?? 0;
    messageTotal.value = DataList.value.messageTotal ?? 0; // 新增：留言总数
    feedbackTotal.value = DataList.value.feedbackTotal ?? 0; // 新增：待解决反馈
    console.log(DataList.value);
  });
}
dataList();

//新闻分类定义
const newsCategoriesList1 = ref([]);

//热点新闻数据
const hotNewsList = ref([]);
// 新增：加载新闻分类数据
function loadNewsCategoriesVOList() {
  listCategories1().then((response) => {
    newsCategoriesList1.value = response.rows || [];
    console.log("新闻分类数据：", newsCategoriesList1.value);
    initPieChart(); // 确保数据加载完成后初始化图表
  });
}

loadNewsCategoriesVOList();
// 加载热点新闻数据
function loadHotNewsList() {
  getHotNews().then((response) => {
    hotNewsList.value = response.rows || [];
    console.log("热点新闻数据：", hotNewsList.value);
  });
}
loadHotNewsList();

// 初始化扇形图
let pieChartInstance = null;
function initPieChart() {
  if (pieChartInstance) {
    pieChartInstance.dispose(); // 销毁之前的实例
  }
  pieChartInstance = echarts.init(document.getElementById("pieChart"));

  const totalNews = newsCategoriesList1.value.reduce(
    (acc, item) => acc + (item.newsCount || 0),
    0
  );

  const pieOption = {
    title: {
      text: "新闻分类占比",
      left: "center",
    },
    tooltip: {
      trigger: "item",
      formatter: (params) => {
        const percent = ((params.value / totalNews) * 100).toFixed(2);
        return `${params.name}: ${params.value} (${percent}%)`;
      },
    },
    legend: {
      orient: "vertical",
      left: "left",
    },
    series: [
      {
        name: "新闻分类",
        type: "pie",
        radius: "50%",
        data: newsCategoriesList1.value.map((item) => ({
          value: item.newsCount || 0,
          name: item.categoryName || "未知",
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
        label: {
          show: true,
          formatter: (params) => {
            const percent = ((params.value / totalNews) * 100).toFixed(2);
            return `${params.name}\n${percent}%`;
          },
        },
        labelLine: {
          show: true,
        },
      },
    ],
  };
  pieChartInstance.setOption(pieOption);
}

watch(newsCategoriesList1, () => {
  initPieChart(); // 当新闻分类数据变化时重新初始化饼图
});

// 新闻详情弹窗相关
const dialogVisible = ref(false);
const currentNews = ref({
  title: "",
  content: "",
});

function showNewsDetail(news) {
  currentNews.value = {
    title: news.title,
    content: news.content || "新闻内容加载中...",
  };
  dialogVisible.value = true;
}

function handleClose(done) {
  currentNews.value = {
    title: "",
    content: "",
  };
  done();
}
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
  background-color: #f0f2f5;

  .box-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    &.clickable {
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
      }
    }

    .box-content {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .icon-box {
        width: 60px;
        height: 60px;
        margin-right: 10px;
      }

      div {
        margin-left: 10px;

        p {
          margin: 0;
          font-size: 16px;
          color: #676a6c;
        }

        h3 {
          margin: 0;
          font-size: 28px;
          font-weight: bold;
        }
      }
    }
  }

  .mb-40 {
    margin-bottom: 50px;
  }

  .mt-40 {
    margin-top: 50px;
  }

  .news-list-col {
    margin-top: -20px; // 调整左侧新闻列表的位置，使其向上靠
  }
}

/* 卡片标题样式 */
.card-header {
  text-align: center;
}

.header-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

/* 热点新闻卡片样式 */
.news-cards {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 0 15px;
}

.news-card {
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  }
}

.rank-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #f5f5f5;
  margin-right: 15px;
  font-weight: bold;
}

.news-content {
  flex: 1;
}

.news-title {
  font-size: 16px;
  color: #333;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  &:hover {
    color: #409eff;
    cursor: pointer;
  }
}

.content-container {
  max-width: 100%;
  overflow: auto;
}

.top-three .rank-badge {
  background-color: #fff3e0;
}

/* 新闻详情弹窗内容样式 */
.news-detail-content {
  line-height: 1.6;
  color: #333;
  font-size: 14px;

  img {
    max-width: 100%;
    height: auto;
    display: block;
    margin: 10px auto;
  }
}
</style>
