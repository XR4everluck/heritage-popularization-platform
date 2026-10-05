<script setup>
/**
 * 非遗详情页：基本信息 + 富文本详情 + 关联课程 + 收藏 + 评论区
 */
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHeritageDetail, addHeritageView, getCourseListByHeritage, getCourseChapters, getCommentPage, postComment,
         getMyCollections, addCollection, removeCollection, getRecommend } from '../../api/front'
import { useUserStore } from '../../store/user'
import { defaultCover, coverFallback } from '../../utils/placeholder'
import CourseCard from '../../components/CourseCard.vue'
import HeritageCard from '../../components/HeritageCard.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const info = ref(null)
const courses = ref([])
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
  // 关联课程 + 各课程章节的视频统计
  const courseRes = await getCourseListByHeritage(route.params.id)
  courses.value = courseRes.data
  loadCourseStats()
  loadRecommend()
  loadComments()
  loadCollectState()
})

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
    <!-- 头部信息卡 -->
    <el-card shadow="never" class="head-card">
      <div class="head-body">
        <img :src="info.coverImage || defaultCover(info.name)" class="head-cover" />
        <div class="head-info">
          <h2>
            {{ info.name }}
            <el-tag :type="levelTagType">{{ info.level }}</el-tag>
            <el-tag type="info">{{ info.categoryName }}</el-tag>
          </h2>
          <div class="info-row"><span class="label">所属地区：</span>{{ info.region || '-' }}</div>
          <div class="info-row"><span class="label">代表性传承人：</span>{{ info.inheritor || '-' }}</div>
          <div class="info-row summary"><span class="label">简介：</span>{{ info.summary }}</div>
          <div class="head-meta">
            <span><el-icon><View /></el-icon> 浏览 {{ info.viewCount }}</span>
            <span><el-icon><Star /></el-icon> 收藏 {{ info.collectionCount }}</span>
            <span><el-icon><Clock /></el-icon> 发布于 {{ (info.publishTime || '').slice(0, 10) }}</span>
            <el-button type="danger" :plain="collected" :loading="collecting" @click="toggleCollect">
              <el-icon><StarFilled /></el-icon>&nbsp;{{ collected ? '取消收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 详细介绍 -->
    <el-card shadow="never" class="block-card">
      <template #header><b>详细介绍</b></template>
      <div class="rich-content" v-html="info.content || '暂无详细介绍'"></div>
    </el-card>

    <!-- 关联课程（统一卡片风格，附章节视频统计） -->
    <el-card shadow="never" class="block-card">
      <template #header><b>相关课程（{{ courses.length }}）</b></template>
      <el-empty v-if="!courses.length" description="该项目暂无课程" :image-size="60" />
      <el-row :gutter="16" v-else>
        <el-col v-for="course in courses" :key="course.id" :span="12">
          <CourseCard :item="{ ...course, chapterCount: courseStats[course.id]?.total }"
                      :tag="videoLabel(course.id)"
                      class="related-course" @open="router.push(`/course/${course.id}`)" />
        </el-col>
      </el-row>
    </el-card>

    <!-- 猜你喜欢：同分类随机推荐 -->
    <el-card shadow="never" class="block-card" v-if="recommendList.length">
      <template #header><b>猜你喜欢</b></template>
      <el-row :gutter="16">
        <el-col v-for="item in recommendList" :key="item.id" :span="6">
          <HeritageCard :item="{ ...item, categoryName: info.categoryName }"
                        @open="router.push(`/heritage/${item.id}`)" />
        </el-col>
      </el-row>
    </el-card>

    <!-- 评论区 -->
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
.head-body {
  display: flex;
  gap: 24px;
}
.head-cover {
  width: 360px;
  height: 240px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}
.head-info h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
}
.info-row {
  margin-bottom: 10px;
  line-height: 1.7;
}
.info-row.summary {
  color: #606266;
}
.label {
  color: #909399;
}
.head-meta {
  margin-top: 14px;
  display: flex;
  align-items: center;
  gap: 20px;
  color: #909399;
  font-size: 13px;
}
.head-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}
.block-card {
  margin-bottom: 16px;
}
.related-course {
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
