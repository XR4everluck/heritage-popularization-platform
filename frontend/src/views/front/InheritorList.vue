<template>
  <div class="inheritor-page">
    <AppBreadcrumb :items="[{ label: '首页', to: '/' }, { label: '传承人专题' }]" />
    <el-page-header :icon="ArrowLeft" :title="pageTitle" @back="goBack" />

    <div class="gq-page-header">
      <h1>传承人专题</h1>
      <p>了解非遗传承人的故事与技艺</p>
    </div>

    <el-card shadow="never" class="inheritor-grid">
      <el-empty v-if="!inheritorList.length" description="暂无传承人信息">
        <el-button type="danger" @click="goBack">返回首页</el-button>
      </el-empty>

      <div v-for="inheritor in inheritorList" :key="inheritor.id" class="inheritor-card" @click="goInheritorDetail(inheritor.id)">
        <div class="inheritor-image">
          <img :src="inheritor.avatar || defaultCover(inheritor.name || '传承人', 200, 200)"
               :alt="`${inheritor.name}肖像，图片用于毕业设计展示`" width="200" height="200">
        </div>
        <div class="inheritor-info">
          <h3 class="inheritor-name">{{ inheritor.name }}</h3>
          <p class="inheritor-title">{{ inheritor.title }}</p>
          <p class="inheritor-bio">{{ inheritor.introduction || '暂无介绍' }}</p>
          <div class="inheritor-tags">
            <el-tag v-if="inheritor.heritageName" size="small" effect="plain">{{ inheritor.heritageName }}</el-tag>
            <el-tag v-for="tag in inheritor.tags" :key="tag" size="small" effect="dark">{{ tag }}</el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 版权备注：头像为视觉中国素材（docs/12 版权约束） -->
    <CopyrightNote />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getInheritorList } from '../../api/front'
import { defaultCover } from '../../utils/placeholder'
import AppBreadcrumb from '../../components/AppBreadcrumb.vue'
import CopyrightNote from '../../components/CopyrightNote.vue'

const router = useRouter()
const inheritorList = ref([])

const pageTitle = '传承人专题'

onMounted(() => {
  loadInheritorList()
})

async function loadInheritorList() {
  try {
    const res = await getInheritorList()
    inheritorList.value = res.data || []
  } catch (error) {
    console.error('加载传承人列表失败:', error)
  }
}

function goBack() {
  router.push('/')
}

function goInheritorDetail(id) {
  router.push(`/inheritor/${id}`)
}
</script>

<style scoped>
.inheritor-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: var(--gq-primary);
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

.inheritor-grid {
  padding: 20px;
}

.inheritor-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  cursor: pointer;
  transition: none;
  background: #fff;
  border: 1px solid var(--gq-border);
}

/* 悬停仅变边框色（docs/11 第 2 节禁令：不做位移/阴影增强） */
.inheritor-card:hover {
  border-color: var(--gq-primary);
}

.inheritor-card:hover .inheritor-name {
  color: var(--gq-primary-dark);
}

.inheritor-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20px;
  flex-shrink: 0;
}

.inheritor-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.inheritor-info {
  flex: 1;
}

.inheritor-name {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--gq-primary);
}

.inheritor-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.inheritor-bio {
  font-size: 14px;
  color: #999;
  margin-bottom: 15px;
  line-height: 1.5;
}

.inheritor-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>