<script setup>
/**
 * 登录/注册页：双 Tab 切换，登录成功按角色跳转
 */
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Postcard } from '@element-plus/icons-vue'
import { login, register } from '../../api/user'
import { useUserStore } from '../../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref(route.query.tab === 'register' ? 'register' : 'login')
const loading = ref(false)

const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', password: '', confirmPassword: '', nickname: '' })
const loginFormRef = ref()
const registerFormRef = ref()

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_]{3,50}$/, message: '3-50位字母、数字或下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度需在6-100位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        value !== registerForm.value.password ? callback(new Error('两次输入的密码不一致')) : callback()
      },
      trigger: 'blur'
    }
  ]
}

/** 登录：成功后按跳转来源返回（后台管理员登录也会进入此页时同样处理） */
async function handleLogin() {
  await loginFormRef.value.validate()
  loading.value = true
  try {
    const res = await login(loginForm.value)
    userStore.setLogin(res.data.token, res.data.user)
    ElMessage.success('登录成功')
    if (route.query.redirect) {
      router.push(route.query.redirect)
    } else if (res.data.user.role === 'admin' && route.path.startsWith('/admin')) {
      router.push('/admin/dashboard')
    } else {
      router.push('/')
    }
  } finally {
    loading.value = false
  }
}

/** 注册成功后自动切到登录 Tab 并预填用户名 */
async function handleRegister() {
  await registerFormRef.value.validate()
  loading.value = true
  try {
    await register({
      username: registerForm.value.username,
      password: registerForm.value.password,
      nickname: registerForm.value.nickname
    })
    ElMessage.success('注册成功，请登录')
    loginForm.value.username = registerForm.value.username
    loginForm.value.password = ''
    activeTab.value = 'login'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="card-head">
        <el-icon :size="30" color="#c0392b"><Collection /></el-icon>
        <h2>非遗知识教学平台</h2>
        <p>登录后可收藏、评论和学习课程</p>
      </div>
      <el-tabs v-model="activeTab" stretch>
        <!-- 登录 -->
        <el-tab-pane label="登 录" name="login">
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large" @keyup.enter="handleLogin">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="用户名" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" show-password placeholder="密码" :prefix-icon="Lock" />
            </el-form-item>
            <el-button type="danger" class="submit-btn" :loading="loading" @click="handleLogin">登 录</el-button>
          </el-form>
        </el-tab-pane>
        <!-- 注册 -->
        <el-tab-pane label="注 册" name="register">
          <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" size="large">
            <el-form-item prop="username">
              <el-input v-model="registerForm.username" placeholder="用户名（3-50位字母数字下划线）" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="nickname">
              <el-input v-model="registerForm.nickname" placeholder="昵称（选填）" :prefix-icon="Postcard" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" type="password" show-password placeholder="密码（至少6位）" :prefix-icon="Lock" />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password" show-password placeholder="确认密码" :prefix-icon="Lock" />
            </el-form-item>
            <el-button type="danger" class="submit-btn" :loading="loading" @click="handleRegister">注 册</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fdf6ec, #fde8e0);
  border-radius: 8px;
}
.login-card {
  width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 32px 36px 40px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
}
.card-head {
  text-align: center;
  margin-bottom: 12px;
}
.card-head h2 {
  margin: 6px 0;
  font-size: 22px;
}
.card-head p {
  color: #909399;
  font-size: 13px;
  margin-bottom: 8px;
}
.submit-btn {
  width: 100%;
  margin-top: 4px;
}
</style>
