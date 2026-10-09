<template>
  <div class="heritage-map-page">
    <el-page-header :icon="ArrowLeft" :title="pageTitle" @back="goBack" />

    <div class="page-header">
      <h1>非遗地图</h1>
      <p>探索中国各地的非遗文化分布</p>
    </div>

    <div class="map-container">
      <el-card shadow="never" class="map-card">
        <template #header>
          <div class="map-header">
            <span>全国非遗项目分布图</span>
            <el-tag type="info" size="small">点击省份查看该地区非遗</el-tag>
          </div>
        </template>

        <div class="map-content" v-loading="loading">
          <div ref="mapRef" class="map-chart"></div>
          <el-empty v-if="!loading && isEmpty" description="暂无已发布的非遗项目数据" />
        </div>
      </el-card>
    </div>

    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="12" :md="6">
          <el-card shadow="never" class="stat-card">
            <div class="stat-content">
              <div class="stat-num" :style="{ color: 'var(--gq-primary)' }">{{ totalHeritage }}</div>
              <div class="stat-label">非遗总数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-card shadow="never" class="stat-card">
            <div class="stat-content">
              <div class="stat-num" :style="{ color: 'var(--gq-gold)' }">{{ regionCount }}</div>
              <div class="stat-label">覆盖省份</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-card shadow="never" class="stat-card">
            <div class="stat-content">
              <div class="stat-num" :style="{ color: 'var(--gq-danger)' }">{{ maxRegionCount }}</div>
              <div class="stat-label">最多省份{{ maxRegionName ? '（' + maxRegionName + '）' : '' }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-card shadow="never" class="stat-card">
            <div class="stat-content">
              <div class="stat-num" :style="{ color: 'var(--gq-secondary)' }">{{ uncoveredCount }}</div>
              <div class="stat-label">未定位到省份</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <p v-if="uncoveredCount > 0" class="stat-hint">
        有 {{ uncoveredCount }} 个项目的所属地区为「全国」等非具体省份描述，已计入非遗总数但无法落到地图上。
      </p>
    </div>

    <div class="legend-container">
      <el-card shadow="never" class="legend-card">
        <template #header><b>图例说明</b></template>
        <div class="legend-content">
          <div class="legend-item" v-for="item in legendItems" :key="item.label">
            <div class="legend-color" :style="{ backgroundColor: item.color }"></div>
            <span>{{ item.label }}</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
/**
 * 非遗地图：ECharts 中国地图按省份着色展示非遗数量，点击省份跳转到对应地区的非遗列表。
 *
 * 数据库 heritage_info.region 是自由文本（如「江苏省苏州市」「江苏省扬州市、北京市等地」），
 * 而地图省份名是简称（「江苏」「内蒙古」），因此这里把地区文本归并到首个被提及的省份后再着色；
 * 点击省份携带的也是省份简称，与列表页的模糊匹配（LIKE %江苏%）天然对齐。
 */
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getRegionStats } from '../../api/front'
import * as echarts from 'echarts'
import chinaGeoJson from '../../assets/china-geojson.json'

const router = useRouter()
const mapRef = ref(null)
const chart = ref(null)

const loading = ref(false)
const totalHeritage = ref(0)
const regionCount = ref(0)
const maxRegionCount = ref(0)
const maxRegionName = ref('')
const uncoveredCount = ref(0)

const pageTitle = '非遗地图'

// 与 assets/china-geojson.json 中的省份名称保持完全一致
const PROVINCES = [
  '北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江', '上海', '江苏',
  '浙江', '安徽', '福建', '江西', '山东', '河南', '湖北', '湖南', '广东', '广西',
  '海南', '重庆', '四川', '贵州', '云南', '西藏', '陕西', '甘肃', '青海', '宁夏',
  '新疆', '台湾', '香港', '澳门'
]

// 国风色阶：宣纸米白 → 朱红
const MAP_COLORS = ['#f7f4ee', '#f3d5d2', '#ecbcb7', '#e08d85', '#d35d52', '#c0392b']

const legendItems = [
  { color: '#f3d5d2', label: '较少' },
  { color: '#ecbcb7', label: '偏少' },
  { color: '#e08d85', label: '中等' },
  { color: '#d35d52', label: '偏多' },
  { color: '#c0392b', label: '最多' }
]

// 省份着色数据：{ 江苏: 5, 北京: 2, ... }
const provinceStats = ref({})
const isEmpty = computed(() => Object.keys(provinceStats.value).length === 0)

onMounted(async () => {
  echarts.registerMap('china', chinaGeoJson)
  window.addEventListener('resize', handleResize)
  await loadRegionStats()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart.value?.dispose()
  chart.value = null
})

