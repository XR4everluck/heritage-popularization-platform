<script setup>
/**
 * 统一非遗卡片（全站复用）：封面固定 160px、圆角 12px、hover 上浮+阴影过渡；
 * 底部统一展示名称 + 级别/分类标签 + 地区/浏览量 meta
 */
import { defaultCover } from '../utils/placeholder'

defineProps({
  /** 非遗项目数据：name/level/categoryName/region/viewCount/coverImage */
  item: { type: Object, required: true }
})
const emit = defineEmits(['open'])
</script>

<template>
  <el-card shadow="hover" class="gq-card" :body-style="{ padding: 0 }" @click="emit('open')">
    <img :src="item.coverImage || defaultCover(item.name)" class="gq-card-cover"
         @error="$event.target.src = defaultCover(item.name)" />
    <div class="gq-card-body">
      <div class="gq-card-name" :title="item.name">{{ item.name }}</div>
      <div class="gq-card-tags">
        <el-tag size="small" :type="item.level === '国家级' ? 'danger' : 'warning'">{{ item.level }}</el-tag>
        <el-tag v-if="item.categoryName" size="small" type="info">{{ item.categoryName }}</el-tag>
      </div>
      <div class="gq-card-meta">
        <span class="gq-card-region" :title="item.region">{{ item.region || '-' }}</span>
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
.gq-card-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  display: block;
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
.gq-card-region {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
