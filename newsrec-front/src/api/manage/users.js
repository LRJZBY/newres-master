import request from '@/utils/request'

// 查询用户列表
export function listUsers(query) {
  return request({
    url: '/manage/users/list',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUsers(userId) {
  return request({
    url: '/manage/users/' + userId,
    method: 'get'
  })
}

// 新增用户
export function addUsers(data) {
  return request({
    url: '/manage/users',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUsers(data) {
  return request({
    url: '/manage/users',
    method: 'put',
    data: data
  })
}

// 删除用户
export function delUsers(userId) {
  return request({
    url: '/manage/users/' + userId,
    method: 'delete'
  })
}

// 重置用户密码
export function resetUsersPwd(userId){
  return request({
    url: '/manage/users/resetPwd/' +  userId,
    method: 'put'
  })
}