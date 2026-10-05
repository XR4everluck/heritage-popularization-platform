<script setup>
/**
 * 课程学习页：课程信息 + 章节目录 + 视频播放 + 学习进度上报（时长累计/完成标记）
 */
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCourseDetail, getCourseChapters, getMyProgress, updateProgress, addCourseView } from '../../api/front'
import { useUserStore } from '../../store/user'
import { defaultCover, coverFallback } from '../../utils/placeholder'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const course = ref(null)
const notFound = ref(false)
const chapters = ref([])
const currentChapter = ref(null)
const myProgress = ref([])

// 播放器引用与计时
const videoRef = ref()
const playedSeconds = ref(0)

// B站外链视频（种子数据使用的外链形式）：转成官方播放器嵌入地址
const isBili = computed(() => /bilibili\.com|b23\.tv/.test(currentChapter.value?.videoUrl || ''))
const biliEmbed = computed(() => {
  const url = currentChapter.value?.videoUrl || ''
  const bv = url.match(/BV[0-9A-Za-z]+/)
  if (!bv) return ''
  const page = url.match(/[?&]p=(\d+)/)
  return `https://player.bilibili.com/player.html?bvid=${bv[0]}&page=${page ? page[1] : 1}&autoplay=0&danmaku=0&high_quality=1`
})

