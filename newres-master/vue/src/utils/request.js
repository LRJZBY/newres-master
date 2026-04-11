import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 获取token
    const token = localStorage.getItem('token')
    // 如果存在token，并且请求的不是登录和注册接口，则在请求头中添加Authorization
    if (token && !config.url.includes('/login') && !config.url.includes('/register')) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.msg || '请求失败')
      // token失效
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('sessionDismissed')
        router.push('/login')
      }
      return Promise.reject(res)
    }
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
