<script setup>
/**
 * 非遗详情页：图文详情 + 相关课程 + 相关推荐 + 评论区 + 答题测验 + 一键分享
 */
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHeritageDetail, getHeritageRecommend, getCourseListByHeritage, addHeritageView,
         getCommentPage, postComment,
         addCollection, removeCollection, getMyCollections,
         getRandomQuestions, submitAnswer } from '../../api/front'
import { useUserStore } from '../../store/user'
import { defaultCover } from '../../utils/placeholder'
import HeritageCard from '../../components/HeritageCard.vue'
import CourseCard from '../../components/CourseCard.vue'
import QuizModal from '../../components/QuizModal.vue'
import ShareModal from '../../components/ShareModal.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const info = ref(null)
const courses = ref([])
const commentTotal = ref(0)
const comments = ref([])
const commentPage = ref(1)
const commentContent = ref('')
const posting = ref(false)
const collected = ref(false)
const collecting = ref(false)
const quizModalVisible = ref(false)
const quizQuestions = ref([])
const quizAnswers = ref({})
const quizScore = ref(0)
const relatedRecommendations = ref([])
const shareModalVisible = ref(false)

/** 当前非遗 id（相关推荐/路由切换时随之变化） */
const heritageId = computed(() => Number(route.params.id))

/** 代表作品拆分为标签（「古琴、琴谱」→ [古琴, 琴谱]） */
const workTags = computed(() =>
  (info.value?.representativeWorks || '').split(/[,，、;；]/).map(t => t.trim()).filter(Boolean)
)

/** 濒危程度对应标签色：危 → 红、中间态 → 橙、良好 → 绿 */
const endangerTagType = computed(() => {
  const level = info.value?.endangerLevel || ''
  if (/濒危|严重|危险/.test(level)) return 'danger'
  if (/需关注|关注|一般/.test(level)) return 'warning'
  if (/良好|安全/.test(level)) return 'success'
  return 'info'
})

// 计算当前页面URL
const currentUrl = computed(() => window.location.pathname)

// 分享信息
const shareInfo = computed(() => {
  return {
    title: info.value?.name || '非遗文化项目',
    description: info.value?.summary || '探索中国传统非遗文化的魅力',
    coverImage: info.value?.coverImage || defaultCover(info.value?.name || '非遗文化项目', 400, 300),
    url: currentUrl.value
  }
})

onMounted(() => {
  loadDetail()
  loadComments()
})

// 相关推荐点击跳转其他非遗详情时，按新 id 整页重载
watch(() => route.params.id, (val, old) => {
  if (val && val !== old) {
    loadDetail()
    loadComments()
  }
})

async function loadDetail() {
  loading.value = true
  try {
    const id = heritageId.value
    const res = await getHeritageDetail(id)
    info.value = res.data
    collected.value = false
    syncCollected(id)
    // 相关课程 / 相关推荐 / 浏览量自增：失败不阻塞详情展示
    getCourseListByHeritage(id).then((r) => (courses.value = r.data || [])).catch(() => {})
    getHeritageRecommend(id, 4).then((r) => (relatedRecommendations.value = r.data || [])).catch(() => {})
    addHeritageView(id).catch(() => {})
  } catch (e) {
    // 项目不存在/未发布：拦截器已提示，这里展示空状态
    info.value = null
  } finally {
    loading.value = false
  }
}

/** 同步收藏状态（仅登录用户；收藏列表含 heritageId 与项目摘要） */
async function syncCollected(id) {
  if (!userStore.isLogin) return
  try {
    const res = await getMyCollections()
    collected.value = (res.data || []).some((c) => c.heritageId === id)
  } catch { /* 忽略，按钮仍可点击重试 */ }
}

// 切换收藏（真实调用收藏接口）
async function toggleCollect() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  collecting.value = true
  try {
    if (collected.value) {
      await removeCollection(heritageId.value)
      collected.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addCollection(heritageId.value)
      collected.value = true
      ElMessage.success('收藏成功')
    }
  } catch { /* 失败原因拦截器已提示 */ } finally {
    collecting.value = false
  }
}

// 跳转到传承人详情页
function goInheritorDetail(inheritorId) {
  router.push(`/inheritor/${inheritorId}`)
}

