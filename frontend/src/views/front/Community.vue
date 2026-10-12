<!--
  全民科普（docs/20）：用户创作内容浏览入口
  仅展示管理员审核通过（status=1）的用户发布内容（快讯/视频），由后端接口兜底
  游客可浏览；快讯详情文本插值渲染（防 XSS）；视频弹窗内嵌 B 站播放器
-->
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getApprovedSubmissions } from '../../api/front'
import { buildBiliEmbed, isBilibiliUrl } from '../../utils/bilibili'
import { defaultCover } from '../../utils/placeholder'
import AppBreadcrumb from '../../components/AppBreadcrumb.vue'
import AppPagination from '../../components/AppPagination.vue'

const router = useRouter()

const activeType = ref('')
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 9
const loading = ref(false)
const firstLoading = ref(true)

// 详情弹窗：news 为文本详情，course 为内嵌播放
const detail = ref(null)
const detailVisible = ref(false)

/** 视频弹窗的 B 站嵌入地址（无 BV 号时弹窗内给出新窗口兜底） */
const detailBiliEmbed = computed(() => (detail.value ? buildBiliEmbed(detail.value.videoUrl) : ''))

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (activeType.value) params.type = activeType.value
    const res = await getApprovedSubmissions(params)
    list.value = res.data.records || []
    total.value = Number(res.data.total) || 0
  } finally {
    loading.value = false
    firstLoading.value = false
  }
}

/** 切换类型：回到第 1 页重新查询 */
function switchType(type) {
  activeType.value = type
  page.value = 1
  loadData()
}

function onPageChange(next) {
  page.value = next
  loadData()
  window.scrollTo({ top: 0 })
}

function openDetail(item) {
  detail.value = item
  detailVisible.value = true
}

function excerpt(text, length = 64) {
  const t = (text || '').replace(/\s+/g, ' ').trim()
  return t.length > length ? `${t.slice(0, length)}…` : t
}

function fmtTime(t) {
  return (t || '').slice(0, 16)
}

/** "我要发布"：登录用户去个人中心发布 Tab，游客先登录并回跳 */
function goPublish() {
  router.push('/profile?tab=publish')
}
</script>

