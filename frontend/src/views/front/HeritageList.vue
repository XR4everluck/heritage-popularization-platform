<script setup>
/**
 * 非遗列表页（docs/11 第 5.2、5.5 节）：分类/级别/地区筛选 + 关键词搜索（兼搜索结果页）
 * + 卡片列表 + 统一分页；面包屑语义化导航；底部版权备注。
 */
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategoryList, getHeritagePage } from '../../api/front'
import HeritageCard from '../../components/HeritageCard.vue'
import AppBreadcrumb from '../../components/AppBreadcrumb.vue'
import AppPagination from '../../components/AppPagination.vue'
import CopyrightNote from '../../components/CopyrightNote.vue'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const list = ref([])
const total = ref(0)
const loading = ref(false)
// 首屏骨架屏
const firstLoading = ref(true)
const query = ref({
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : null,
  level: null,
  // 非遗地图点击省份后以 ?region=省份简称 跳转至此，需在首屏即生效
  region: route.query.region ? String(route.query.region) : null,
  keyword: route.query.keyword ? String(route.query.keyword) : '',
  isNews: route.query.isNews ? Number(route.query.isNews) : null,
  page: 1,
  pageSize: 8
})

// 级别选项
const LEVELS = ['国家级', '省级', '市级']
// 地区选项：与非遗地图的省份名称保持一致（均为省份简称）
const REGIONS = [
  '北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江', '上海', '江苏',
  '浙江', '安徽', '福建', '江西', '山东', '河南', '湖北', '湖南', '广东', '广西',
  '海南', '重庆', '四川', '贵州', '云南', '西藏', '陕西', '甘肃', '青海', '宁夏',
  '新疆', '台湾', '香港', '澳门'
]

onMounted(async () => {
  // 分类下拉加载失败不阻塞列表（如后端未就绪），仅下拉为空
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch { /* 拦截器已提示 */ }
  loadData()
})

watch(() => route.query.categoryId, (val) => {
  query.value.categoryId = val ? Number(val) : null
  query.value.page = 1
  loadData()
})

watch(() => route.query.keyword, (val) => {
  query.value.keyword = val ? String(val) : ''
  query.value.page = 1
  loadData()
})

// 非遗地图点击省份跳转
watch(() => route.query.region, (val) => {
  query.value.region = val ? String(val) : null
  query.value.page = 1
  loadData()
})

// 首页快讯入口跳转
watch(() => route.query.isNews, (val) => {
  query.value.isNews = val ? Number(val) : null
  query.value.page = 1
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    // 只传非空参数
    const params = { page: query.value.page, pageSize: query.value.pageSize }
    if (query.value.categoryId) params.categoryId = query.value.categoryId
    if (query.value.level) params.level = query.value.level
    if (query.value.region) params.region = query.value.region
    if (query.value.keyword) params.keyword = query.value.keyword
    if (query.value.isNews) params.isNews = query.value.isNews
    const res = await getHeritagePage(params)
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
    firstLoading.value = false
  }
}

/** 清空筛选条件并重新查询 */
function resetFilters() {
  query.value.categoryId = null
  query.value.level = null
  query.value.region = null
  query.value.keyword = ''
  query.value.isNews = null
  query.value.page = 1
  loadData()
}

function search() {
  query.value.page = 1
  loadData()
}

/** 面包屑：搜索进入时为 非遗博览 / 搜索结果，常规进入为 首页 / 非遗博览 */
const breadcrumbItems = computed(() =>
  query.value.keyword
    ? [
        { label: '首页', to: '/' },
        { label: '非遗博览', to: '/heritage' },
        { label: '搜索结果' }
      ]
    : [{ label: '首页', to: '/' }, { label: '非遗博览' }]
)
</script>

