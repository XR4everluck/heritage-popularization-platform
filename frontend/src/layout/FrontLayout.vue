<script setup>
/**
 * 前台布局：顶部导航栏（logo、菜单、用户区）+ 主内容区 + 页脚
 * 小屏（<=768px）时水平菜单自动折叠为抽屉导航
 */
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => {
  const path = router.currentRoute.value.path
  // 详情页（/heritage/12、/inheritor/3）也保持对应一级菜单高亮
  if (path.startsWith('/heritage')) return '/heritage'
  if (path.startsWith('/inheritor')) return '/inheritor'
  return path
})
const drawerOpen = ref(false)

/** 抽屉菜单跳转并关闭抽屉 */
function go(path) {
  drawerOpen.value = false
  router.push(path)
}
</script>

<template>
  <el-container class="front-layout">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-inner">
        <div class="logo" @click="router.push('/')">
          <span class="logo-seal"><el-icon :size="22"><Collection /></el-icon></span>
          <span class="logo-text">非遗知识科普平台</span>
        </div>
        <el-menu mode="horizontal" :default-active="activeMenu" :ellipsis="false" router class="nav-menu">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/heritage">非遗博览</el-menu-item>
          <el-menu-item index="/inheritor">传承人专题</el-menu-item>
          <el-menu-item index="/map">非遗地图</el-menu-item>
          <el-menu-item v-if="userStore.isLogin" index="/profile">个人中心</el-menu-item>
        </el-menu>
        <!-- 小屏折叠菜单按钮（<=768px 显示） -->
        <button class="nav-burger" @click="drawerOpen = true">
          <el-icon :size="22"><Fold /></el-icon>
        </button>
        <!-- 折叠抽屉导航 -->
        <el-drawer v-model="drawerOpen" title="导航菜单" size="240px" class="nav-drawer">
          <div class="drawer-menu">
            <div class="drawer-item" :class="{ active: activeMenu === '/' }" @click="go('/')">首页</div>
            <div class="drawer-item" :class="{ active: activeMenu === '/heritage' }" @click="go('/heritage')">非遗博览</div>
            <div class="drawer-item" :class="{ active: activeMenu === '/inheritor' }" @click="go('/inheritor')">传承人专题</div>
            <div class="drawer-item" :class="{ active: activeMenu === '/map' }" @click="go('/map')">非遗地图</div>
            <div v-if="userStore.isLogin" class="drawer-item" :class="{ active: activeMenu === '/profile' }" @click="go('/profile')">个人中心</div>
            <div v-if="userStore.isAdmin" class="drawer-item" @click="go('/admin/dashboard')">进入后台</div>
          </div>
        </el-drawer>
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
    <el-footer class="footer">非遗知识科普平台 · 传承中华优秀传统文化 · 毕业设计作品</el-footer>
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
/* 小屏折叠：隐藏水平菜单，显示汉堡按钮 */
.nav-burger {
  display: none;
  border: none;
  background: transparent;
  color: var(--gq-text);
  cursor: pointer;
  padding: 6px;
}
.drawer-menu {
  display: flex;
  flex-direction: column;
}
.drawer-item {
  padding: 14px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 15px;
  transition: background 0.2s;
}
.drawer-item:hover {
  background: #fdf6ec;
}
.drawer-item.active {
  color: var(--gq-primary);
  font-weight: 700;
  background: var(--gq-primary-light-9);
}
@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  .nav-burger {
    display: flex;
    align-items: center;
    margin-left: auto;
  }
  .user-area {
    margin-left: 8px;
  }
  .logo-text {
    font-size: 16px;
  }
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
