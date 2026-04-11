import request from '@/utils/request'

// 查询浏览历史列表
export function listHistory(query) {
  return request({
    url: '/manage/history/list',
    method: 'get',
    params: query
  })
}

// 查询浏览历史详细
export function getHistory(historyId) {
  return request({
    url: '/manage/history/' + historyId,
    method: 'get'
  })
}

// 新增浏览历史
export function addHistory(data) {
  return request({
    url: '/manage/history',
    method: 'post',
    data: data
  })
}

// 修改浏览历史
export function updateHistory(data) {
  return request({
    url: '/manage/history',
    method: 'put',
    data: data
  })
}

// 删除浏览历史
export function delHistory(historyId) {
  return request({
    url: '/manage/history/' + historyId,
    method: 'delete'
  })
}
