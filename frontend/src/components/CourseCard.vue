<script setup>
/**
 * 统一课程卡片（全站复用）：与非遗卡片同规格（圆角 12px、封面 160px、hover 上浮+阴影）；
 * 底部展示课程名 + 讲师 + 时长/浏览量 meta，时长超过 60 分钟换算为"小时+分钟"
 */
import { defaultCover } from '../utils/placeholder'

defineProps({
  /** 课程数据：name/cover/teacher/duration/viewCount，可选 chapterCount */
  item: { type: Object, required: true },
  /** 额外标签（如视频教学/图文课程标识）：{ text, type } */
  tag: { type: Object, default: null }
})
const emit = defineEmits(['open'])

function formatDuration(minutes) {
  if (!minutes) return '暂无时长'
  if (minutes < 60) return `${minutes} 分钟`
  return `${Math.floor(minutes / 60)} 小时 ${minutes % 60} 分钟`
}
</script>

<template>
  <el-card shadow="hover" class="gq-card" :body-style="{ padding: 0 }" @click="emit('open')">
    <div class="gq-card-cover-wrap">
      <img :src="item.cover || defaultCover(item.name)" class="gq-card-cover"
           @error="$event.target.src = defaultCover(item.name)" />
      <span class="gq-card-duration"><el-icon><VideoCamera /></el-icon>{{ formatDuration(item.duration) }}</span>
    </div>
    <div class="gq-card-body">
      <div class="gq-card-name" :title="item.name">{{ item.name }}</div>
      <div class="gq-card-tags">
        <el-tag v-if="tag" size="small" :type="tag.type || 'success'" effect="light">{{ tag.text }}</el-tag>
        <el-tag v-if="item.teacher" size="small" type="warning">{{ item.teacher }}</el-tag>
      </div>
      <div class="gq-card-meta">
        <span v-if="item.chapterCount" class="gq-card-region">共 {{ item.chapterCount }} 节</span>
        <span v-else class="gq-card-region">上线于 {{ (item.publishTime || '').slice(0, 10) || '-' }}</span>
        <span><el-icon><View /></el-icon>{{ item.viewCount }}</span>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.gq-card {
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}
.gq-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 24px rgba(44, 62, 80, 0.14);
}
.gq-card-cover-wrap {
  position: relative;
}
.gq-card-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  display: block;
}
.gq-card-duration {
  position: absolute;
  right: 8px;
  bottom: 8px;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 3px;
}
.gq-card-body {
  padding: 12px;
}
.gq-card-name {
  font-weight: 700;
  font-size: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.gq-card-tags {
  margin: 8px 0;
  display: flex;
  gap: 6px;
  min-height: 20px;
}
.gq-card-meta {
  color: #909399;
  font-size: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.gq-card-meta span {
  display: flex;
  align-items: center;
  gap: 2px;
}
</style>
