<script setup>
/**
 * 数据统计首页：用户/非遗/课程/评论总数（复用各分页接口的 total，pageSize=1 最小开销）
 */
import { ref, onMounted } from 'vue'
import { heritageApi, courseApi, userApi, commentApi } from '../../api/admin'

const stats = ref([
  { title: '用户总数', value: 0, icon: 'User', color: '#409eff' },
  { title: '非遗项目', value: 0, icon: 'Collection', color: '#c0392b' },
  { title: '科普专题', value: 0, icon: 'VideoPlay', color: '#67c23a' },
  { title: '评论总数', value: 0, icon: 'ChatDotRound', color: '#e6a23c' }
])

onMounted(async () => {
  const [users, heritages, courses, comments] = await Promise.all([
    userApi.page({ page: 1, pageSize: 1 }),
    heritageApi.page({ page: 1, pageSize: 1 }),
    courseApi.page({ page: 1, pageSize: 1 }),
    commentApi.page({ page: 1, pageSize: 1 })
  ])
  stats.value[0].value = Number(users.data.total)
  stats.value[1].value = Number(heritages.data.total)
  stats.value[2].value = Number(courses.data.total)
  stats.value[3].value = Number(comments.data.total)
})
</script>

<template>
  <div>
    <el-row :gutter="16">
      <el-col v-for="item in stats" :key="item.title" :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <el-icon :size="44" :color="item.color"><component :is="item.icon" /></el-icon>
            <div>
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-title">{{ item.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never" class="welcome-card">
      <h3>欢迎使用非遗知识科普平台管理系统</h3>
      <p class="tip">左侧菜单可进入各管理模块：分类、非遗项目、科普专题与内容、用户、评论、轮播图、公告。</p>
    </el-card>
  </div>
</template>

<style scoped>
.stat-card {
  margin-bottom: 16px;
}
.stat-body {
  display: flex;
  align-items: center;
  gap: 20px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
}
.stat-title {
  color: #909399;
  font-size: 13px;
  margin-top: 4px;
}
.welcome-card .tip {
  color: #909399;
  margin-top: 10px;
  font-size: 13px;
}
</style>