<template>
  <div class="community-page">
    <AppBreadcrumb :items="[{ label: '首页', to: '/' }, { label: '全民科普' }]" />

    <div class="gq-page-header">
      <h1>全民科普</h1>
      <p>来自普通用户的非遗科普创作 · 均经管理员审核后发布</p>
    </div>

    <!-- 类型筛选 + 发布入口 -->
    <div class="community-toolbar">
      <el-radio-group :model-value="activeType" @change="switchType">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="news">科普快讯</el-radio-button>
        <el-radio-button value="course">科普视频</el-radio-button>
      </el-radio-group>
      <el-button type="danger" plain @click="goPublish">我要发布</el-button>
    </div>

    <!-- 骨架屏 -->
    <el-row :gutter="16" v-if="firstLoading">
      <el-col v-for="i in 6" :key="i" :xs="24" :sm="12" :lg="8">
        <el-card shadow="never" class="sk-card">
          <el-skeleton animated :loading="true">
            <template #template>
              <el-skeleton-item variant="image" style="width: 100%; height: 140px" />
              <div style="padding: 12px">
                <el-skeleton-item variant="text" style="width: 70%" />
                <el-skeleton-item variant="text" style="width: 45%; margin-top: 8px" />
              </div>
            </template>
          </el-skeleton>
        </el-card>
      </el-col>
    </el-row>

    <template v-else>
      <el-empty v-if="!list.length && !loading" description="还没有用户发布的科普内容，期待你的创作">
        <el-button type="danger" @click="goPublish">去发布第一条</el-button>
      </el-empty>

      <el-row v-loading="loading" :gutter="16" class="card-list">
        <el-col v-for="item in list" :key="item.id" :xs="24" :sm="12" :lg="8">
          <el-card shadow="hover" class="community-card" :body-style="{ padding: 0 }" @click="openDetail(item)">
            <div class="card-cover-wrap">
              <img :src="defaultCover(item.title, 600, 340)" :alt="item.title || '科普内容封面'"
                   width="600" height="340" class="card-cover" />
              <!-- 类型角标 -->
              <span class="card-type" :class="item.type">
                {{ item.type === 'course' ? '▶ 视频' : '快讯' }}
              </span>
            </div>
            <div class="card-body">
              <div class="card-title">{{ item.title }}</div>
              <div class="card-excerpt">{{ excerpt(item.content) }}</div>
              <div class="card-meta">
                <span class="card-meta-left">
                  {{ item.heritageName || '通用科普' }} · {{ item.submitter }}
                </span>
                <span class="card-time">{{ fmtTime(item.reviewTime || item.submitTime) }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <AppPagination v-model:page="page" :page-size="pageSize" :total="total" @change="onPageChange" />
    </template>

    <!-- 快讯详情弹窗：正文文本插值渲染（UGC 纯文本，防 XSS，docs/20） -->
    <el-dialog v-model="detailVisible" :title="detail?.title || '科普快讯'" width="620px">
      <div v-if="detail" class="detail-body">
        <div class="detail-meta">
          <el-tag size="small" type="info" effect="plain">{{ detail.type === 'course' ? '科普视频' : '科普快讯' }}</el-tag>
          <span v-if="detail.heritageName">关联项目：{{ detail.heritageName }}</span>
          <span>作者：{{ detail.submitter }}</span>
          <span>{{ fmtTime(detail.reviewTime || detail.submitTime) }}</span>
        </div>
        <template v-if="detail.type === 'course'">
          <iframe v-if="isBilibiliUrl(detail.videoUrl) && detailBiliEmbed"
                  :src="detailBiliEmbed" scrolling="no" frameborder="0" allowfullscreen
                  class="detail-player"></iframe>
          <div v-else class="detail-fallback">
            该视频为外部链接：
            <el-link type="danger" :href="detail.videoUrl" target="_blank">在新窗口打开观看</el-link>
          </div>
          <p class="detail-text">{{ detail.content }}</p>
        </template>
        <template v-else>
          <p class="detail-text detail-text--news">{{ detail.content }}</p>
          <el-button v-if="detail.heritageId" type="danger" plain
                     @click="router.push(`/heritage/${detail.heritageId}`); detailVisible = false">
            查看关联非遗项目
          </el-button>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.community-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}
.card-list {
  min-height: 300px;
}
.card-list .el-col {
  margin-bottom: 16px;
}
.sk-card {
  margin-bottom: 16px;
}
.community-card {
  cursor: pointer;
  transition: none;
}
.community-card:hover {
  border-color: var(--gq-primary);
}
.community-card:hover .card-title {
  color: var(--gq-primary);
}
.card-cover-wrap {
  position: relative;
}
.card-cover {
  width: 100%;
  height: 150px;
  object-fit: cover;
  display: block;
}
.card-type {
  position: absolute;
  left: 8px;
  top: 8px;
  padding: 2px 10px;
  border-radius: 2px;
  font-size: 12px;
  color: #fff;
  background: rgba(51, 51, 51, 0.72);
}
.card-type.course {
  background: rgba(158, 32, 33, 0.88);
}
.card-body {
  padding: 12px 14px 14px;
}
.card-title {
  font-weight: 700;
  font-size: 16px;
  color: var(--gq-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.card-excerpt {
  margin-top: 6px;
  font-size: 13px;
  line-height: 1.6;
  color: var(--gq-text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 42px;
}
.card-meta {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--gq-text-muted);
}
.card-meta-left {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.card-time {
  flex-shrink: 0;
  font-family: var(--font-en);
}
/* 详情弹窗 */
.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
  font-size: 13px;
  color: var(--gq-text-muted);
}
.detail-player {
  width: 100%;
  aspect-ratio: 16 / 9;
  border: none;
  border-radius: 2px;
  background: #000;
}
.detail-fallback {
  padding: 16px;
  text-align: center;
  background: var(--gq-bg, #f8f8f8);
  border-radius: 4px;
  font-size: 14px;
  color: var(--gq-text-secondary);
}
.detail-text {
  margin: 12px 0 0;
  font-size: 16px;
  line-height: 1.75;
  color: var(--gq-text-secondary);
  white-space: pre-wrap;
  word-break: break-word;
}
.detail-text--news {
  color: var(--gq-text);
}
@media (max-width: 768px) {
  .community-toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  .community-toolbar .el-button {
    align-self: flex-end;
  }
}
</style>
