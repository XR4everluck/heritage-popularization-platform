<script setup>
/**
 * 首页（科普杂志风格）：轮播图 + 搜索 + 今日非遗冷知识 + 分类导航 + 热门科普视频(横滚) + 非遗快讯 + 猜你喜欢 + 最新公告
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBanners, getCategoryList, getHeritagePage, getNoticePage, getNoticeDetail,
         getHotCourses, getLatestProgress, getRecommend, getRandomTip } from '../../api/front'
import { useUserStore } from '../../store/user'
import HeritageCard from '../../components/HeritageCard.vue'
import CourseCard from '../../components/CourseCard.vue'
import CateIcon from '../../components/CateIcon.vue'
import BannerCarousel from '../../components/BannerCarousel.vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const banners = ref([])
const categories = ref([])
const hotCourses = ref([])
const heritageList = ref([])
const recommendList = ref([])
const notices = ref([])
const noticeDetail = ref(null)
const noticeVisible = ref(false)
const keyword = ref('')
const latest = ref(null)
const loadFailed = ref(false)

// 今日非遗冷知识
const dailyTip = ref(null)

// 非遗快讯（is_news=1）
const newsList = ref([])

// 热门搜索标签
const HOT_KEYWORDS = ['昆曲', '剪纸', '皮影戏', '二十四节气']

onMounted(loadAll)

/** 首屏数据加载 */
async function loadAll() {
  loading.value = true
  loadFailed.value = false
  try {
    const [b, c, h, n, courses, recommend, news, tip] = await Promise.all([
      getBanners(),
      getCategoryList(),
      getHeritagePage({ page: 1, pageSize: 8 }),
      getNoticePage({ page: 1, pageSize: 5 }),
      getHotCourses(8).catch(() => ({ data: [] })),
      getRecommend({ limit: 4 }).catch(() => ({ data: [] })),
      getHeritagePage({ page: 1, pageSize: 6, isNews: 1 }).catch(() => ({ data: { records: [] } })),
      getRandomTip().catch(() => ({ data: null }))
    ])
    banners.value = b.data
    categories.value = c.data
    hotCourses.value = courses.data
    heritageList.value = h.data.records
    recommendList.value = recommend.data
    notices.value = n.data.records
    newsList.value = news.data.records
    dailyTip.value = tip.data
  } catch (e) {
    loadFailed.value = true
  } finally {
    loading.value = false
  }
  loadLatest()
}

/** 加载最近观看记录（仅登录用户；失败静默） */
async function loadLatest() {
  if (!userStore.isLogin) return
  try {
    const res = await getLatestProgress()
    latest.value = res.data
  } catch {
    /* 未登录或无记录时忽略 */
  }
}

/** 刷新冷知识 */
async function refreshTip() {
  try {
    const res = await getRandomTip()
    dailyTip.value = res.data
  } catch { /* ignore */ }
}

/** 搜索 */
function goSearch(kw) {
  const word = (kw ?? keyword.value ?? '').trim()
  if (!word) return
  router.push({ path: '/heritage', query: { keyword: word } })
}

/** 查看公告详情 */
async function showNotice(notice) {
  const res = await getNoticeDetail(notice.id)
  noticeDetail.value = res.data
  noticeVisible.value = true
}

/** 轮播图点击跳转 */
function goBanner(banner) {
  if (!banner.linkUrl) return
  if (/^https?:\/\//i.test(banner.linkUrl)) {
    window.open(banner.linkUrl, '_blank')
  } else {
    router.push(banner.linkUrl)
  }
}

/** 热门科普视频横向滚动 */
const hotScrollRef = ref(null)
function scrollHot(dir) {
  const el = hotScrollRef.value
  if (!el) return
  el.scrollBy({ left: dir * 320, behavior: 'smooth' })
}
</script>

