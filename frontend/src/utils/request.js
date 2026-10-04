import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { useUserStore } from '../store/user'

// axios 实例：统一 baseURL，前后端分离直连后端（后端已开启 CORS）
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 30000
})

// 请求拦截器：自动携带登录 token
request.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

// 响应拦截器：统一处理业务码与异常
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      // 401 未登录/令牌失效：清除登录态并跳转登录页
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        ElMessage.error(res.msg || '登录已过期，请重新登录')
        // 后台页面跳后台登录，前台页面跳前台登录
        if (router.currentRoute.value.path.startsWith('/admin')) {
          router.push('/admin/login')
        } else {
          router.push('/login')
        }
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

export default request
