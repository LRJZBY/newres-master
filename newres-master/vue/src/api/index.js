import request from '@/utils/request'

// 用户相关接口
export const userApi = {
  // 登录
  login: (data) => request.post('/login', data),
  // 注册
  register: (data) => request.post('/register', data),
  // 获取用户信息
  getUserInfo: () => request.get('/user/info')
}

// 新闻相关接口
export const newsApi = {
  // 获取新闻列表
  getNewsList: (params) => request.get('/news/list', { params }),
  // 获取新闻详情
  getNewsDetail: (id) => request.get(`/news/${id}`),
  // 获取新闻分类
  getCategories: () => request.get('/news/categories'),
  // 获取热门新闻
  getHotNews: () => request.get('/news/hot'),
  // 获取系统公告
  getAnnouncements: () => request.get('/announcements')
} 