import request from '@/utils/request'

//点击按钮，手动刷新轮播图(根据当前的热榜新闻)
export function refreshBanner() {
  return request({
    url: '/manage/banner/refresh',
    method: 'get'
  })
}



// 查询轮播图列表
export function listBanner(query) {
  return request({
    url: '/manage/banner/list',
    method: 'get',
    params: query
  })
}

// 查询轮播图详细
export function getBanner(bannerId) {
  return request({
    url: '/manage/banner/' + bannerId,
    method: 'get'
  })
}

// 新增轮播图
export function addBanner(data) {
  return request({
    url: '/manage/banner',
    method: 'post',
    data: data
  })
}

// 修改轮播图
export function updateBanner(data) {
  return request({
    url: '/manage/banner',
    method: 'put',
    data: data
  })
}

// 删除轮播图
export function delBanner(bannerId) {
  return request({
    url: '/manage/banner/' + bannerId,
    method: 'delete'
  })
}
