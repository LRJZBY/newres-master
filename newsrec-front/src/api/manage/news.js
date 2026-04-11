import request from '@/utils/request'

//查询首页数据表（单独的）
export function getData() {
  return request({
    url: '/manage/statistics/dashboard',
    method: 'get'
  })
}


//查询热点新闻列表（进行redis排名榜过后的）
export function getHotNews() {
  return request({
    url: '/manage/hotNews/list',
    method: 'get'
  })
}


// 查询新闻内容列表
export function listNews(query) {
  return request({
    url: '/manage/news/list',
    method: 'get',
    params: query
  })
}

// 查询新闻内容详细
export function getNews(newsId) {
  return request({
    url: '/manage/news/' + newsId,
    method: 'get'
  })
}

// 新增新闻内容
export function addNews(data) {
  return request({
    url: '/manage/news',
    method: 'post',
    data: data
  })
}

// 修改新闻内容
export function updateNews(data) {
  return request({
    url: '/manage/news',
    method: 'put',
    data: data
  })
}

// 删除新闻内容
export function delNews(newsId) {
  return request({
    url: '/manage/news/' + newsId,
    method: 'delete'
  })
}