<template>
  <div class="list-page">
    <!-- 面包屑（搜索进入时显示"搜索结果"层级） -->
    <AppBreadcrumb :items="breadcrumbItems" />

    <!-- 页头横幅（docs/14：居中标题 + 对称纹样装饰；搜索态下隐藏） -->
    <div v-if="!query.keyword" class="gq-page-header">
      <h1>非遗博览</h1>
      <p>国家级 · 省级 · 市级非物质文化遗产代表性项目</p>
    </div>

    <!-- 搜索结果统计行（docs/11 第 5.5 节："找到 N 条与关键词相关的结果"） -->
    <p v-if="query.keyword" class="search-summary" role="status">
      找到 <b>{{ total }}</b> 条与“{{ query.keyword }}”相关的结果
    </p>

    <!-- 骨架屏（首次加载） -->
    <el-row :gutter="16" v-if="firstLoading">
      <el-col v-for="i in 8" :key="i" :xs="12" :sm="12" :md="8" :lg="6">
        <el-card shadow="never" :body-style="{ padding: 0 }" class="sk-card">
          <el-skeleton animated :loading="true">
            <template #template>
              <el-skeleton-item variant="image" style="width: 100%; height: 160px" />
              <div style="padding: 12px">
                <el-skeleton-item variant="text" style="width: 60%" />
                <el-skeleton-item variant="text" style="width: 40%; margin-top: 8px" />
                <el-skeleton-item variant="text" style="width: 80%; margin-top: 8px" />
              </div>
            </template>
          </el-skeleton>
        </el-card>
      </el-col>
    </el-row>

    <template v-else>
    <!-- 筛选区 -->
    <el-card shadow="never" class="filter-card">
      <div class="filter-row">
        <!-- 快讯/全部切换 -->
        <el-radio-group v-model="query.isNews" @change="search" size="default">
          <el-radio-button :value="null">全部</el-radio-button>
          <el-radio-button :value="1">科普快讯</el-radio-button>
        </el-radio-group>
        <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 160px" @change="search">
          <el-option v-for="cate in categories" :key="cate.id" :label="cate.name" :value="cate.id" />
        </el-select>
        <el-select v-model="query.level" placeholder="非遗级别" clearable style="width: 130px" @change="search">
          <el-option v-for="lv in LEVELS" :key="lv" :label="lv" :value="lv" />
        </el-select>
        <el-select v-model="query.region" placeholder="所属地区" clearable filterable style="width: 140px" @change="search">
          <el-option v-for="r in REGIONS" :key="r" :label="r" :value="r" />
        </el-select>
        <el-input v-model="query.keyword" placeholder="搜索非遗名称..." clearable style="width: 220px"
                  @keyup.enter="search" @clear="search" />
        <el-button type="danger" @click="search"><el-icon><Search /></el-icon>&nbsp;搜索</el-button>
      </div>
    </el-card>

    <!-- 卡片列表 -->
    <el-empty v-if="!list.length && !loading" :description="query.isNews ? '暂无科普快讯' : '没有找到相关非遗项目，换个关键词试试？'">
      <el-button type="danger" @click="resetFilters">清空筛选条件</el-button>
      <el-button @click="router.push('/')">回首页逛逛</el-button>
    </el-empty>
    <el-row v-loading="loading" :gutter="16" class="card-list">
      <el-col v-for="item in list" :key="item.id" :xs="12" :sm="12" :md="8" :lg="6">
        <HeritageCard :item="item" @open="router.push(`/heritage/${item.id}`)" />
      </el-col>
    </el-row>

    <!-- 分页（统一公共组件） -->
    <AppPagination v-model:page="query.page" :page-size="query.pageSize" :total="total" @change="loadData" />

    <!-- 版权备注：卡片封面为视觉中国素材（docs/12 版权约束） -->
    <CopyrightNote />
    </template>
  </div>
</template>

<style scoped>
/* 面包屑项：非遗博览 / 搜索结果 */
/* 搜索结果统计行：数字命中 Times New Roman（全局字体栈） */
.search-summary {
  margin: 0 0 16px;
  font-size: 14px;
  color: var(--gq-text-secondary);
}
.search-summary b {
  color: var(--gq-primary);
  font-family: var(--font-en);
}
.filter-card {
  margin-bottom: 16px;
}
.filter-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}
.card-list {
  min-height: 300px;
}
.card-list .el-col {
  margin-bottom: 16px;
}
.sk-card {
  margin-bottom: 16px;
  border-radius: 4px;
}
</style>
