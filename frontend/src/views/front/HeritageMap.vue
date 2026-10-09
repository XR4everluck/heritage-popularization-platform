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
            <el-tag type="info" size="small">点击省份查看详情</el-tag>
          </div>
        </template>
        
        <div class="map-content">
          <div ref="mapRef" class="map-chart"></div>
        </div>
      </el-card>
    </div>

    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="never" class="stat-card">
            <div class="stat-content">
              <div class="stat-num" :style="{ color: 'var(--gq-primary)' }">{{ totalHeritage }}</div>
              <div class="stat-label">非遗总数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="never" class="stat-card">
            <div class="stat-content">
              <div class="stat-num" :style="{ color: 'var(--gq-gold)' }">{{ regionCount }}</div>
              <div class="stat-label">覆盖地区</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="never" class="stat-card">
            <div class="stat-content">
              <div class="stat-num" :style="{ color: 'var(--gq-danger)' }">{{ maxRegionCount }}</div>
              <div class="stat-label">最多地区</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="never" class="stat-card">
            <div class="stat-content">
              <div class="stat-num" :style="{ color: 'var(--gq-secondary)' }">{{ avgRegionCount }}</div>
              <div class="stat-label">平均数量</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="legend-container">
      <el-card shadow="never" class="legend-card">
        <template #header><b>图例说明</b></template>
        <div class="legend-content">
          <div class="legend-item">
            <div class="legend-color" :style="{ backgroundColor: '#e0f2fe' }"></div>
            <span>0-10个</span>
          </div>
          <div class="legend-item">
            <div class="legend-color" :style="{ backgroundColor: '#7dd3fc' }"></div>
            <span>11-30个</span>
          </div>
          <div class="legend-item">
            <div class="legend-color" :style="{ backgroundColor: '#0ea5e9' }"></div>
            <span>31-50个</span>
          </div>
          <div class="legend-item">
            <div class="legend-color" :style="{ backgroundColor: '#0284c7' }"></div>
            <span>51-100个</span>
          </div>
          <div class="legend-item">
            <div class="legend-color" :style="{ backgroundColor: '#075985' }"></div>
            <span>100+个</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getRegionStats, getHeritagePage } from '../../api/front'
import * as echarts from 'echarts'
import 'echarts/map/js/china'

const router = useRouter()
const mapRef = ref(null)
const chart = ref(null)
const regionStats = ref({})
const heritageList = ref([])

const pageTitle = '非遗地图'
const totalHeritage = ref(0)
const regionCount = ref(0)
const maxRegionCount = ref(0)
const avgRegionCount = ref(0)

onMounted(() => {
  loadRegionStats()
})

async function loadRegionStats() {
  try {
    const res = await getRegionStats()
    regionStats.value = res.data || {}
    calculateStats()
    await nextTick()
    initChart()
  } catch (error) {
    console.error('加载地区统计数据失败:', error)
  }
}

function calculateStats() {
  const stats = regionStats.value
  const counts = Object.values(stats)
  
  totalHeritage.value = counts.reduce((sum, count) => sum + count, 0)
  regionCount.value = Object.keys(stats).length
  maxRegionCount.value = counts.length > 0 ? Math.max(...counts) : 0
  avgRegionCount.value = regionCount.value > 0 ? Math.round(totalHeritage.value / regionCount.value) : 0
}

function initChart() {
  if (!mapRef.value) return
  
  chart.value = echarts.init(mapRef.value)
  
  // 准备地图数据
  const mapData = Object.entries(regionStats.value).map(([name, value]) => ({
    name,
    value
  }))
  
  // 地图配置
  const option = {
    backgroundColor: '#fff',
    title: {
      text: '',
      left: 'center',
      top: '10px'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>非遗项目数量: {c}'
    },
    visualMap: {
      min: 0,
      max: Math.max(...Object.values(regionStats.value), 100),
      left: 'left',
      top: 'bottom',
      text: ['高', '低'],
      calculable: true,
      inRange: {
        color: ['#e0f2fe', '#7dd3fc', '#0ea5e9', '#0284c7', '#075985']
      }
    },
    series: [{
      name: '非遗项目数量',
      type: 'map',
      map: 'china',
      roam: true,
      emphasis: {
        label: {
          show: true,
          fontSize: 12,
          fontWeight: 'bold'
        },
        itemStyle: {
          areaColor: '#389e0d'
        }
      },
      data: mapData,
      label: {
        show: true,
        fontSize: 10,
        color: '#333'
      }
    }]
  }
  
  chart.value.setOption(option)
  
  // 添加点击事件
  chart.value.on('click', params => {
    const regionName = params.name
    router.push({
      path: '/heritage',
      query: { region: regionName }
    })
  })
  
  // 响应式调整
  window.addEventListener('resize', () => {
    chart.value?.resize()
  })
}

function goBack() {
  router.push('/')
}

// 组件卸载时清理
onUnmounted(() => {
  chart.value?.dispose()
  window.removeEventListener('resize', () => {
    chart.value?.resize()
  })
})
</script>

<style scoped>
.heritage-map-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: var(--gq-primary);
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

.map-container {
  margin-bottom: 30px;
}

.map-card {
  height: 600px;
}

.map-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.map-content {
  height: 550px;
  width: 100%;
}

.map-chart {
  height: 100%;
  width: 100%;
}

.stats-container {
  margin-bottom: 30px;
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
  color: #666;
}
</style>