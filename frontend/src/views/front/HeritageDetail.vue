<script setup>
/**
 * 非遗详情页（杂志式两栏版式）：
 * 左栏 70% 大图 + 标题 + 富文本 + 历史时间轴；右栏 30% 悬浮信息卡 + 相关课程 + 收藏；
 * 评论区与"猜你喜欢"通栏置于两栏之下
 */
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHeritageDetail, getHeritageHistory, addHeritageView, getCourseListByHeritage, getCourseChapters, getCommentPage, postComment,
         getMyCollections, addCollection, removeCollection, getRecommend } from '../../api/front'
import { useUserStore } from '../../store/user'
import { defaultCover, coverFallback } from '../../utils/placeholder'
import CourseCard from '../../components/CourseCard.vue'
import HeritageCard from '../../components/HeritageCard.vue'
import HistoryTimeline from '../../components/HistoryTimeline.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const info = ref(null)
const courses = ref([])
// 历史发展节点（时间轴数据）
const historyNodes = ref([])
// 课程章节视频统计：{ [课程id]: { total, videos } }，用于区分有视频/无视频章节
const courseStats = ref({})
// 猜你喜欢：同分类随机推荐（排除当前项目）
const recommendList = ref([])
const comments = ref([])
const commentTotal = ref(0)
const commentPage = ref(1)
const commentContent = ref('')
const posting = ref(false)
const collected = ref(false)
const collecting = ref(false)
const notFound = ref(false)

const levelTagType = computed(() => (info.value?.level === '国家级' ? 'danger' : 'warning'))

onMounted(async () => {
  let detailRes
  try {
    detailRes = await getHeritageDetail(route.params.id)
  } catch (e) {
    // 项目不存在/已删除/未发布：展示友好空状态
    notFound.value = true
    return
  }
  info.value = detailRes.data
  // 浏览量自增（不阻塞页面）
  addHeritageView(route.params.id).catch(() => {})
  // 历史发展节点（失败不阻塞主内容）
  getHeritageHistory(route.params.id).then((res) => { historyNodes.value = res.data }).catch(() => {})
  // 关联课程 + 各课程章节的视频统计
  const courseRes = await getCourseListByHeritage(route.params.id)
  courses.value = courseRes.data
  loadCourseStats()
  loadRecommend()
  loadComments()
  loadCollectState()
})

/** 濒危程度标签配色 */
const endangerTagType = computed(() => {
  const map = { '濒危': 'danger', '急需保护': 'danger', '脆弱': 'warning', '状况良好': 'success' }
  return map[info.value?.endangerLevel] || 'info'
})

/** 代表作品拆分为标签墙（支持 、 ， ; ; 分隔） */
const workTags = computed(() => (info.value?.representativeWorks || '')
  .split(/[、，,;；]/).map((s) => s.trim()).filter(Boolean))

/** 猜你喜欢：同分类随机推荐 4 个项目（排除当前项目），失败静默 */
async function loadRecommend() {
  try {
    const res = await getRecommend({ categoryId: info.value.categoryId, excludeId: info.value.id, limit: 4 })
    recommendList.value = res.data
  } catch {
    /* 推荐加载失败不影响主内容 */
  }
}

/** 并发拉取各课程的章节列表，统计视频章节数（单课程失败不阻塞） */
async function loadCourseStats() {
  const results = await Promise.all(courses.value.map((course) =>
    getCourseChapters(course.id)
      .then((res) => ({ id: course.id, chapters: res.data }))
      .catch(() => ({ id: course.id, chapters: [] }))
  ))
  const stats = {}
  results.forEach(({ id, chapters }) => {
    stats[id] = { total: chapters.length, videos: chapters.filter((c) => c.videoUrl).length }
  })
  courseStats.value = stats
}

/** 课程视频标识文案：全部有视频 / 部分有视频 / 暂无视频 */
function videoLabel(courseId) {
  const stat = courseStats.value[courseId]
  if (!stat || !stat.total) return null
  if (stat.videos === 0) return { text: `图文课程 · ${stat.total} 章`, type: 'info' }
  if (stat.videos === stat.total) return { text: `视频教学 · ${stat.total} 章`, type: 'success' }
  return { text: `视频 ${stat.videos}/${stat.total} 章`, type: 'warning' }
}

async function loadComments() {
  const res = await getCommentPage({ heritageId: route.params.id, page: commentPage.value, pageSize: 10 })
  comments.value = res.data.records
  commentTotal.value = Number(res.data.total)
}

/** 查询我的收藏，判断当前项目是否已收藏 */
async function loadCollectState() {
  if (!userStore.isLogin) return
  const res = await getMyCollections()
  collected.value = res.data.some((item) => item.heritageId === Number(route.params.id))
}

async function toggleCollect() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    return router.push({ path: '/login', query: { redirect: route.fullPath } })
  }
  collecting.value = true
  try {
    if (collected.value) {
      await removeCollection(route.params.id)
      collected.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addCollection(route.params.id)
      collected.value = true
      ElMessage.success('收藏成功')
    }
  } finally {
    collecting.value = false
  }
}

