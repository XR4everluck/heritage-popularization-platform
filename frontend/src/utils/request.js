import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { useUserStore } from '../store/user'
import { mockRequest } from '../mock'

// mock 开关（docs/11 第 6.2 节）：VITE_USE_MOCK=true 时全部接口走本地 mock，
// URL 结构与真实 SpringBoot 接口完全一致，关闭开关即可直连后端，页面零改动。
const USE_MOCK = String(import.meta.env.VITE_USE_MOCK || '').toLowerCase() === 'true'

// 业务码统一处理（mock 与 axios 两条通道共用）：401 清登录态跳登录页
function handleUnauthorized(msg) {
  const userStore = useUserStore()
  userStore.logout()
  ElMessage.error(msg || '登录已过期，请重新登录')
  if (router.currentRoute.value.path.startsWith('/admin')) {
    router.push('/admin/login')
  } else {
    router.push('/login')
  }
}

let instance

if (USE_MOCK) {
  // mock 通道：方法签名与 axios 实例一致，内部走 mock 路由表
  instance = {
    get: (url, config) => mockRequest({ url, method: 'get', params: config?.params }),
    post: (url, data, config) => mockRequest({ url, method: 'post', data, params: config?.params }),
    put: (url, data, config) => mockRequest({ url, method: 'put', data, params: config?.params }),
    delete: (url, config) => mockRequest({ url, method: 'delete', params: config?.params })
  }
} else {
  // axios 实例：统一 baseURL，前后端分离直连后端（后端已开启 CORS）
  instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    timeout: 30000
  })

  // 请求拦截器：自动携带登录 token
  instance.interceptors.request.use((config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  })

  // 响应拦截器：统一处理业务码与异常
  instance.interceptors.response.use(
    (response) => {
      const res = response.data
      if (res.code !== 200) {
        // 401 未登录/令牌失效：清除登录态并跳转登录页
        if (res.code === 401) {
          handleUnauthorized(res.msg)
        } else {
          ElMessage.error(res.msg || '操作失败')
        }
        return Promise.reject(new Error(res.msg || 'Error'))
      }
      return res
    },
    (error) => {
      ElMessage.error(error.message || '网络异常，请稍后重试')
      return Promise.reject(error)
    }
  )
}

export default instance
