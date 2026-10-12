<script setup>
/**
 * 统一非遗卡片（全站复用，docs/11 第 5.2 节）
 * 纯白卡片 + 细边框 + 唯一档阴影；封面固定 160px（width/height 占位防 CLS）；
 * 悬停仅允许边框/文字变色，不做位移与阴影增强。
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
    <!-- width/height 属性占位防布局偏移（CLS）；占位图尺寸 600x360 -->
    <img :src="item.coverImage || defaultCover(item.name)" :alt="item.name || '非遗项目封面'"
         width="600" height="360" class="gq-card-cover"
         @error="$event.target.src = defaultCover(item.name)" />
    <div class="gq-card-body">
      <div class="gq-card-name" :title="item.name">{{ item.name }}</div>
      <div class="gq-card-tags">
        <el-tag size="small" :type="item.level === '国家级' ? 'danger' : 'info'">{{ item.level }}</el-tag>
        <el-tag v-if="item.categoryName" size="small" type="info" effect="plain">{{ item.categoryName }}</el-tag>
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
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: none;
}
/* 悬停仅变边框色（docs/11 第 2 节：不做位移/阴影增强） */
.gq-card:hover {
  border-color: var(--gq-primary);
}
.gq-card:hover .gq-card-name {
  color: var(--gq-primary);
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
  font-size: 16px;
  color: var(--gq-text);
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
  color: var(--gq-text-muted);
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