async function submitComment() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录后再评论')
    return router.push({ path: '/login', query: { redirect: route.fullPath } })
  }
  if (!commentContent.value.trim()) {
    return ElMessage.warning('请输入评论内容')
  }
  posting.value = true
  try {
    await postComment({ heritageId: Number(route.params.id), content: commentContent.value })
    commentContent.value = ''
    commentPage.value = 1
    await loadComments()
    ElMessage.success('评论发布成功')
  } finally {
    posting.value = false
  }
}
</script>

<template>
  <!-- 项目不存在/已被删除时的友好空状态 -->
  <el-card v-if="notFound" shadow="never" class="notfound-card">
    <el-empty description="该项目不存在或已被删除">
      <el-button type="danger" @click="router.push('/heritage')">去逛逛其他非遗项目</el-button>
    </el-empty>
  </el-card>

  <div v-else-if="info" class="detail-page">
    <!-- 杂志式两栏：左 70% 主内容 + 右 30% 悬浮信息栏 -->
    <div class="magazine">
      <!-- 左栏 -->
      <div class="magazine-main">
        <!-- 大图封面 -->
        <div class="hero-wrap">
          <img :src="info.coverImage || defaultCover(info.name)" class="hero-cover"
               @error="coverFallback($event, info.name)" />
          <div class="hero-badge">{{ info.level }}</div>
        </div>

        <!-- 标题与标签栏 -->
        <h2 class="magazine-title">
          {{ info.name }}
          <el-tag :type="levelTagType" effect="dark">{{ info.level }}</el-tag>
          <el-tag type="info" effect="plain">{{ info.categoryName }}</el-tag>
        </h2>
        <div class="magazine-meta">
          <span><el-icon><View /></el-icon> 浏览 {{ info.viewCount }}</span>
          <span><el-icon><Star /></el-icon> 收藏 {{ info.collectionCount }}</span>
          <span><el-icon><Clock /></el-icon> 发布于 {{ (info.publishTime || '').slice(0, 10) }}</span>
        </div>
        <p class="magazine-summary">{{ info.summary }}</p>

        <!-- 详细富文本 -->
        <el-card shadow="never" class="block-card">
          <template #header><b>详细介绍</b></template>
          <div class="rich-content" v-html="info.content || '暂无详细介绍'"></div>
        </el-card>

        <!-- 历史发展时间轴 -->
        <el-card shadow="never" class="block-card">
          <template #header><b>历史沿革</b></template>
          <HistoryTimeline :nodes="historyNodes" />
        </el-card>
      </div>

      <!-- 右栏：悬浮信息卡 -->
      <aside class="magazine-side">
        <el-card shadow="never" class="side-card info-card">
          <div class="info-row">
            <span class="info-label"><el-icon><Clock /></el-icon> 起源年代</span>
            <span class="info-value">{{ info.originAge || '待补充' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label"><el-icon><Location /></el-icon> 分布地区</span>
            <span class="info-value">{{ info.distributionArea || info.region || '待补充' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label"><el-icon><User /></el-icon> 代表传承人</span>
            <span class="info-value">{{ info.inheritor || '群体传承' }}</span>
          </div>
          <div class="info-block">
            <span class="info-label"><el-icon><Trophy /></el-icon> 代表作品</span>
            <div class="work-tags" v-if="workTags.length">
              <el-tag v-for="work in workTags" :key="work" size="small" effect="plain" class="work-tag">{{ work }}</el-tag>
            </div>
            <span v-else class="info-value">待补充</span>
          </div>
          <div class="info-block">
            <span class="info-label"><el-icon><Warning /></el-icon> 濒危程度</span>
            <div class="endanger-wall">
              <el-tag :type="endangerTagType" effect="dark" size="large">{{ info.endangerLevel || '暂无评估' }}</el-tag>
            </div>
          </div>
          <el-button type="danger" :plain="collected" :loading="collecting" class="collect-btn"
                     @click="toggleCollect">
            <el-icon><StarFilled /></el-icon>&nbsp;{{ collected ? '取消收藏' : '收藏本项目' }}
          </el-button>
        </el-card>

        <!-- 相关课程（紧凑列表） -->
        <el-card shadow="never" class="side-card">
          <template #header><b>相关课程（{{ courses.length }}）</b></template>
          <el-empty v-if="!courses.length" description="暂无课程" :image-size="50" />
          <div v-for="course in courses" :key="course.id" class="side-course"
               @click="router.push(`/course/${course.id}`)">
            <img :src="course.cover || defaultCover(course.name, 160, 100)" class="side-course-cover" />
            <div class="side-course-info">
              <div class="side-course-name">{{ course.name }}</div>
              <div class="side-course-meta">
                <el-tag v-if="videoLabel(course.id)" size="small" :type="videoLabel(course.id).type" effect="light">
                  {{ videoLabel(course.id).text }}
                </el-tag>
                <span>{{ course.duration }} 分钟</span>
              </div>
            </div>
          </div>
        </el-card>
      </aside>
    </div>

    <!-- 猜你喜欢：同分类随机推荐（通栏） -->
    <el-card shadow="never" class="block-card" v-if="recommendList.length">
      <template #header><b>猜你喜欢</b></template>
      <el-row :gutter="16">
        <el-col v-for="item in recommendList" :key="item.id" :span="6">
          <HeritageCard :item="{ ...item, categoryName: info.categoryName }"
                        @open="router.push(`/heritage/${item.id}`)" />
        </el-col>
      </el-row>
    </el-card>

    <!-- 评论区（通栏，保留原有功能） -->
    <el-card shadow="never" class="block-card">
      <template #header><b>评论（{{ commentTotal }}）</b></template>
      <div class="comment-editor">
        <el-input v-model="commentContent" type="textarea" :rows="3" maxlength="1000" show-word-limit
                  :placeholder="userStore.isLogin ? '分享你的看法...' : '登录后即可评论'" />
        <el-button type="danger" :loading="posting" @click="submitComment">发表评论</el-button>
      </div>
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <el-avatar :size="36" :src="comment.avatar">{{ comment.nickname?.charAt(0) }}</el-avatar>
        <div class="comment-body">
          <div class="comment-head">
            <b>{{ comment.nickname }}</b>
            <span class="comment-time">{{ comment.createTime }}</span>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
        </div>
      </div>
      <el-empty v-if="!comments.length" description="还没有评论，来抢沙发吧" :image-size="60" />
      <div class="pager" v-if="commentTotal > 10">
        <el-pagination v-model:current-page="commentPage" :page-size="10" :total="commentTotal"
                       layout="prev, pager, next" background @current-change="loadComments" />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.notfound-card {
  margin-bottom: 16px;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.head-card {
  margin-bottom: 16px;
}

/* ---------- 杂志式两栏布局 ---------- */
.magazine {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}
.magazine-main {
  flex: 7;
  min-width: 0;
}
/* 大图封面：通栏主视觉 + 左下角级别印章角标 */
.hero-wrap {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(44, 62, 80, 0.12);
}
.hero-cover {
  width: 100%;
  height: 320px;
  object-fit: cover;
  display: block;
}
.hero-badge {
  position: absolute;
  left: 0;
  bottom: 0;
  background: rgba(192, 57, 43, 0.92);
  color: #fff;
  font-family: var(--font-heading);
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 2px;
  padding: 6px 16px;
  border-radius: 0 12px 0 0;
}
.magazine-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 16px 0 8px;
  font-size: 26px;
}
.magazine-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #8a8578;
  font-size: 13px;
  margin-bottom: 10px;
}
.magazine-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}
.magazine-summary {
  color: var(--gq-text-secondary);
  line-height: 1.8;
  margin-bottom: 14px;
}

/* ---------- 右侧悬浮信息栏 ---------- */
.magazine-side {
  flex: 3;
  min-width: 0;
  position: sticky;
  top: 72px;
  align-self: flex-start;
}
.side-card {
  margin-bottom: 16px;
}
.info-card .info-row,
.info-card .info-block {
  padding: 9px 0;
  border-bottom: 1px dashed var(--gq-border);
}
.info-card .info-row:last-of-type {
  border-bottom: 1px dashed var(--gq-border);
}
.info-label {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #8a8578;
  font-size: 13px;
  margin-bottom: 4px;
}
.info-value {
  color: var(--gq-text);
  font-size: 14px;
  font-weight: 600;
  line-height: 1.5;
  word-break: break-all;
}
.info-block {
  display: block;
}
/* 代表作品标签墙 */
.work-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 4px;
}
.work-tag {
  border-color: var(--gq-border);
  color: var(--gq-secondary);
}
/* 濒危程度标签墙 */
.endanger-wall {
  margin-top: 4px;
}
.collect-btn {
  width: 100%;
  margin-top: 14px;
}
/* 相关课程紧凑列表 */
.side-course {
  display: flex;
  gap: 10px;
  padding: 8px 0;
  cursor: pointer;
  border-bottom: 1px dashed var(--gq-border);
}
.side-course:last-child {
  border-bottom: none;
}
.side-course:hover .side-course-name {
  color: var(--gq-primary);
}
.side-course-cover {
  width: 84px;
  height: 56px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}
.side-course-info {
  flex: 1;
  min-width: 0;
}
.side-course-name {
  font-size: 13px;
  font-weight: 600;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.side-course-meta {
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #c0c4cc;
  font-size: 12px;
}

/* 窄屏回退为单栏 */
@media (max-width: 900px) {
  .magazine {
    flex-direction: column;
  }
  .magazine-side {
    position: static;
    width: 100%;
  }
}
.block-card {
  margin-bottom: 16px;
}
.comment-editor {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  margin-bottom: 18px;
}
.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px dashed #ebeef5;
}
.comment-head {
  display: flex;
  align-items: center;
  gap: 10px;
}
.comment-time {
  color: #c0c4cc;
  font-size: 12px;
}
.comment-content {
  margin-top: 6px;
  line-height: 1.6;
}
.pager {
  display: flex;
  justify-content: center;
  padding-top: 12px;
}
</style>
