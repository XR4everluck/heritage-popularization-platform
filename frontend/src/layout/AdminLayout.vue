<script setup>
/**
 * 后台布局：左侧菜单 + 顶部栏（管理员信息/退出）+ 主内容区
 */
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '../store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

async function handleLogout() {
  await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
  userStore.logout()
  router.push('/admin/login')
}
</script>

<template>
  <el-container class="admin-layout">
    <!-- 左侧菜单 -->
    <el-aside width="210px" class="aside">
      <div class="aside-logo">
        <span class="logo-seal aside-seal"><el-icon :size="18"><Collection /></el-icon></span>
        <span>非遗平台后台</span>
      </div>
      <el-menu :default-active="activeMenu" router background-color="#2c3e50" text-color="#c8c9cc"
               active-text-color="#ffffff" class="aside-menu">
        <el-menu-item index="/admin/dashboard"><el-icon><DataAnalysis /></el-icon>数据统计</el-menu-item>
        <el-menu-item index="/admin/category"><el-icon><Menu /></el-icon>分类管理</el-menu-item>
        <el-menu-item index="/admin/heritage"><el-icon><Collection /></el-icon>非遗项目管理</el-menu-item>
        <el-menu-item index="/admin/course"><el-icon><VideoPlay /></el-icon>课程管理</el-menu-item>
        <el-menu-item index="/admin/user"><el-icon><User /></el-icon>用户管理</el-menu-item>
        <el-menu-item index="/admin/comment"><el-icon><ChatDotRound /></el-icon>评论管理</el-menu-item>
        <el-menu-item index="/admin/banner"><el-icon><Picture /></el-icon>轮播图管理</el-menu-item>
        <el-menu-item index="/admin/notice"><el-icon><Bell /></el-icon>公告管理</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部栏 -->
      <el-header class="topbar">
        <span class="page-title">{{ route.meta.title }}</span>
        <div class="topbar-right">
          <el-button text @click="router.push('/')"><el-icon><House /></el-icon>&nbsp;返回前台</el-button>
          <el-dropdown>
            <span class="admin-name">
              <el-avatar :size="30" :src="userStore.user?.avatar">{{ userStore.user?.nickname?.charAt(0) }}</el-avatar>
              {{ userStore.user?.nickname }}（管理员）
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <!-- 主内容区 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
}
.aside {
  background: var(--gq-secondary);
}
.aside-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-family: var(--font-heading);
  font-size: 16px;
  font-weight: 700;
}
.aside-seal {
  width: 30px;
  height: 30px;
}
.aside-menu {
  border-right: none;
}
.topbar {
  background: var(--gq-card);
  border-bottom: 1px solid var(--gq-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.page-title {
  font-family: var(--font-heading);
  font-size: 17px;
  font-weight: 700;
  color: var(--gq-text);
}
.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.admin-name {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.content {
  background: var(--gq-bg);
  overflow-y: auto;
}
</style>
