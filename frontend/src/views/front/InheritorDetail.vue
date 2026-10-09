<template>
  <div class="inheritor-detail-page">
    <el-page-header :icon="ArrowLeft" :title="pageTitle" @back="goBack" />
    
    <div class="inheritor-header">
      <div class="inheritor-avatar">
        <img :src="inheritor.avatar || 'https://picsum.photos/seed/inheritor/300/300.jpg'" :alt="inheritor.name">
      </div>
      <div class="inheritor-info">
        <h1 class="inheritor-name">{{ inheritor.name }}</h1>
        <p class="inheritor-title">{{ inheritor.title }}</p>
        <div class="inheritor-tags">
          <el-tag v-for="tag in inheritor.tags" :key="tag" size="small" effect="dark">{{ tag }}</el-tag>
        </div>
        <p class="inheritor-bio">{{ inheritor.introduction || '暂无介绍' }}</p>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="人物介绍" name="intro">
        <div class="inheritor-content">
          <h3>个人简介</h3>
          <p>{{ inheritor.introduction || '暂无详细介绍' }}</p>
          
          <h3>传承经历</h3>
          <p>{{ inheritor.experience || '暂无传承经历信息' }}</p>
          
          <h3>荣誉成就</h3>
          <p>{{ inheritor.achievements || '暂无荣誉成就信息' }}</p>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="代表作品" name="works">
        <div class="inheritor-content">
          <el-empty v-if="!inheritor.works.length" description="暂无代表作品">
            <el-button type="danger" @click="goBack">返回传承人列表</el-button>
          </el-empty>
          <div v-for="work in inheritor.works" :key="work.id" class="work-item">
            <div class="work-image">
              <img :src="work.image || 'https://picsum.photos/seed/work/200/150.jpg'" :alt="work.title">
            </div>
            <div class="work-info">
              <h4 class="work-title">{{ work.title }}</h4>
              <p class="work-description">{{ work.description || '暂无描述' }}</p>
              <div class="work-tags">
                <el-tag v-for="tag in work.tags" :key="tag" size="small" effect="dark">{{ tag }}</el-tag>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="关联非遗项目" name="heritage">
        <div class="inheritor-content">
          <el-empty v-if="!inheritor.heritageList.length" description="暂无关联非遗项目">
            <el-button type="danger" @click="goBack">返回传承人列表</el-button>
          </el-empty>
          <div v-for="heritage in inheritor.heritageList" :key="heritage.id" class="heritage-item" @click="goHeritageDetail(heritage.id)">
            <div class="heritage-image">
              <img :src="heritage.coverImage || 'https://picsum.photos/seed/heritage/200/150.jpg'" :alt="heritage.name">
            </div>
            <div class="heritage-info">
              <h4 class="heritage-name">{{ heritage.name }}</h4>
              <p class="heritage-description">{{ heritage.description || '暂无描述' }}</p>
              <div class="heritage-meta">
                <span class="heritage-region">{{ heritage.region }}</span>
                <span class="heritage-level">{{ heritage.level }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="相关科普视频" name="videos">
        <div class="inheritor-content">
          <el-empty v-if="!inheritor.videos.length" description="暂无相关科普视频">
            <el-button type="danger" @click="goBack">返回传承人列表</el-button>
          </el-empty>
          <div v-for="video in inheritor.videos" :key="video.id" class="video-item" @click="goCourseDetail(video.id)">
            <div class="video-image">
              <img :src="video.coverImage || 'https://picsum.photos/seed/video/200/150.jpg'" :alt="video.title">
            </div>
            <div class="video-info">
              <h4 class="video-title">{{ video.title }}</h4>
              <p class="video-description">{{ video.description || '暂无描述' }}</p>
              <div class="video-meta">
                <span class="video-duration">{{ video.duration }} 分钟</span>
                <span class="video-views">{{ video.viewCount }} 次观看</span>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getInheritorDetail } from '../api/front'

const router = useRouter()
const inheritor = ref({})
const activeTab = ref('intro')

const pageTitle = '传承人详情'

onMounted(() => {
  const inheritorId = router.currentRoute.value.params.id
  loadInheritorDetail(inheritorId)
})

async function loadInheritorDetail(id) {
  try {
    const res = await getInheritorDetail(id)
    inheritor.value = res.data || {}
  } catch (error) {
    console.error('加载传承人详情失败:', error)
    ElMessage.error('加载传承人详情失败')
    router.push('/inheritor')
  }
}

function goBack() {
  router.push('/inheritor')
}

function goHeritageDetail(id) {
  router.push(`/heritage/${id}`)
}

function goCourseDetail(id) {
  router.push(`/course/${id}`)
}
</script>

<style scoped>
.inheritor-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.inheritor-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.inheritor-avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 30px;
  flex-shrink: 0;
}

.inheritor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.inheritor-info {
  flex: 1;
}

.inheritor-name {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 10px;
  color: var(--gq-primary);
}

.inheritor-title {
  font-size: 18px;
  color: #666;
  margin-bottom: 15px;
}

.inheritor-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.inheritor-bio {
  font-size: 16px;
  line-height: 1.6;
  color: #909399;
}

.inheritor-content {
  padding: 20px;
}

.inheritor-content h3 {
  font-size: 20px;
  margin-bottom: 15px;
  color: var(--gq-primary);
}

.inheritor-content p {
  font-size: 16px;
  line-height: 1.6;
  color: #666;
  margin-bottom: 20px;
}

.work-item, .heritage-item, .video-item {
  display: flex;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid var(--gq-border);
  cursor: pointer;
  transition: all 0.3s;
}

.work-item:hover, .heritage-item:hover, .video-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  border-color: var(--gq-primary);
}

.work-image, .heritage-image, .video-image {
  width: 150px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 20px;
  flex-shrink: 0;
}

.work-image img, .heritage-image img, .video-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.work-info, .heritage-info, .video-info {
  flex: 1;
}

.work-title, .heritage-name, .video-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--gq-primary);
}

.work-description, .heritage-description, .video-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
  line-height: 1.5;
}

.work-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.work-meta, .heritage-meta, .video-meta {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.work-meta span, .heritage-meta span, .video-meta span {
  font-size: 12px;
  color: #909399;
}

.video-duration, .video-views {
  display: flex;
  align-items: center;
  gap: 5px;
}
</style>