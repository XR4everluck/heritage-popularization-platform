<script setup>
/**
 * 个人中心：学习数据仪表板 + 我的收藏 / 我的评论 / 我的学习 / 个人资料（Tab 切换）
 */
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyCollections, removeCollection, getMyComments, deleteMyComment,
         getMyProgress, getCourseChapters, getTotalScore,
         submitContent, getMySubmissions, getCategoryList, getHeritagePage } from '../../api/front'
import { getProfile, updateProfile } from '../../api/user'
import { useUserStore } from '../../store/user'
import FileUpload from '../../components/FileUpload.vue'
import { defaultCover } from '../../utils/placeholder'
import { Medal } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 支持 /profile?tab=publish 直达指定 Tab（全民科普页"我要发布"入口）
const activeTab = ref(['collection', 'comment', 'study', 'publish', 'profile'].includes(route.query.tab) ? route.query.tab : 'collection')
  // 我的收藏
  const collections = ref([])
  // 我的评论
  const comments = ref([])
  const commentTotal = ref(0)
  const commentPage = ref(1)
  // 我的学习
  const progressList = ref([])
  // 各课程总章节数：{ [课程id]: 章节数 }，用于计算完成度
  const courseTotals = ref({})
  // 个人资料
  const profileForm = ref({ nickname: '', phone: '', email: '', avatar: '', introduction: '' })
  const saving = ref(false)
  // 我的积分
  const totalScore = ref(0)
  // 内容发布（docs/17：提交后需管理员审核）
  const heritageOptions = ref([])
  const publishing = ref(false)
  const mySubs = ref([])
  const publishForm = ref({ type: 'news', title: '', heritageId: null, content: '', videoUrl: '' })

  onMounted(() => {
    loadCollections()
    loadComments()
    loadProgress()
    loadProfile()
    loadTotalScore()
    loadSubmissions()
    loadHeritageOptions()
  })

  async function loadHeritageOptions() {
    try {
      const res = await getHeritagePage({ page: 1, pageSize: 100 })
      heritageOptions.value = res.data.records || []
    } catch { /* 下拉为空不阻塞 */ }
  }

  async function loadSubmissions() {
    try {
      const res = await getMySubmissions()
      mySubs.value = res.data || []
    } catch { /* 失败保留空列表 */ }
  }

  /** 发布内容：快讯必填正文；视频必填 B 站链接 */
  async function publish() {
    const form = publishForm.value
    if (!form.title.trim()) { ElMessage.warning('请填写标题'); return }
    if (form.type === 'news' && !form.content.trim()) { ElMessage.warning('科普快讯请填写正文'); return }
    if (form.type === 'course' && !form.videoUrl.trim()) { ElMessage.warning('科普视频请填写B站视频链接'); return }
    publishing.value = true
    try {
      await submitContent({ ...form, title: form.title.trim() })
      ElMessage.success('已提交，等待管理员审核通过后展示')
      publishForm.value = { type: 'news', title: '', heritageId: null, content: '', videoUrl: '' }
      loadSubmissions()
    } catch { /* 失败原因拦截器已提示 */ } finally {
      publishing.value = false
    }
  }

  /** 审核状态文案与标签类型 */
  const STATUS_META = {
    0: { text: '待审核', type: 'warning' },
    1: { text: '已通过', type: 'success' },
    2: { text: '未通过', type: 'danger' }
  }

async function loadCollections() {
  const res = await getMyCollections()
  collections.value = res.data
}

async function loadComments() {
  const res = await getMyComments({ page: commentPage.value, pageSize: 10 })
  comments.value = res.data.records
  commentTotal.value = Number(res.data.total)
}

async function loadProgress() {
  const res = await getMyProgress({})
  progressList.value = res.data
  // 并发拉取涉及课程的总章节数（失败按 0 处理）
  const ids = [...new Set(res.data.map((p) => p.courseId))]
  const results = await Promise.all(ids.map((id) =>
    getCourseChapters(id).then((r) => [id, r.data.length]).catch(() => [id, 0])
  ))
  courseTotals.value = Object.fromEntries(results)
}

// ---------- 学习数据仪表板 ----------
const studyStats = computed(() => {
  const list = progressList.value
  const totalMinutes = list.reduce((sum, p) => sum + (p.studyDuration || 0), 0)
  const finishedChapters = list.filter((p) => p.finished).length
  return { totalMinutes, finishedChapters, streakDays: calcStreak(list) }
})

