<!--
  AppPagination 分页（docs/11 第 4.2 节）
  包装 el-pagination 固定规格：background 模式、总数 + 上下页 + 页码 + 跳页；
  页码数字走 Times New Roman（全局字体栈自动命中）。
-->
<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    /** 总条数 */
    total: number
    /** 当前页（v-model:page） */
    page?: number
    /** 每页条数 */
    pageSize?: number
  }>(),
  { page: 1, pageSize: 10 }
)

const emit = defineEmits<{ (e: 'update:page', page: number): void; (e: 'change', page: number): void }>()

function onChange(next: number) {
  emit('update:page', next)
  emit('change', next)
}
</script>

<template>
  <div class="app-pagination">
    <el-pagination
      :current-page="props.page"
      :page-size="props.pageSize"
      :total="props.total"
      layout="total, prev, pager, next, jumper"
      background
      @current-change="onChange"
    />
  </div>
</template>

<style scoped>
.app-pagination {
  display: flex;
  justify-content: center;
  padding: 16px 0 24px;
}
</style>