// 打开/关闭分享弹窗
function openShare() { shareModalVisible.value = true }
function closeShare() { shareModalVisible.value = false }

// 打开测验弹窗
function openQuiz() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录后再参与答题')
    return
  }
  quizModalVisible.value = true
  loadQuizQuestions()
}

// 加载测验题目
async function loadQuizQuestions() {
  try {
    const res = await getRandomQuestions(info.value.id)
    quizQuestions.value = res.data
    quizAnswers.value = {}
    quizScore.value = 0
  } catch (e) {
    ElMessage.error('加载题目失败，请稍后再试')
    quizModalVisible.value = false
  }
}

// 提交答案：逐题提交，后端按题判分并累计积分
async function submitQuiz() {
  const answered = Object.keys(quizAnswers.value).length
  if (answered !== quizQuestions.value.length) {
    ElMessage.warning('请完成所有题目后再提交')
    return
  }

  try {
    const results = await Promise.all(quizQuestions.value.map((question) =>
      submitAnswer({
        questionId: question.id,
        answer: quizAnswers.value[question.id]
      })
    ))
    quizScore.value = results.reduce((sum, res) => sum + (Number(res.data?.score) || 0), 0)
    ElMessage.success(`答题完成！得分：${quizScore.value}分`)
  } catch (e) {
    ElMessage.error('提交答案失败，请稍后再试')
  }
}

// 再答一次：清空作答记录并重新抽题
function resetQuiz() {
  quizAnswers.value = {}
  quizScore.value = 0
  loadQuizQuestions()
}

// 加载评论（公开接口，分页）
async function loadComments(page) {
  if (page) commentPage.value = page
  try {
    const res = await getCommentPage({ heritageId: heritageId.value, page: commentPage.value, pageSize: 10 })
    comments.value = res.data.records || []
    commentTotal.value = Number(res.data.total) || 0
  } catch { /* 加载失败保留原列表 */ }
}

// 提交评论
async function submitComment() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录后再评论')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  const content = commentContent.value.trim()
  if (!content) {
    ElMessage.warning('评论内容不能为空')
    return
  }
  posting.value = true
  try {
    await postComment({ heritageId: heritageId.value, content })
    ElMessage.success('评论发表成功')
    commentContent.value = ''
    commentPage.value = 1
    loadComments()
  } catch { /* 失败原因拦截器已提示 */ } finally {
    posting.value = false
  }
}
</script>