/** 连续学习天数：从今天（或昨天）往前数，学习日期连续的天数 */
function calcStreak(list) {
  const fmt = (d) => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  const dates = [...new Set(list.map((p) => (p.updateTime || '').slice(0, 10)))].sort()
  if (!dates.length) return 0
  const days = new Set(dates)
  let streak = 0
  const cursor = new Date()
  // 今天没学不打断连续（从昨天起算）
  if (!days.has(fmt(cursor))) cursor.setDate(cursor.getDate() - 1)
  while (days.has(fmt(cursor))) {
    streak += 1
    cursor.setDate(cursor.getDate() - 1)
  }
  return streak
}

/** 分钟数格式化为"X 小时 Y 分钟" */
function formatMinutes(minutes) {
  if (!minutes) return '0 分钟'
  if (minutes < 60) return `${minutes} 分钟`
  return `${Math.floor(minutes / 60)} 小时 ${minutes % 60} 分钟`
}

/** 课程完成度百分比 */
function coursePercent(courseId) {
  const total = courseTotals.value[courseId] || 0
  if (!total) return 0
  const finished = progressList.value.filter((p) => p.courseId === courseId && p.finished).length
  return Math.round((finished / total) * 100)
}

/** 按课程聚合的学习记录（我的学习列表展示） */
const courseStudy = computed(() => {
  const map = new Map()
  progressList.value.forEach((p) => {
    if (!map.has(p.courseId)) map.set(p.courseId, { courseId: p.courseId, courseName: p.courseName, records: [] })
    map.get(p.courseId).records.push(p)
  })
  return [...map.values()]
})

async function loadProfile() {
  const res = await getProfile()
  const { nickname, phone, email, avatar, introduction } = res.data || {}
  profileForm.value = { nickname, phone, email, avatar, introduction }
}

async function loadTotalScore() {
  try {
    const res = await getTotalScore()
    totalScore.value = res.data || 0
  } catch (error) {
    console.error('获取积分失败:', error)
    totalScore.value = 0
  }
}

async function cancelCollect(row) {
  await ElMessageBox.confirm(`确定取消收藏《${row.name}》吗？`, '提示', { type: 'warning' })
  await removeCollection(row.heritageId)
  ElMessage.success('已取消收藏')
  loadCollections()
}

async function removeComment(row) {
  await ElMessageBox.confirm('确定删除这条评论吗？', '提示', { type: 'warning' })
  await deleteMyComment(row.id)
  ElMessage.success('已删除')
  loadComments()
}

