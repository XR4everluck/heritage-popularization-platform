<script setup>
/**
 * 前台布局：顶部导航栏（logo、菜单、用户区）+ 主内容区 + 页脚
 */
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => router.currentRoute.value.path)
</script>

<template>
  <el-container class="front-layout">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-inner">
        <div class="logo" @click="router.push('/')">
          <span class="logo-seal"><el-icon :size="22"><Collection /></el-icon></span>
          <span class="logo-text">非遗知识教学平台</span>
        </div>
        <el-menu mode="horizontal" :default-active="activeMenu" :ellipsis="false" router class="nav-menu">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/heritage">非遗博览</el-menu-item>
          <el-menu-item v-if="userStore.isLogin" index="/profile">个人中心</el-menu-item>
        </el-menu>
        <div class="user-area">
          <template v-if="userStore.isLogin">
            <el-dropdown>
              <span class="user-name">
                <el-avatar :size="30" :src="userStore.user?.avatar">{{ userStore.user?.nickname?.charAt(0) }}</el-avatar>
                {{ userStore.user?.nickname }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" @click="router.push('/admin/dashboard')">进入后台</el-dropdown-item>
                  <el-dropdown-item divided @click="userStore.logout(); router.push('/')">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button text @click="router.push('/login')">登录</el-button>
            <el-button type="danger" plain @click="router.push('/login?tab=register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-main class="main">
      <router-view />
    </el-main>

    <!-- 页脚 -->
    <el-footer class="footer">非遗知识教学平台 · 传承中华优秀传统文化 · 毕业设计作品</el-footer>
  </el-container>
</template>

<style scoped>
.front-layout {
  min-height: 100vh;
}
.header {
  background: var(--gq-card);
  border-bottom: 1px solid var(--gq-border);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 0 20px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  margin-right: 40px;
}
.logo-text {
  font-family: var(--font-heading);
  font-size: 20px;
  font-weight: 700;
  color: var(--gq-text);
  letter-spacing: 1px;
}
.nav-menu {
  flex: 1;
  border-bottom: none;
}
.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
}
.user-name {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #303133;
  outline: none;
}
.main {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 20px;
}
.footer {
  text-align: center;
  color: var(--gq-text-secondary);
  font-size: 13px;
  height: 60px;
  line-height: 60px;
  border-top: 1px solid var(--gq-border);
  background: var(--gq-card);
}
</style>
