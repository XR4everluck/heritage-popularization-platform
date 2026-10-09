<script setup>
/**
 * 管理员登录页：独立于前台登录，登录成功校验 admin 角色后进入后台
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '../../api/user'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = ref({ username: '', password: '' })
const formRef = ref()

const rules = {
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form.value)
    if (res.data.user.role !== 'admin') {
      ElMessage.error('该账号不是管理员，无权访问后台')
      return
    }
    userStore.setLogin(res.data.token, res.data.user)
    ElMessage.success('欢迎回来，管理员')
    router.push('/admin/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="admin-login-page">
    <div class="login-card">
      <div class="head">
        <el-icon :size="34" color="#c0392b"><DataAnalysis /></el-icon>
        <h2>非遗科普 · 后台管理</h2>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="管理员账号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="密码" :prefix-icon="Lock" />
        </el-form-item>
        <el-button type="danger" class="submit-btn" :loading="loading" @click="handleLogin">登 录 后 台</el-button>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.admin-login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #001529;
}
.login-card {
  width: 380px;
  background: #fff;
  border-radius: 12px;
  padding: 36px 36px 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}
.head {
  text-align: center;
  margin-bottom: 20px;
}
.head h2 {
  margin-top: 8px;
  font-size: 20px;
}
.submit-btn {
  width: 100%;
}
</style>
