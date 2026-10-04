<script setup>
/**
 * 首页：轮播图 + 分类导航 + 推荐非遗 + 最新公告
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBanners, getCategoryList, getHeritagePage, getNoticePage } from '../../api/front'
import { defaultCover, coverFallback } from '../../utils/placeholder'

const router = useRouter()
const banners = ref([])
const categories = ref([])
const heritageList = ref([])
const notices = ref([])
const noticeDetail = ref(null)
const noticeVisible = ref(false)

onMounted(async () => {
  const [b, c, h, n] = await Promise.all([
    getBanners(),
    getCategoryList(),
    getHeritagePage({ page: 1, pageSize: 8 }),
    getNoticePage({ page: 1, pageSize: 5 })
  ])
  banners.value = b.data
  categories.value = c.data
  heritageList.value = h.data.records
  notices.value = n.data.records
})

/** 查看公告详情（弹窗展示） */
async function showNotice(notice) {
  const res = await getNoticeDetail(notice.id)
  noticeDetail.value = res.data
  noticeVisible.value = true
}
</script>

<template>
  <div class="home">
    <!-- 轮播图 -->
    <el-carousel height="360px" class="banner" :interval="4000">
      <el-carousel-item v-for="banner in banners" :key="banner.id">
        <img :src="banner.image" class="banner-img"
             @error="coverFallback($event, banner.title || '非遗之美')"
             @click="banner.linkUrl && router.push(banner.linkUrl)" />
        <div class="banner-title">{{ banner.title }}</div>
      </el-carousel-item>
    </el-carousel>

    <!-- 分类导航 -->
    <div class="section">
      <h3 class="section-title">非遗分类</h3>
      <div class="category-grid">
        <div v-for="cate in categories" :key="cate.id" class="category-item"
             @click="router.push({ path: '/heritage', query: { categoryId: cate.id } })">
          <span class="cate-icon">{{ cate.icon || '🏆' }}</span>
          <span>{{ cate.name }}</span>
        </div>
      </div>
    </div>

    <div class="home-body">
      <!-- 推荐非遗 -->
      <div class="section main-section">
        <div class="section-head">
          <h3 class="section-title">非遗博览</h3>
          <el-link type="danger" @click="router.push('/heritage')">更多 &gt;</el-link>
        </div>
        <el-row :gutter="16">
          <el-col v-for="item in heritageList" :key="item.id" :span="6">
            <el-card shadow="hover" class="heritage-card" :body-style="{ padding: 0 }"
                     @click="router.push(`/heritage/${item.id}`)">
              <img :src="item.coverImage || defaultCover(item.name)" class="card-cover" />
              <div class="card-body">
                <div class="card-name">
                  {{ item.name }}
                  <el-tag size="small" :type="item.level === '国家级' ? 'danger' : 'warning'">{{ item.level }}</el-tag>
                </div>
                <div class="card-meta">
                  <span>{{ item.region }}</span>
                  <span><el-icon><View /></el-icon>{{ item.viewCount }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 最新公告 -->
      <div class="section notice-section">
        <h3 class="section-title">最新公告</h3>
        <el-card shadow="never">
          <div v-for="notice in notices" :key="notice.id" class="notice-item" @click="showNotice(notice)">
            <el-icon color="#c0392b"><Bell /></el-icon>
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-date">{{ (notice.publishTime || '').slice(0, 10) }}</span>
          </div>
          <el-empty v-if="!notices.length" description="暂无公告" :image-size="60" />
        </el-card>
      </div>
    </div>

    <!-- 公告详情弹窗 -->
    <el-dialog v-model="noticeVisible" :title="noticeDetail?.title" width="560px">
      <div class="rich-content" v-html="noticeDetail?.content"></div>
    </el-dialog>
  </div>
</template>

<style scoped>
.banner {
  border-radius: 8px;
  overflow: hidden;
}
.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
}
.banner-title {
  position: absolute;
  left: 24px;
  bottom: 18px;
  color: #fff;
  font-size: 22px;
  font-weight: 600;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.6);
}
.section {
  margin-top: 24px;
}
.section-title {
  margin-bottom: 14px;
  border-left: 4px solid #c0392b;
  padding-left: 10px;
}
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}
.category-item {
  background: #fff;
  border-radius: 8px;
  padding: 16px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.category-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(192, 57, 43, 0.12);
}
.cate-icon {
  font-size: 28px;
}
.home-body {
  display: flex;
  gap: 20px;
}
.main-section {
  flex: 1;
  margin-top: 24px;
}
.notice-section {
  width: 320px;
  flex-shrink: 0;
  margin-top: 24px;
}
.heritage-card {
  margin-bottom: 16px;
  cursor: pointer;
}
.card-cover {
  width: 100%;
  height: 150px;
  object-fit: cover;
  display: block;
}
.card-body {
  padding: 12px;
}
.card-name {
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 4px;
}
.card-meta {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-meta span {
  display: flex;
  align-items: center;
  gap: 2px;
}
.notice-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 0;
  border-bottom: 1px dashed #ebeef5;
  cursor: pointer;
  font-size: 13px;
}
.notice-item:last-of-type {
  border-bottom: none;
}
.notice-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.notice-date {
  color: #c0c4cc;
  font-size: 12px;
}
</style>
