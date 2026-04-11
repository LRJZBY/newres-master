import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

// 添加浏览记录并跳转到新闻详情
export const goToNewsDetail = (newsId, title = '') => {
  const router = useRouter();
  
  // 获取当前登录用户信息
  const userInfo = JSON.parse(localStorage.getItem('user') || '{}');
  const userId = userInfo.userId;
  
  if (userId && newsId) {
    // 添加浏览记录
    const historyData = {
      userId: userId,
      newsId: newsId,
      title: title // 如果有传入标题则使用，否则为空
    };
    
    // 调用添加浏览记录接口
    request.post('/manage/history', historyData)
      .then(response => {
        if (response.code !== 200) {
          console.warn('添加浏览记录失败:', response.msg);
        }
      })
      .catch(error => {
        console.error('添加浏览记录接口调用失败:', error);
      })
      .finally(() => {
        // 无论是否成功添加浏览记录，都跳转到新闻详情页
        router.push(`/newsDetail?newsId=${newsId}`);
      });
  } else {
    // 如果用户未登录或没有新闻ID，直接跳转
    router.push(`/newsDetail?newsId=${newsId}`);
  }
}; 