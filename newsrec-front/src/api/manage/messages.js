import request from '@/utils/request'

// 查询留言列表
export function listMessages(query) {
  return request({
    url: '/manage/messages/list',
    method: 'get',
    params: query
  })
}

// 查询留言详细
export function getMessages(messageId) {
  return request({
    url: '/manage/messages/' + messageId,
    method: 'get'
  })
}

// 新增留言
export function addMessages(data) {
  return request({
    url: '/manage/messages',
    method: 'post',
    data: data
  })
}

// 修改留言
export function updateMessages(data) {
  return request({
    url: '/manage/messages',
    method: 'put',
    data: data
  })
}

// 删除留言
export function delMessages(messageId) {
  return request({
    url: '/manage/messages/' + messageId,
    method: 'delete'
  })
}
