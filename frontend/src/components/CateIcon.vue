<script setup>
/**
 * 分类国风线性图标：按分类名称返回对应的 24x24 线条 SVG（stroke 跟随 currentColor）
 * 覆盖平台六大分类；未匹配到时回退"卷轴"图形
 */
const ICONS = {
  传统音乐: [
    'M9 17V4l10-2v13',
    'M6.5 20a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z',
    'M16.5 18a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z'
  ],
  传统舞蹈: [
    'M5 21c3.5-7 7.5-9 9-16',
    'M9.5 21c3-5 8-6.5 10-11.5',
    'M4.5 6a2 2 0 1 0 0-4 2 2 0 0 0 0 4z'
  ],
  传统戏剧: [
    'M6 4h12v7a6 6 0 0 1-12 0z',
    'M9 9.5h.01M15 9.5h.01',
    'M10 13.5c1.2 1 2.8 1 4 0'
  ],
  传统技艺: [
    'M10 3h4',
    'M10 3c0 2.5-4 3.5-4 7v6.5a4.5 4.5 0 0 0 9 0V10c0-3.5-4-4.5-4-7',
    'M9.5 21h5'
  ],
  传统美术: [
    'M19.5 4.5 10 14',
    'M8.5 15.5c-2 1-3.2 3-3.5 5.5 2.5-.3 4.5-1.5 5.5-3.5z',
    'M19.5 4.5c-4.5.8-9.5 4.6-11.5 8'
  ],
  民俗: [
    'M8.5 6.5h7',
    'M12 6.5V4M10 4h4',
    'M12 6.5c3 0 5 2.4 5 5.5s-2 5.5-5 5.5-5-2.4-5-5.5 2-5.5 5-5.5z',
    'M12 17.5V20M10.5 21h3'
  ],
  默认: [
    'M5 4h11a3 3 0 0 1 3 3v13H8a3 3 0 0 1-3-3z',
    'M5 4a3 3 0 0 0-3 3v10a3 3 0 0 1 3-3',
    'M16 4v13'
  ]
}

const props = defineProps({
  /** 分类名称 */
  name: { type: String, required: true },
  size: { type: Number, default: 26 }
})

const paths = ICONS[props.name] || ICONS['默认']
</script>

<template>
  <svg :width="size" :height="size" viewBox="0 0 24 24" fill="none" stroke="currentColor"
       stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" class="cate-svg">
    <path v-for="(d, i) in paths" :key="i" :d="d" />
  </svg>
</template>

<style scoped>
.cate-svg {
  display: block;
}
</style>
