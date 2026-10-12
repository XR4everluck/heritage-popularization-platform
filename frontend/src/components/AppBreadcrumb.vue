<!--
  AppBreadcrumb 面包屑（docs/11 第 4.3 节）
  语义：nav[aria-label=面包屑] + ol；最后一项为当前页（不可点，aria-current=page）
-->
<script setup lang="ts">
defineProps<{
  /** 面包屑项：label 必填；to 缺省即为当前页 */
  items: Array<{ label: string; to?: string }>
}>()
</script>

<template>
  <nav class="app-breadcrumb" aria-label="面包屑">
    <ol>
      <li v-for="(item, index) in items" :key="`${item.label}-${index}`">
        <template v-if="item.to && index !== items.length - 1">
          <router-link :to="item.to" class="crumb-link">{{ item.label }}</router-link>
          <span class="crumb-sep" aria-hidden="true">/</span>
        </template>
        <template v-else>
          <span class="crumb-current" :aria-current="index === items.length - 1 ? 'page' : undefined">{{ item.label }}</span>
          <span v-if="index !== items.length - 1" class="crumb-sep" aria-hidden="true">/</span>
        </template>
      </li>
    </ol>
  </nav>
</template>

<style scoped>
.app-breadcrumb {
  margin-bottom: 16px;
}
.app-breadcrumb ol {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin: 0;
  padding: 0;
  list-style: none;
  font-size: 14px;
}
.app-breadcrumb li {
  display: flex;
  align-items: center;
  gap: 8px;
}
.crumb-link {
  color: #666666;
}
.crumb-link:hover {
  color: #9e2021;
}
.crumb-current {
  color: #999999;
}
.crumb-sep {
  color: #cccccc;
}
</style>
