<script setup>
/**
 * 历史发展时间轴：按传入节点顺序垂直展示（nodes 已按年代排序），
 * 朱红节点圆点 + 黛青年代标题，呼应国风主题
 */
defineProps({
  /** 历史节点数组：[{ id, year, event, description }] */
  nodes: { type: Array, default: () => [] }
})
</script>

<template>
  <div class="htimeline" v-if="nodes.length">
    <div class="htimeline-item" v-for="node in nodes" :key="node.id">
      <div class="htimeline-dot"></div>
      <div class="htimeline-year">{{ node.year }}</div>
      <div class="htimeline-body">
        <div class="htimeline-event">{{ node.event }}</div>
        <div class="htimeline-desc" v-if="node.description">{{ node.description }}</div>
      </div>
    </div>
  </div>
  <el-empty v-else description="暂无历史节点" :image-size="60" />
</template>

<style scoped>
.htimeline {
  position: relative;
  padding-left: 20px;
}
/* 纵向时间线 */
.htimeline::before {
  content: '';
  position: absolute;
  left: 5px;
  top: 6px;
  bottom: 6px;
  width: 2px;
  background: var(--gq-border);
}
.htimeline-item {
  position: relative;
  display: flex;
  gap: 14px;
  padding: 0 0 20px 14px;
}
.htimeline-item:last-child {
  padding-bottom: 4px;
}
/* 节点圆点 */
.htimeline-dot {
  position: absolute;
  left: -20px;
  top: 5px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: var(--gq-card);
  border: 3px solid var(--gq-primary);
  box-shadow: 0 0 0 3px rgba(192, 57, 43, 0.12);
}
.htimeline-year {
  flex-shrink: 0;
  width: 110px;
  font-family: var(--font-heading);
  font-weight: 700;
  color: var(--gq-secondary);
  font-size: 15px;
  line-height: 1.5;
}
.htimeline-body {
  flex: 1;
  min-width: 0;
}
.htimeline-event {
  font-weight: 700;
  color: var(--gq-text);
  font-size: 14px;
}
.htimeline-desc {
  margin-top: 3px;
  color: var(--gq-text-secondary);
  font-size: 13px;
  line-height: 1.6;
}
@media (max-width: 640px) {
  .htimeline-item {
    flex-direction: column;
    gap: 2px;
  }
}
</style>