<template>
  <div class="heritage-detail">
    <el-card shadow="never" v-loading="loading">
      <div class="detail-header">
        <h1>{{ info?.name || '非遗详情' }}</h1>
        <div class="meta">
          <span><el-icon><Location /></el-icon>{{ info?.region || '未知地区' }}</span>
          <span><el-icon><Award /></el-icon>{{ info?.level || '未知级别' }}</span>
          <span><el-icon><Calendar /></el-icon>{{ (info?.publishTime || '').slice(0, 10) || '未发布' }}</span>
        </div>
      </div>

      <el-card shadow="never" class="info-card">
        <template #header><b>基本信息</b></template>
        <div class="info-grid">
          <div class="info-block">
            <span class="info-label"><el-icon><Document /></el-icon> 起源年代</span>
            <span class="info-value">{{ info?.originAge || '待补充' }}</span>
          </div>
          <div class="info-block">
            <span class="info-label"><el-icon><Location /></el-icon> 分布地区</span>
            <span class="info-value">{{ info?.distributionArea || '待补充' }}</span>
          </div>
          <div class="info-block">
            <span class="info-label"><el-icon><Medal /></el-icon> 代表作品</span>
            <div class="info-value">
              <el-tag v-for="work in workTags" :key="work" size="small" effect="plain" class="work-tag">{{ work }}</el-tag>
            </div>
          </div>
          <div class="info-block">
            <span class="info-label"><el-icon><User /></el-icon> 传承人</span>
            <span class="info-value">
              <template v-if="info?.inheritor">
                <a href="javascript:void(0)" @click="goInheritorDetail(info.inheritorId)" class="inheritor-link">
                  {{ info.inheritor }}
                </a>
              </template>
              <template v-else>待补充</template>
            </span>
          </div>
          <div class="info-block">
            <span class="info-label"><el-icon><Warning /></el-icon> 濒危程度</span>
            <div class="endanger-wall">
              <el-tag :type="endangerTagType" effect="dark" size="large">{{ info?.endangerLevel || '暂无评估' }}</el-tag>
            </div>
          </div>
          <el-button type="danger" :plain="collected" :loading="collecting" class="collect-btn"
                     @click="toggleCollect">
            <el-icon><StarFilled /></el-icon>&nbsp;{{ collected ? '取消收藏' : '收藏本项目' }}
          </el-button>
          <el-button type="primary" class="share-btn" @click="openShare">
            <el-icon><Share /></el-icon>&nbsp;分享
          </el-button>
        </div>
      </el-card>

      <!-- 相关课程（紧凑列表） -->
      <el-card shadow="never" class="side-card">
        <template #header><b>相关科普视频（{{ courses.length }}）</b></template>
        <el-empty v-if="!courses.length" description="暂无科普视频" :image-size="50" />
        <el-row :gutter="12">
          <el-col v-for="course in courses" :key="course.id" :xs="12" :sm="12" :md="12" :lg="12">
            <CourseCard :item="course" @open="router.push(`/course/${course.id}`)" />
          </el-col>
        </el-row>
      </el-card>

      <!-- 相关推荐（多维度关联） -->
      <el-card shadow="never" class="side-card">
        <template #header><b>相关推荐</b></template>
        <el-empty v-if="!relatedRecommendations.length" description="暂无相关推荐" :image-size="50" />
        <el-row :gutter="12">
          <el-col v-for="heritage in relatedRecommendations" :key="heritage.id" :xs="12" :sm="12" :md="12" :lg="12">
            <HeritageCard :item="heritage" @open="router.push(`/heritage/${heritage.id}`)" />
          </el-col>
        </el-row>
      </el-card>

      <!-- 非遗小测验入口 -->
      <el-card shadow="never" class="side-card">
        <template #header><b>测一测你了解多少</b></template>
        <div class="quiz-section">
          <el-button type="primary" @click="openQuiz" :disabled="!userStore.isLogin">
            <el-icon><QuestionFilled /></el-icon>&nbsp;开始答题
          </el-button>
          <div class="quiz-tip" v-if="!userStore.isLogin">
            <el-icon><InfoFilled /></el-icon>&nbsp;登录后可参与答题获取积分
          </div>
        </div>
      </el-card>

      <!-- 评论区 -->
      <el-card shadow="never">
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
    </el-card>

    <QuizModal v-model="quizModalVisible" :questions="quizQuestions" :answers="quizAnswers"
              :score="quizScore" @submit="submitQuiz" @reset="resetQuiz" />
    
    <!-- 分享弹窗 -->
    <ShareModal v-model="shareModalVisible" :share-info="shareInfo" @close="closeShare" />
  </div>
</template>

<style scoped>
.detail-header {
  margin-bottom: 20px;
}
.detail-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--gq-primary);
  margin-bottom: 12px;
}
.meta {
  display: flex;
  gap: 20px;
  color: #8a8578;
  font-size: 14px;
}
.meta span {
  display: flex;
  align-items: center;
  gap: 6px;
}
.info-card {
  margin-bottom: 20px;
}
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}
.info-block {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.info-label {
  color: #8a8578;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.info-value {
  flex: 1;
  font-size: 15px;
  line-height: 1.6;
}
.work-tag {
  margin-right: 6px;
  margin-bottom: 6px;
}
.endanger-wall {
  display: flex;
  align-items: center;
  gap: 10px;
}
.collect-btn, .share-btn {
  position: absolute;
  top: 20px;
  right: 20px;
}

.share-btn {
  margin-right: 120px;
}
.side-card {
  margin-bottom: 20px;
}
.quiz-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.quiz-tip {
  color: #8a8578;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.comment-editor {
  margin-bottom: 16px;
  padding: 16px;
  background: var(--gq-card);
  border-radius: 8px;
}
.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px dashed var(--gq-border);
}
.comment-body {
  flex: 1;
}
.comment-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.comment-head b {
  color: var(--gq-primary);
}
.comment-time {
  color: #8a8578;
  font-size: 12px;
}
.comment-content {
  color: var(--gq-text);
  line-height: 1.6;
}
.pager {
  display: flex;
  justify-content: center;
  padding: 16px 0;
}
</style>