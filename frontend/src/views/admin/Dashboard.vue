<script setup>
/**
 * 数据看板：基础统计（用户/非遗/专题/评论）+ 运营指标（答题人次/累计积分/传承人/冷知识）
 *
 * 基础统计复用各分页接口的 total（pageSize=1 开销最小）；
 * 运营指标走 /api/admin/dashboard/* 专用统计接口。
 * 每项独立容错：某个统计接口异常时该卡片显示 0，不影响其余卡片。
 */
import { ref, onMounted } from 'vue'
import { heritageApi, courseApi, userApi, commentApi, dashboardApi } from '../../api/admin'

const stats = ref([
  { title: '用户总数', value: 0, icon: 'User', color: '#409eff' },
  { title: '非遗项目', value: 0, icon: 'Collection', color: '#c0392b' },
  { title: '科普专题', value: 0, icon: 'VideoPlay', color: '#67c23a' },
  { title: '评论总数', value: 0, icon: 'ChatDotRound', color: '#e6a23c' },
  { title: '答题人次', value: 0, icon: 'EditPen', color: '#8e44ad' },
  { title: '累计积分', value: 0, icon: 'Trophy', color: '#d4a017' },
  { title: '传承人数量', value: 0, icon: 'Avatar', color: '#16a085' },
  { title: '冷知识总数', value: 0, icon: 'Lightbulb', color: '#e67e22' }
])

/** 取分页接口的 total，失败返回 0 */
const pageTotal = (promise) =>
  promise.then((res) => Number(res?.data?.total ?? 0)).catch(() => 0)

/** 取统计接口的数值，失败返回 0 */
const statValue = (promise) =>
  promise.then((res) => Number(res?.data ?? 0)).catch(() => 0)

onMounted(async () => {
  const values = await Promise.all([
    pageTotal(userApi.page({ page: 1, pageSize: 1 })),
    pageTotal(heritageApi.page({ page: 1, pageSize: 1 })),
    pageTotal(courseApi.page({ page: 1, pageSize: 1 })),
    pageTotal(commentApi.page({ page: 1, pageSize: 1 })),
    statValue(dashboardApi.getQuizParticipation()),
    statValue(dashboardApi.getTotalScore()),
    statValue(dashboardApi.getInheritorCount()),
    statValue(dashboardApi.getTipCount())
  ])
  values.forEach((value, index) => {
    stats.value[index].value = value
  })
})
</script>

<template>
  <div>
    <el-row :gutter="16">
      <el-col v-for="item in stats" :key="item.title" :xs="12" :sm="12" :md="6">
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
      <p class="tip">
        左侧菜单可进入各管理模块：分类、非遗项目、科普专题与内容、传承人、题库、冷知识、科普快讯、用户、评论、轮播图、公告。
      </p>
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
  line-height: 1.7;
}
</style>