async function saveProfile() {
  saving.value = true
  try {
    await updateProfile(profileForm.value)
    ElMessage.success('资料已更新')
    // 同步刷新顶部登录态中的昵称/头像
    const res = await getProfile()
    userStore.setLogin(userStore.token, res.data)
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <!-- 学习数据仪表板 -->
  <div class="stat-row">
    <el-card shadow="never" class="stat-card">
      <div class="stat-num" :style="{ color: 'var(--gq-primary)' }">{{ formatMinutes(studyStats.totalMinutes) }}</div>
      <div class="stat-label"><el-icon><Timer /></el-icon> 累计观看时长</div>
    </el-card>
    <el-card shadow="never" class="stat-card">
      <div class="stat-num" :style="{ color: 'var(--gq-gold)' }">{{ studyStats.finishedChapters }}</div>
      <div class="stat-label"><el-icon><CircleCheck /></el-icon> 完成内容数</div>
    </el-card>
    <el-card shadow="never" class="stat-card">
      <div class="stat-num" :style="{ color: 'var(--gq-secondary)' }">{{ studyStats.streakDays }} 天</div>
      <div class="stat-label"><el-icon><Sunny /></el-icon> 连续观看天数</div>
    </el-card>
    <el-card shadow="never" class="stat-card">
      <div class="stat-num" :style="{ color: 'var(--gq-danger)' }">{{ totalScore || 0 }}</div>
      <div class="stat-label"><el-icon><Medal /></el-icon> 我的积分</div>
    </el-card>
  </div>

  <el-card shadow="never" class="profile-card">
    <el-tabs v-model="activeTab">
      <!-- 我的收藏 -->
      <el-tab-pane label="我的收藏" name="collection">
        <el-empty v-if="!collections.length" description="还没有收藏任何非遗项目">
          <el-button type="danger" @click="router.push('/heritage')">去非遗博览逛逛</el-button>
        </el-empty>
        <div v-for="item in collections" :key="item.id" class="collect-item" @click="router.push(`/heritage/${item.heritageId}`)">
          <img :src="item.coverImage || defaultCover(item.name, 200, 140)" class="collect-cover" />
          <div class="collect-info">
            <div class="collect-name">{{ item.name }} <el-tag size="small" type="danger">{{ item.level }}</el-tag></div>
            <div class="collect-meta">{{ item.region }} · 收藏于 {{ item.collectionTime }}</div>
          </div>
          <el-button size="small" plain @click.stop="cancelCollect(item)">取消收藏</el-button>
        </div>
      </el-tab-pane>

      <!-- 我的评论 -->
      <el-tab-pane label="我的评论" name="comment">
        <el-empty v-if="!comments.length" description="还没有发表过评论，去项目详情页说说你的看法吧">
          <el-button type="danger" @click="router.push('/heritage')">去逛逛</el-button>
        </el-empty>
        <div v-for="item in comments" :key="item.id" class="comment-row">
          <div class="comment-main">
            <div class="comment-text">{{ item.content }}</div>
            <div class="comment-meta">
              评论于《{{ item.heritageName }}》 · {{ item.createTime }}
              <el-tag v-if="item.status === 0" size="small" type="info" style="margin-left: 6px">已被管理员屏蔽</el-tag>
            </div>
          </div>
          <el-button size="small" type="danger" plain @click="removeComment(item)">删除</el-button>
        </div>
        <div class="pager" v-if="commentTotal > 10">
          <el-pagination v-model:current-page="commentPage" :page-size="10" :total="commentTotal"
                         layout="prev, pager, next" background @current-change="loadComments" />
        </div>
      </el-tab-pane>

      <!-- 我的观看（按科普专题聚合 + 完成度进度条） -->
      <el-tab-pane label="我的观看" name="study">
        <el-empty v-if="!progressList.length" description="还没有观看记录，选一个科普专题开始观看吧">
          <el-button type="danger" @click="router.push('/')">去看热门科普视频</el-button>
        </el-empty>
        <div v-for="course in courseStudy" :key="course.courseId" class="course-study"
             @click="router.push(`/course/${course.courseId}`)">
          <div class="course-study-head">
            <span class="course-study-name">{{ course.courseName }}</span>
            <span class="course-study-percent">{{ coursePercent(course.courseId) }}%</span>
          </div>
          <el-progress :percentage="coursePercent(course.courseId)" :stroke-width="8"
                       :show-text="false" color="var(--gq-primary)" class="course-study-bar" />
          <div v-for="item in course.records" :key="item.id" class="study-item">
            <div class="study-main">
              <div class="study-chapter">{{ item.chapterTitle }}</div>
            </div>
            <div class="study-right">
              <el-tag size="small" :type="item.finished ? 'success' : 'info'">{{ item.finished ? '已完成' : '观看中' }}</el-tag>
              <span class="study-duration">{{ item.studyDuration }} 分钟</span>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 内容发布（docs/17：提交后需管理员审核通过才上线） -->
      <el-tab-pane label="内容发布" name="publish">
        <el-alert type="info" :closable="false" show-icon style="margin-bottom: 16px"
                  title="发布的内容须经管理员审核通过后才会展示在首页" />
        <el-form :model="publishForm" label-width="90px" style="max-width: 640px">
          <el-form-item label="内容类型">
            <el-radio-group v-model="publishForm.type">
              <el-radio-button value="news">科普快讯</el-radio-button>
              <el-radio-button value="course">科普视频</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="标题">
            <el-input v-model="publishForm.title" maxlength="60" show-word-limit placeholder="请输入标题" />
          </el-form-item>
          <el-form-item label="关联项目">
            <el-select v-model="publishForm.heritageId" placeholder="关联非遗项目（可选）" clearable filterable style="width: 100%">
              <el-option v-for="item in heritageOptions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="publishForm.type === 'course'" label="B站链接">
            <el-input v-model="publishForm.videoUrl" placeholder="https://www.bilibili.com/video/BVxxxx" />
          </el-form-item>
          <el-form-item :label="publishForm.type === 'course' ? '视频简介' : '正文'">
            <el-input v-model="publishForm.content" type="textarea" :rows="4" maxlength="2000" show-word-limit />
          </el-form-item>
          <el-form-item>
            <el-button type="danger" :loading="publishing" @click="publish">提交发布</el-button>
          </el-form-item>
        </el-form>

        <el-divider content-position="left">我的发布</el-divider>
        <el-empty v-if="!mySubs.length" description="还没有发布过内容" :image-size="60" />
        <div v-for="sub in mySubs" :key="sub.id" class="sub-row">
          <div class="sub-main">
            <div class="sub-title">
              {{ sub.title }}
              <el-tag size="small" type="info" effect="plain">{{ sub.type === 'course' ? '科普视频' : '科普快讯' }}</el-tag>
            </div>
            <div class="sub-meta">提交于 {{ sub.submitTime }}</div>
            <div v-if="sub.status === 2 && sub.reviewNote" class="sub-reject">驳回原因：{{ sub.reviewNote }}</div>
          </div>
          <el-tag :type="STATUS_META[sub.status]?.type">{{ STATUS_META[sub.status]?.text }}</el-tag>
        </div>
      </el-tab-pane>

      <!-- 个人资料 -->
      <el-tab-pane label="个人资料" name="profile">
        <el-form :model="profileForm" label-width="80px" style="max-width: 520px; margin-top: 10px">
          <el-form-item label="头像"><FileUpload v-model="profileForm.avatar" type="image" /></el-form-item>
          <el-form-item label="昵称"><el-input v-model="profileForm.nickname" maxlength="50" /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="profileForm.phone" maxlength="20" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="profileForm.email" maxlength="100" /></el-form-item>
          <el-form-item label="个人简介">
            <el-input v-model="profileForm.introduction" type="textarea" :rows="3" maxlength="500" />
          </el-form-item>
          <el-form-item>
            <el-button type="danger" :loading="saving" @click="saveProfile">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<style scoped>
.profile-card {
  min-height: 480px;
}
.collect-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
}
.collect-item:hover {
  background: #fdf6ec;
}
.collect-cover {
  width: 100px;
  height: 70px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}
.collect-info {
  flex: 1;
}
.collect-name {
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}
.collect-meta {
  color: #909399;
  font-size: 12px;
  margin-top: 6px;
}
.comment-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 0;
  border-bottom: 1px dashed #ebeef5;
}
.comment-main {
  flex: 1;
}
.comment-text {
  line-height: 1.6;
}
.comment-meta {
  color: #c0c4cc;
  font-size: 12px;
  margin-top: 4px;
}
.study-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  border-radius: 8px;
}
.study-item:hover {
  background: #fdf6ec;
}
/* 学习数据仪表板 */
.stat-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}
.stat-card {
  flex: 1;
  text-align: center;
  border-radius: 12px;
}
.stat-num {
  font-family: var(--font-heading);
  font-size: 26px;
  font-weight: 700;
  line-height: 1.3;
}
.stat-label {
  margin-top: 6px;
  color: #8a8578;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}
/* 按课程聚合的学习块 */
.course-study {
  padding: 14px 10px;
  border-bottom: 1px dashed var(--gq-border);
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 6px;
}
.course-study:hover {
  background: #fdf6ec;
}
.course-study-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.course-study-name {
  font-weight: 700;
  font-family: var(--font-heading);
}
.course-study-percent {
  color: var(--gq-primary);
  font-weight: 700;
}
.course-study-bar {
  margin: 8px 0 4px;
}
.study-course {
  font-weight: 600;
}
.study-chapter {
  color: #909399;
  font-size: 13px;
  margin-top: 4px;
}
.study-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.study-duration {
  color: #909399;
  font-size: 13px;
}
/* 内容发布：我的发布列表 */
.sub-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 0;
  border-bottom: 1px solid var(--gq-divider);
}
.sub-main {
  flex: 1;
  min-width: 0;
}
.sub-title {
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}
.sub-meta {
  margin-top: 4px;
  color: var(--gq-text-muted);
  font-size: 12px;
  font-family: var(--font-en);
}
.sub-reject {
  margin-top: 4px;
  color: var(--gq-primary);
  font-size: 13px;
}
.pager {
  display: flex;
  justify-content: center;
  padding-top: 12px;
}
</style>