<template>
  <div class="home">
    <!-- 骨架屏 -->
    <div v-if="loading" class="home-skeleton">
      <el-skeleton animated style="width: 100%">
        <template #template>
          <el-skeleton-item variant="image" style="width: 100%; height: 380px; border-radius: 12px" />
          <div style="display: flex; gap: 16px; margin-top: 24px">
            <el-skeleton-item v-for="i in 4" :key="i" variant="image" style="flex: 1; height: 220px; border-radius: 12px" />
          </div>
        </template>
      </el-skeleton>
    </div>

    <!-- 后端不可用时的失败引导 -->
    <el-card v-else-if="loadFailed" shadow="never" class="fail-card">
      <el-empty description="页面数据加载失败，可能是后端服务或数据库未就绪">
        <el-button type="danger" @click="loadAll">重新加载</el-button>
        <el-button @click="router.push('/heritage')">先去非遗博览</el-button>
      </el-empty>
    </el-card>

    <template v-else>
    <!-- 轮播图 -->
    <BannerCarousel :banners="banners" :open="goBanner" />

    <!-- 搜索框 + 热门搜索 -->
    <div class="search-section">
      <div class="search-box">
        <el-input v-model="keyword" size="large" placeholder="搜索你感兴趣的非遗项目，如：昆曲、剪纸……"
                  clearable @keyup.enter="goSearch()">
          <template #append>
            <el-button type="danger" @click="goSearch()"><el-icon><Search /></el-icon>&nbsp;搜索</el-button>
          </template>
        </el-input>
        <div class="hot-keywords">
          <span class="hot-label">热门搜索：</span>
          <el-tag v-for="kw in HOT_KEYWORDS" :key="kw" class="hot-tag" effect="plain" round
                  @click="goSearch(kw)">{{ kw }}</el-tag>
        </div>
      </div>
    </div>

    <!-- 今日非遗·冷知识 -->
    <div class="section" v-if="dailyTip">
      <div class="tip-card">
        <div class="tip-badge"><el-icon><MagicStick /></el-icon> 今日非遗</div>
        <div class="tip-content">{{ dailyTip.content }}</div>
        <el-button text type="danger" @click="refreshTip"><el-icon><RefreshRight /></el-icon>&nbsp;换一条</el-button>
      </div>
    </div>

    <!-- 接着看 -->
    <div class="section continue-section" v-if="userStore.isLogin && latest">
      <div class="continue-card" @click="router.push(`/course/${latest.courseId}?chapter=${latest.chapterId}`)">
        <span class="continue-badge">接着看</span>
        <el-icon class="continue-icon" :size="26"><VideoPlay /></el-icon>
        <div class="continue-info">
          <div class="continue-course">{{ latest.courseName }}</div>
          <div class="continue-chapter">
            上次看到：{{ latest.chapterTitle }} · 已看 {{ latest.studyDuration }} 分钟
            <el-tag v-if="latest.finished" size="small" type="success">本节已完成</el-tag>
          </div>
        </div>
        <el-button type="danger" plain size="small">接着看&nbsp;<el-icon><ArrowRight /></el-icon></el-button>
      </div>
    </div>

    <!-- 分类导航 -->
    <div class="section">
      <h3 class="section-title">非遗分类</h3>
      <div class="category-grid">
        <div v-for="cate in categories" :key="cate.id" class="category-item"
             @click="router.push({ path: '/heritage', query: { categoryId: cate.id } })">
          <span class="cate-icon"><CateIcon :name="cate.name" /></span>
          <span>{{ cate.name }}</span>
        </div>
      </div>
    </div>

    <!-- 热门科普视频（横向滚动卡片） -->
    <div class="section" v-if="hotCourses.length">
      <div class="section-head">
        <h3 class="section-title">热门科普视频</h3>
        <div class="scroll-arrows">
          <el-button circle size="small" @click="scrollHot(-1)"><el-icon><ArrowLeft /></el-icon></el-button>
          <el-button circle size="small" @click="scrollHot(1)"><el-icon><ArrowRight /></el-icon></el-button>
        </div>
      </div>
      <div class="hot-scroll-wrap">
        <div ref="hotScrollRef" class="hot-scroll">
          <CourseCard v-for="course in hotCourses" :key="course.id" :item="course" class="hot-card"
                      @open="router.push(`/course/${course.id}`)" />
        </div>
      </div>
    </div>

    <!-- 非遗快讯（is_news=1 图文列表） -->
    <div class="section" v-if="newsList.length">
      <div class="section-head">
        <h3 class="section-title">非遗快讯</h3>
        <el-link type="danger" @click="router.push({ path: '/heritage', query: { isNews: 1 } })">更多 &gt;</el-link>
      </div>
      <div class="news-grid">
        <div v-for="item in newsList" :key="item.id" class="news-item"
             @click="router.push(`/heritage/${item.id}`)">
          <img v-if="item.coverImage" :src="item.coverImage" class="news-cover" />
          <div class="news-body">
            <div class="news-tag"><el-tag size="small" type="danger" effect="dark">快讯</el-tag></div>
            <div class="news-name">{{ item.name }}</div>
            <div class="news-summary">{{ item.summary }}</div>
            <div class="news-meta">{{ (item.publishTime || '').slice(0, 10) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 猜你喜欢 -->
    <div class="section" v-if="recommendList.length">
      <h3 class="section-title">猜你喜欢</h3>
      <el-row :gutter="16">
        <el-col v-for="item in recommendList" :key="item.id" :xs="12" :sm="12" :md="8" :lg="6">
          <HeritageCard :item="item" class="grid-card" @open="router.push(`/heritage/${item.id}`)" />
        </el-col>
      </el-row>
    </div>

    <!-- 最新公告 -->
    <div class="section" v-if="notices.length">
      <div class="section-head">
        <h3 class="section-title">最新公告</h3>
      </div>
      <el-card shadow="never">
        <div v-for="notice in notices" :key="notice.id" class="notice-item" @click="showNotice(notice)">
          <el-icon color="#c0392b"><Bell /></el-icon>
          <span class="notice-title">{{ notice.title }}</span>
          <span class="notice-date">{{ (notice.publishTime || '').slice(0, 10) }}</span>
        </div>
      </el-card>
    </div>
    </template>

    <!-- 公告详情弹窗 -->
    <el-dialog v-model="noticeVisible" :title="noticeDetail?.title" width="560px">
      <div class="rich-content" v-html="noticeDetail?.content"></div>
    </el-dialog>
  </div>
</template>

<style scoped>
.fail-card {
  min-height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fail-card :deep(.el-card__body) {
  width: 100%;
}
.section {
  margin-top: 24px;
}
.section-title {
  margin-bottom: 14px;
  border-left: 4px solid var(--gq-primary);
  padding-left: 10px;
}
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.grid-card {
  margin-bottom: 16px;
}

/* ---------- 搜索区 ---------- */
.search-section {
  margin-top: 16px;
}
.search-box {
  background: var(--gq-card);
  border: 1px solid var(--gq-border);
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 2px 10px rgba(44, 62, 80, 0.05);
}
.hot-keywords {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.hot-label {
  color: #8a8578;
  font-size: 13px;
}
.hot-tag {
  cursor: pointer;
  color: var(--gq-primary);
  border-color: var(--gq-border);
}
.hot-tag:hover {
  background: var(--gq-primary);
  border-color: var(--gq-primary);
  color: #fff;
}

/* ---------- 今日非遗冷知识 ---------- */
.tip-card {
  background: linear-gradient(135deg, #fdf6ec 0%, #fde8e0 100%);
  border: 1px solid var(--gq-border);
  border-left: 4px solid var(--gq-gold);
  border-radius: 12px;
  padding: 18px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.tip-badge {
  flex-shrink: 0;
  background: var(--gq-gold);
  color: #fff;
  font-size: 13px;
  font-family: var(--font-heading);
  font-weight: 700;
  padding: 6px 14px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.tip-content {
  flex: 1;
  font-size: 15px;
  line-height: 1.7;
  color: var(--gq-text);
}

/* ---------- 接着看 ---------- */
.continue-card {
  background: var(--gq-card);
  border: 1px solid var(--gq-border);
  border-left: 4px solid var(--gq-primary);
  border-radius: 12px;
  padding: 14px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: box-shadow 0.25s ease, transform 0.25s ease;
}
.continue-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(44, 62, 80, 0.12);
}
.continue-badge {
  background: var(--gq-primary);
  color: #fff;
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 10px;
  flex-shrink: 0;
}
.continue-icon {
  color: var(--gq-primary);
  flex-shrink: 0;
}
.continue-info {
  flex: 1;
  min-width: 0;
}
.continue-course {
  font-weight: 700;
  font-family: var(--font-heading);
}
.continue-chapter {
  margin-top: 3px;
  color: #8a8578;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ---------- 分类导航 ---------- */
.category-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}
.category-item {
  position: relative;
  overflow: hidden;
  background: var(--gq-card);
  border: 1px solid var(--gq-border);
  border-radius: 12px;
  padding: 18px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}
.category-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(192, 57, 43, 0.12);
}
.category-item::after {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(192, 57, 43, 0.10) 0%, rgba(192, 57, 43, 0.04) 45%, transparent 70%);
  transform: translate(-50%, -50%);
  transition: width 0.5s ease, height 0.5s ease;
  pointer-events: none;
}
.category-item:hover::after {
  width: 260%;
  height: 260%;
}
.cate-icon {
  color: var(--gq-primary);
  position: relative;
  z-index: 1;
}
.category-item span:last-child {
  position: relative;
  z-index: 1;
}

/* ---------- 热门科普视频横向滚动 ---------- */
.hot-scroll-wrap {
  position: relative;
}
.hot-scroll {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  padding-bottom: 8px;
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.hot-scroll::-webkit-scrollbar {
  display: none;
}
.hot-card {
  flex: 0 0 260px;
  scroll-snap-align: start;
}
.scroll-arrows {
  display: flex;
  gap: 6px;
}

/* ---------- 非遗快讯 ---------- */
.news-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.news-item {
  background: var(--gq-card);
  border: 1px solid var(--gq-border);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}
.news-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(44, 62, 80, 0.12);
}
.news-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  display: block;
}
.news-body {
  padding: 12px 16px;
}
.news-tag {
  margin-bottom: 6px;
}
.news-name {
  font-weight: 700;
  font-family: var(--font-heading);
  font-size: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.news-summary {
  margin-top: 6px;
  color: var(--gq-text-secondary);
  font-size: 13px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.news-meta {
  margin-top: 8px;
  color: #c0c4cc;
  font-size: 12px;
}
@media (max-width: 900px) {
  .news-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .category-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 640px) {
  .news-grid {
    grid-template-columns: 1fr;
  }
  .category-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .hot-card {
    flex: 0 0 200px;
  }
}

/* ---------- 公告 ---------- */
.notice-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 0;
  border-bottom: 1px dashed var(--gq-border);
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
