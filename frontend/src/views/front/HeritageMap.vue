<template>
  <div class="heritage-map-page">
    <!-- 沉浸式地图区（样式对标 ihchina #page5：绚丽山河背景 + 白字居中标题 + 中国地图） -->
    <section class="map-hero" role="region" aria-label="非物质文化遗产地图（各省份非遗数量以地图颜色深浅与悬浮提示呈现）">
      <!-- 背景图槽位：视觉中国素材 map-bg.webp 优先，内置原创山河风景兜底 -->
      <div class="map-hero-bg" :style="heroBgStyle" aria-hidden="true"></div>

      <h1 class="map-hero-title">非物质文化遗产地图</h1>
      <p class="map-hero-sub">点击省份，查看该地区非遗项目列表</p>

      <div class="map-chart-wrap" v-loading="loading">
        <div ref="mapRef" class="map-chart"></div>
      </div>
    </section>
  </div>
</template>

<script setup>
/**
 * 非遗地图（docs/16 改版）：样式对标中国非物质文化遗产网 #page5
 * - 绚丽山河背景（本地素材槽位 map-bg.webp 优先，内置原创风景兜底）+ 白字居中标题
 * - ECharts 中国地图（暗金色阶），点击省份跳转列表页地区筛选
 * - 数据全部来自 mock 点位（src/mock/map-points.ts），未接真实接口
 * - 版式精简：仅保留标题与地图，不含统计面板
 */
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getRegionStats } from '../../api/front'
import * as echarts from 'echarts'
import chinaGeoJson from '../../assets/china-geojson.json'

const router = useRouter()
const mapRef = ref(null)
const chart = ref(null)

const loading = ref(false)

// 省份着色数据：{ 江苏: 5, 北京: 2, ... }
const provinceStats = ref({})
const isEmpty = computed(() => Object.keys(provinceStats.value).length === 0)

// ---------- 背景图槽位（视觉中国素材 map-bg.webp 优先，内置原创山河风景兜底） ----------
const bgModules = import.meta.glob('../../assets/intangible-images/bg/map-bg.*', { import: 'default', eager: true })
const heroBgStyle = computed(() => {
  const entries = Object.entries(bgModules)
  if (!entries.length) {
    return { background: 'linear-gradient(rgba(18, 13, 9, 0.3), rgba(18, 13, 9, 0.3)), #241c16' }
  }
  // 优先真实素材位图（webp/jpg/png），演示风景（svg）兜底
  const byPriority = (re) => entries.find(([path]) => re.test(path))
  const hit = byPriority(/map-bg\.webp$/) ?? byPriority(/map-bg\.jpe?g$/) ?? byPriority(/map-bg\.png$/) ?? entries[0]
  return { backgroundImage: `linear-gradient(rgba(18, 13, 9, 0.3), rgba(18, 13, 9, 0.3)), url("${hit[1]}")` }
})

// 与 assets/china-geojson.json 中的省份名称保持完全一致
const PROVINCES = [
  '北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江', '上海', '江苏',
  '浙江', '安徽', '福建', '江西', '山东', '河南', '湖北', '湖南', '广东', '广西',
  '海南', '重庆', '四川', '贵州', '云南', '西藏', '陕西', '甘肃', '青海', '宁夏',
  '新疆', '台湾', '香港', '澳门'
]

// 暗金色阶（对标 #page5 的古金地图观感）
const MAP_COLORS = ['#4a3a2c', '#6b563d', '#8a7454', '#b09a6e', '#d9c9a3']

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

/** 把自由文本地区归并到省份（「江苏省苏州市」→ 江苏；「全国各地」等不参与着色） */
function applyStats(rawStats) {
  const merged = {}

  Object.entries(rawStats).forEach(([region, count]) => {
    const value = Number(count) || 0
    const province = toProvince(region)
    if (province) {
      merged[province] = (merged[province] || 0) + value
    }
  })

  provinceStats.value = merged
}

/** 取地区文本中最早出现的省份 */
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
      show: false,
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
          // 无数据省份的默认底色（暗色基调，与古金观感一致）
          areaColor: 'rgba(58, 47, 38, 0.82)',
          borderColor: '#8a7454',
          borderWidth: 0.8
        },
        emphasis: {
          label: { show: true, fontSize: 12, fontWeight: 'bold', color: '#241c16' },
          itemStyle: { areaColor: '#d9c9a3', borderColor: '#d9c9a3' }
        },
        select: {
          label: { show: true, color: '#241c16' },
          itemStyle: { areaColor: '#d9c9a3' }
        },
        label: { show: true, color: '#e8dcc0', fontSize: 10 }
      }
    ]
  })

  chart.value.on('click', (params) => {
    if (!params.name || !provinceStats.value[params.name]) {
      return
    }
    goRegion(params.name)
  })
}

function goRegion(region) {
  router.push({ path: '/heritage', query: region ? { region } : {} })
}
</script>

<style scoped>
.heritage-map-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* ---------- 沉浸式地图区 ---------- */
.map-hero {
  position: relative;
  border-radius: 4px;
  overflow: hidden;
  padding: 48px 32px 28px;
  display: flex;
  flex-direction: column;
}
/* 背景层：图片由 heroBgStyle 提供（含轻度暗化），这里只兜底底色 */
.map-hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
  background-size: cover;
  background-position: center;
}
.map-hero-title {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin: 0 0 10px;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 4px;
  color: #f3ead6;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}
/* 标题两侧对称菱形纹样 */
.map-hero-title::before,
.map-hero-title::after {
  content: '';
  width: 8px;
  height: 8px;
  border: 1px solid #d9c9a3;
  transform: rotate(45deg);
}
.map-hero-sub {
  position: relative;
  z-index: 1;
  text-align: center;
  margin: 0 0 24px;
  font-size: 14px;
  letter-spacing: 1px;
  color: rgba(232, 220, 192, 0.85);
}

.map-chart-wrap {
  position: relative;
  z-index: 1;
}
.map-chart {
  height: 580px;
  width: 100%;
}

@media (max-width: 768px) {
  .map-hero {
    padding: 28px 12px 16px;
  }
  .map-hero-title {
    font-size: 20px;
    letter-spacing: 2px;
  }
  .map-chart {
    height: 340px;
  }
}
</style>
