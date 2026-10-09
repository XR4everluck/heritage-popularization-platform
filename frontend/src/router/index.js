import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'

// 路由表：前台路由 + 后台管理路由（后台带登录与角色守卫）
const routes = [
  {
    path: '/',
    component: () => import('../layout/FrontLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('../views/front/Home.vue'), meta: { title: '首页' } },
      { path: 'heritage', name: 'heritageList', component: () => import('../views/front/HeritageList.vue'), meta: { title: '非遗博览' } },
      { path: 'heritage/:id', name: 'heritageDetail', component: () => import('../views/front/HeritageDetail.vue'), meta: { title: '非遗详情' } },
      { path: 'course/:id', name: 'courseDetail', component: () => import('../views/front/CourseDetail.vue'), meta: { title: '科普观看' } },
      { path: 'inheritor', name: 'inheritorList', component: () => import('../views/front/InheritorList.vue'), meta: { title: '传承人专题' } },
      { path: 'inheritor/:id', name: 'inheritorDetail', component: () => import('../views/front/InheritorDetail.vue'), meta: { title: '传承人详情' } },
      { path: 'profile', name: 'profile', component: () => import('../views/front/Profile.vue'), meta: { title: '个人中心', requiresAuth: true } },
      { path: 'login', name: 'login', component: () => import('../views/front/Login.vue'), meta: { title: '登录' } }
    ]
  },
  {
    path: '/admin/login',
    name: 'adminLogin',
    component: () => import('../views/admin/Login.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/admin',
    component: () => import('../layout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAdmin: true },
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '数据统计' } },
      { path: 'category', name: 'categoryManage', component: () => import('../views/admin/CategoryManage.vue'), meta: { title: '分类管理' } },
      { path: 'heritage', name: 'heritageManage', component: () => import('../views/admin/HeritageManage.vue'), meta: { title: '非遗项目管理' } },
      { path: 'course', name: 'courseManage', component: () => import('../views/admin/CourseManage.vue'), meta: { title: '科普专题管理' } },
      { path: 'user', name: 'userManage', component: () => import('../views/admin/UserManage.vue'), meta: { title: '用户管理' } },
      { path: 'comment', name: 'commentManage', component: () => import('../views/admin/CommentManage.vue'), meta: { title: '评论管理' } },
      { path: 'banner', name: 'bannerManage', component: () => import('../views/admin/BannerManage.vue'), meta: { title: '轮播图管理' } },
      { path: 'notice', name: 'noticeManage', component: () => import('../views/admin/NoticeManage.vue'), meta: { title: '公告管理' } },
      { path: 'inheritor', name: 'inheritorManage', component: () => import('../views/admin/InheritorManage.vue'), meta: { title: '传承人管理' } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 全局前置守卫：页面标题 + 登录/角色校验
router.beforeEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} - 非遗知识科普平台` : '非遗知识科普平台'
  const userStore = useUserStore()

  // 个人中心等需登录页面
  if (to.meta.requiresAuth && !userStore.isLogin) {
    ElMessage.warning('请先登录')
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  // 后台路由：需要管理员身份
  if (to.path.startsWith('/admin') && to.path !== '/admin/login') {
    if (!userStore.isLogin) {
      ElMessage.warning('请先登录管理员账号')
      return { path: '/admin/login' }
    }
    if (!userStore.isAdmin) {
      ElMessage.error('该账号不是管理员，无权访问后台')
      return { path: '/' }
    }
  }
  return true
})

export default router