// 非 B站的 http(s) 外链且不是直链媒体文件（mp4 等）：原生 video 大概率无法解码，
// 播放器下方给出"新窗口打开"兜底入口
const isExternalPage = computed(() => {
  const url = currentChapter.value?.videoUrl || ''
  if (!/^https?:\/\//i.test(url) || isBili.value) return false
  return !/\.(mp4|webm|ogg|ogv|mov|mkv|mp3|wav|m4a)(\?.*)?$/i.test(url)
})

const progressOfChapter = computed(() => {
  const map = {}
  myProgress.value.forEach((p) => (map[p.chapterId] = p))
  return map
})

onMounted(async () => {
  let courseRes
  try {
    courseRes = await getCourseDetail(route.params.id)
  } catch (e) {
    // 课程不存在/已删除：notFound 置位，页面展示友好空状态
    notFound.value = true
    return
  }
  course.value = courseRes.data
  // 浏览量自增（不阻塞页面）
  addCourseView(route.params.id).catch(() => {})
  const chapterRes = await getCourseChapters(route.params.id)
  chapters.value = chapterRes.data
  if (chapters.value.length) {
    selectChapter(chapters.value[0])
  }
  // 已登录则加载本课程的学习进度
  if (userStore.isLogin) {
    const res = await getMyProgress({ courseId: route.params.id })
    myProgress.value = res.data
  }
})

/** 切换章节：保存上一章节的已观看时长 */
function selectChapter(chapter) {
  reportProgress()
  currentChapter.value = chapter
  playedSeconds.value = 0
}

/** 记录播放时长（timeupdate 每4秒左右触发一次，此处只累计不提交） */
function onTimeUpdate() {
  playedSeconds.value += 4
}

/** 上报学习进度：累计时长 + 完成状态（未登录静默跳过） */
async function reportProgress(finished) {
  if (!userStore.isLogin || !currentChapter.value) return
  const minutes = Math.round(playedSeconds.value / 60)
  if (minutes <= 0 && !finished) return
  try {
    await updateProgress({
      chapterId: currentChapter.value.id,
      courseId: course.value.id,
      studyDuration: minutes,
      finished: finished ? 1 : undefined
    })
    playedSeconds.value = 0
    if (finished) ElMessage.success('已完成本章节学习')
    const res = await getMyProgress({ courseId: route.params.id })
    myProgress.value = res.data
  } catch {
    /* 进度上报失败不打断学习 */
  }
}

/** 播放自然结束：标记章节完成 */
function onVideoEnded() {
  reportProgress(true)
}
</script>

<template>
  <!-- 课程不存在/已被删除时的友好空状态 -->
  <el-card v-if="notFound" shadow="never" class="notfound-card">
    <el-empty description="课程不存在或已被删除">
      <el-button type="danger" @click="router.push('/heritage')">去逛逛非遗项目</el-button>
    </el-empty>
  </el-card>

  <div v-else-if="course" class="course-page">
    <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon> 返回</el-button>

    <!-- 课程信息 -->
    <el-card shadow="never" class="head-card">
      <div class="head-body">
        <img :src="course.cover || defaultCover(course.name, 480, 300)" class="head-cover" />
        <div class="head-info">
          <h2>{{ course.name }}</h2>
          <div class="course-desc">{{ course.summary }}</div>
          <div class="course-meta">
            <span>讲师：{{ course.teacher || '-' }}</span>
            <span>总时长：{{ course.duration }} 分钟</span>
            <span>章节数：{{ chapters.length }}</span>
            <span>浏览：{{ course.viewCount }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <div class="study-body">
      <!-- 视频播放区 -->
      <el-card shadow="never" class="player-card">
        <template #header><b>正在学习：{{ currentChapter?.title || '暂无章节' }}</b></template>
        <!-- B站外链视频：使用官方播放器内嵌播放 -->
        <iframe v-if="currentChapter?.videoUrl && isBili" :key="'bili-' + currentChapter.id"
                :src="biliEmbed" scrolling="no" frameborder="0" allowfullscreen
                class="player bili-player"></iframe>
        <!-- 其他 http(s) 外链：直链媒体走原生 video，网页外链提供新窗口打开兜底 -->
        <video v-else-if="currentChapter?.videoUrl" ref="videoRef" :key="currentChapter.id"
               :src="currentChapter.videoUrl" controls class="player"
               @timeupdate="onTimeUpdate" @ended="onVideoEnded" />
        <div v-else class="player-placeholder">
          <el-empty :description="currentChapter ? '本章节视频暂未上传，可先阅读图文讲义' : '暂无章节内容'" :image-size="80" />
        </div>
        <!-- 兜底提示（独立于上方 v-if 链，避免打断条件分支） -->
        <div v-if="currentChapter?.videoUrl && isExternalPage" class="external-tip">
          本章节为外部视频链接，若上方播放器无法解码，
          <el-link type="danger" :href="currentChapter.videoUrl" target="_blank" class="external-link">在新窗口打开观看&nbsp;<el-icon><TopRight /></el-icon></el-link>
        </div>
        <div v-if="currentChapter?.content" class="rich-content chapter-content" v-html="currentChapter.content"></div>
        <div class="study-actions">
          <span class="study-tip">观看视频时长会自动累计到学习进度</span>
          <el-button type="danger" plain @click="reportProgress(true)"><el-icon><CircleCheck /></el-icon>&nbsp;标记本章节完成</el-button>
        </div>
      </el-card>

      <!-- 章节目录 -->
      <el-card shadow="never" class="chapter-card">
        <template #header><b>章节目录</b></template>
        <div v-for="(chapter, index) in chapters" :key="chapter.id" class="chapter-item"
             :class="{ active: currentChapter?.id === chapter.id }" @click="selectChapter(chapter)">
          <span class="chapter-index">{{ index + 1 }}</span>
          <span class="chapter-title">{{ chapter.title }}</span>
          <el-tag v-if="index === 0" size="small" type="success" effect="light">免费试看</el-tag>
          <el-tag v-if="progressOfChapter[chapter.id]?.finished" size="small" type="success">已完成</el-tag>
          <span v-else-if="progressOfChapter[chapter.id]" class="chapter-duration">
            {{ progressOfChapter[chapter.id].studyDuration }} 分钟
          </span>
        </div>
        <el-empty v-if="!chapters.length" description="暂无章节" :image-size="60" />
        <div v-if="!userStore.isLogin" class="login-tip">登录后可记录学习进度</div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.head-card {
  margin: 10px 0 16px;
}
.head-body {
  display: flex;
  gap: 24px;
}
.head-cover {
  width: 320px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}
.notfound-card {
  margin: 10px 0;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.head-info h2 {
  margin-bottom: 10px;
}
.course-desc {
  color: #606266;
  line-height: 1.7;
}
.course-meta {
  margin-top: 16px;
  display: flex;
  gap: 24px;
  color: #909399;
  font-size: 13px;
}
.study-body {
  display: flex;
  gap: 16px;
  padding-bottom: 20px;
}
.player-card {
  flex: 1;
}
.player {
  width: 100%;
  max-height: 460px;
  border-radius: 8px;
  background: #000;
}
/* B站内嵌播放器保持 16:9 */
.bili-player {
  aspect-ratio: 16 / 9;
  height: auto;
  border: 0;
}
/* 外链视频兜底提示 */
.external-tip {
  margin-top: 10px;
  font-size: 13px;
  color: #909399;
}
.external-link {
  vertical-align: middle;
  font-size: 13px;
}
.player-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.chapter-content {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px dashed #ebeef5;
}
.study-actions {
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.study-tip {
  color: #c0c4cc;
  font-size: 12px;
}
.chapter-card {
  width: 340px;
  flex-shrink: 0;
}
.chapter-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 10px;
  border-radius: 6px;
  cursor: pointer;
  margin-bottom: 4px;
}
.chapter-item:hover {
  background: #fdf6ec;
}
.chapter-item.active {
  background: #fde8e0;
  color: #c0392b;
  font-weight: 600;
}
.chapter-index {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #f0f2f5;
  color: #909399;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.chapter-item.active .chapter-index {
  background: #c0392b;
  color: #fff;
}
.chapter-title {
  flex: 1;
  font-size: 14px;
}
.chapter-duration {
  color: #c0c4cc;
  font-size: 12px;
}
.login-tip {
  margin-top: 10px;
  color: #c0c4cc;
  font-size: 12px;
  text-align: center;
}
</style>
