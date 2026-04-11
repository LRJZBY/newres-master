import request from '@/utils/request'

// 查询新闻分类列表
export function listCategories(query) {
  return request({
    url: '/manage/categories/list',
    method: 'get',
    params: query
  })
}

export function listCategories1(query) {
  return request({
    url: '/manage/categories/list1',
    method: 'get',
    params: query
  })
}

// 查询新闻分类详细
export function getCategories(categoryId) {
  return request({
    url: '/manage/categories/' + categoryId,
    method: 'get'
  })
}

// 新增新闻分类
export function addCategories(data) {
  return request({
    url: '/manage/categories',
    method: 'post',
    data: data
  })
}

// 修改新闻分类
export function updateCategories(data) {
  return request({
    url: '/manage/categories',
    method: 'put',
    data: data
  })
}

// 删除新闻分类
export function delCategories(categoryId) {
  return request({
    url: '/manage/categories/' + categoryId,
    method: 'delete'
  })
}