function handleResize() {
  chart.value?.resize()
}

async function loadRegionStats() {
  loading.value = true
  try {
    const res = await getRegionStats()
    applyStats(res.data || {})
    await nextTick()
    renderChart()
  } catch (error) {
    console.error('加载地区统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

/** 把自由文本地区归并到省份，并计算统计卡片数据 */
function applyStats(rawStats) {
  const merged = {}
  let uncovered = 0
  let total = 0

  Object.entries(rawStats).forEach(([region, count]) => {
    const value = Number(count) || 0
    total += value
    const province = toProvince(region)
    if (province) {
      merged[province] = (merged[province] || 0) + value
    } else {
      uncovered += value
    }
  })

  provinceStats.value = merged
  totalHeritage.value = total
  uncoveredCount.value = uncovered
  regionCount.value = Object.keys(merged).length

  const entries = Object.entries(merged)
  if (entries.length > 0) {
    const [name, value] = entries.reduce((max, cur) => (cur[1] > max[1] ? cur : max))
    maxRegionName.value = name
    maxRegionCount.value = value
  } else {
    maxRegionName.value = ''
    maxRegionCount.value = 0
  }
}

/** 取地区文本中最早出现的省份；「江苏省扬州市、北京市等地」→ 江苏；「全国各地」→ null */
function toProvince(region) {
  if (!region) return null
  let matched = null
  let matchedIndex = Number.MAX_SAFE_INTEGER
  for (const province of PROVINCES) {
    const index = region.indexOf(province)
    if (index !== -1 && index < matchedIndex) {
      matched = province
      matchedIndex = index
    }
  }
  return matched
}

function renderChart() {
  if (!mapRef.value) return
  if (chart.value) {
    chart.value.dispose()
    chart.value = null
  }
  if (isEmpty.value) return

  chart.value = echarts.init(mapRef.value)

  const mapData = Object.entries(provinceStats.value).map(([name, value]) => ({ name, value }))
  const maxValue = Math.max(...mapData.map((item) => item.value), 1)

  chart.value.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        const value = params.value == null || Number.isNaN(params.value) ? 0 : params.value
        return `${params.name}<br/>非遗项目数量：${value}`
      }
    },
    visualMap: {
      min: 0,
      max: maxValue,
      left: 'left',
      bottom: '10',
      text: ['多', '少'],
      calculable: false,
      itemWidth: 12,
      itemHeight: 100,
      textStyle: { color: '#8a8578' },
      inRange: { color: MAP_COLORS }
    },
    series: [
      {
        name: '非遗项目数量',
        type: 'map',
        map: 'china',
        roam: true,
        scaleLimit: { min: 0.8, max: 4 },
        data: mapData,
        itemStyle: {
          borderColor: '#c9bda6',
          borderWidth: 0.6
        },
        emphasis: {
          label: { show: true, fontSize: 12, fontWeight: 'bold', color: '#2c3e50' },
          itemStyle: { areaColor: '#d4af37', borderColor: '#a02c20' }
        },
        select: {
          label: { show: true, color: '#2c3e50' },
          itemStyle: { areaColor: '#d4af37' }
        },
        label: { show: false }
      }
    ]
  })

  chart.value.on('click', (params) => {
    if (!params.name || !provinceStats.value[params.name]) {
      return
    }
    router.push({ path: '/heritage', query: { region: params.name } })
  })
}

function goBack() {
  router.push('/')
}
</script>

<style scoped>
.heritage-map-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin: 24px 0 30px;
}

.page-header h1 {
  font-family: var(--font-heading);
  font-size: 28px;
  color: var(--gq-primary);
  margin-bottom: 10px;
}

.page-header p {
  color: #8a8578;
  font-size: 16px;
}

.map-container {
  margin-bottom: 30px;
}

.map-card {
  height: 640px;
}

.map-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.map-content {
  height: 560px;
  width: 100%;
  position: relative;
}

.map-chart {
  height: 100%;
  width: 100%;
}

.stats-container {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  height: 120px;
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.stat-num {
  font-family: var(--font-heading);
  font-size: 32px;
  font-weight: 700;
  line-height: 1.3;
  margin-bottom: 8px;
}

.stat-label {
  color: #8a8578;
  font-size: 14px;
}

.stat-hint {
  margin: 12px 2px 0;
  font-size: 13px;
  color: #a89f8c;
}

.legend-container {
  margin-bottom: 30px;
}

.legend-card {
  text-align: center;
}

.legend-content {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.legend-item span {
  font-size: 14px;
  color: #8a8578;
}
</style>
