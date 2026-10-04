<script setup>
/**
 * 非遗列表页：分类/级别筛选 + 关键词搜索 + 卡片列表 + 分页
 */
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategoryList, getHeritagePage } from '../../api/front'
import { defaultCover } from '../../utils/placeholder'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = ref({
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : null,
  level: null,
  keyword: '',
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

async function loadData() {
  loading.value = true
  try {
    const res = await getHeritagePage(query.value)
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

function search() {
  query.value.page = 1
  loadData()
}
</script>

<template>
  <div class="list-page" v-loading="loading">
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

    <!-- 卡片列表 -->
    <el-empty v-if="!list.length && !loading" description="暂无相关非遗项目" />
    <el-row :gutter="16" class="card-list">
      <el-col v-for="item in list" :key="item.id" :span="6">
        <el-card shadow="hover" class="heritage-card" :body-style="{ padding: 0 }"
                 @click="router.push(`/heritage/${item.id}`)">
          <img :src="item.coverImage || defaultCover(item.name)" class="card-cover" />
          <div class="card-body">
            <div class="card-name">{{ item.name }}</div>
            <div class="card-tags">
              <el-tag size="small" :type="item.level === '国家级' ? 'danger' : 'warning'">{{ item.level }}</el-tag>
              <el-tag size="small" type="info">{{ item.categoryName }}</el-tag>
            </div>
            <div class="card-summary">{{ item.summary }}</div>
            <div class="card-meta">
              <span>{{ item.region }}</span>
              <span><el-icon><View /></el-icon>{{ item.viewCount }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分页 -->
    <div class="pager">
      <el-pagination v-model:current-page="query.page" :page-size="query.pageSize" :total="total"
                     layout="prev, pager, next, total" background @current-change="loadData" />
    </div>
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
  font-weight: 700;
  font-size: 15px;
}
.card-tags {
  margin: 8px 0;
  display: flex;
  gap: 6px;
}
.card-summary {
  color: #909399;
  font-size: 12px;
  height: 36px;
  line-height: 18px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.card-meta {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  display: flex;
  justify-content: space-between;
}
.card-meta span {
  display: flex;
  align-items: center;
  gap: 2px;
}
.pager {
  display: flex;
  justify-content: center;
  padding: 8px 0 20px;
}
</style>
