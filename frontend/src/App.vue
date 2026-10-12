<script setup>
// 应用根组件：全站背景衬底层 + 路由出口
import { onMounted } from 'vue'
// 背景衬底：本地素材优先（bg.webp），未就位退回纯色底（docs/14 方案）
import { applyAppBackground } from './composables/useAppBackground'

onMounted(applyAppBackground)
</script>

<template>
  <!-- 背景衬底层：极低对比度纹样/纸纹，固定不随滚动，纯装饰（对读屏隐藏） -->
  <div class="app-bg" aria-hidden="true"></div>
  <router-view />
</template>
