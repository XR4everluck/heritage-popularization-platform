<script setup>
/**
 * 非遗列表页：分类/级别筛选 + 关键词搜索 + 卡片列表 + 分页
 */
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategoryList, getHeritagePage } from '../../api/front'
import HeritageCard from '../../components/HeritageCard.vue'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const list = ref([])
const total = ref(0)
const loading = ref(false)
// 首屏骨架屏：仅在首次加载时展示
const firstLoading = ref(true)
const query = ref({
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : null,
  level: null,
  keyword: route.query.keyword ? String(route.query.keyword) : '',
  page: 1,
  pageSize: 8
})

onMounted(async () => {
  const res = await getCategoryList()
  categories.value = res.data
  loadData()
})

watch(() => route.query.categoryId, (val) => {
  query.value.categoryId = val ? Number(val) : null
  query.value.page = 1
  loadData()
})

// 首页搜索框/热门标签跳转过来时自动执行搜索
watch(() => route.query.keyword, (val) => {
  query.value.keyword = val ? String(val) : ''
  query.value.page = 1
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getHeritagePage(query.value)
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
    firstLoading.value = false
  }
}

/** 清空筛选条件并重新查询（空结果引导） */
function resetFilters() {
  query.value.categoryId = null
  query.value.level = null
  query.value.keyword = ''
  query.value.page = 1
  loadData()
}

function search() {
  query.value.page = 1
  loadData()
}
</script>

<template>
  <div class="list-page">
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
        <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 160px" @change="search">
          <el-option v-for="cate in categories" :key="cate.id" :label="cate.name" :value="cate.id" />
        </el-select>
        <el-select v-model="query.level" placeholder="非遗级别" clearable style="width: 140px" @change="search">
          <el-option label="国家级" value="国家级" />
          <el-option label="省级" value="省级" />
          <el-option label="市级" value="市级" />
        </el-select>
        <el-input v-model="query.keyword" placeholder="搜索非遗名称..." clearable style="width: 260px"
                  @keyup.enter="search" @clear="search" />
        <el-button type="danger" @click="search"><el-icon><Search /></el-icon>&nbsp;搜索</el-button>
      </div>
    </el-card>

    <!-- 卡片列表（统一卡片风格，移动端 2 列） -->
    <el-empty v-if="!list.length && !loading" description="没有找到相关非遗项目，换个关键词试试？">
      <el-button type="danger" @click="resetFilters">清空筛选条件</el-button>
      <el-button @click="router.push('/')">回首页逛逛</el-button>
    </el-empty>
    <el-row v-loading="loading" :gutter="16" class="card-list">
      <el-col v-for="item in list" :key="item.id" :xs="12" :sm="12" :md="8" :lg="6">
        <HeritageCard :item="item" @open="router.push(`/heritage/${item.id}`)" />
      </el-col>
    </el-row>

    <!-- 分页 -->
    <div class="pager">
      <el-pagination v-model:current-page="query.page" :page-size="query.pageSize" :total="total"
                     layout="prev, pager, next, total" background @current-change="loadData" />
    </div>
    </template>
  </div>
</template>

<style scoped>
.filter-card {
  margin-bottom: 16px;
}
.filter-row {
  display: flex;
  gap: 12px;
}
.card-list {
  min-height: 300px;
}
.card-list .el-col {
  margin-bottom: 16px;
}
.sk-card {
  margin-bottom: 16px;
  border-radius: 12px;
}
.pager {
  display: flex;
  justify-content: center;
  padding: 8px 0 20px;
}
</style>
