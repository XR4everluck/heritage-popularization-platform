import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 登录用户状态（localStorage 持久化，刷新不丢失）
 */
export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('heritage_token') || '')
  const user = ref(JSON.parse(localStorage.getItem('heritage_user') || 'null'))

  /** 是否已登录 */
  const isLogin = computed(() => !!token.value)
  /** 是否管理员 */
  const isAdmin = computed(() => user.value?.role === 'admin')

  /** 登录成功后保存登录态 */
  function setLogin(tokenValue, userInfo) {
    token.value = tokenValue
    user.value = userInfo
    localStorage.setItem('heritage_token', tokenValue)
    localStorage.setItem('heritage_user', JSON.stringify(userInfo))
  }

  /** 退出登录，清空本地状态 */
  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('heritage_token')
    localStorage.removeItem('heritage_user')
  }

  return { token, user, isLogin, isAdmin, setLogin, logout }
})
