<!--
  CopyrightNote 版权备注（毕设合规要求）
  固定样式：12px / #999 / 居中 / 与上方内容留 16px；
  含图片素材的页面统一在内容底部挂载本组件（docs/11 第 3.3、docs/12 版权约束）。
-->
<script setup lang="ts">
withDefaults(
  defineProps<{
    /** 备注文案（默认免费图库合规文案） */
    text?: string
  }>(),
  {
    // 素材来自免费图库（Wikimedia Commons 等，自由授权），仅毕设本地演示
    text: '素材来源：免费图库（Wikimedia Commons 等），本作品仅用于毕业设计本地演示，非商用，不对外发布。'
  }
)
</script>

<template>
  <p class="gq-copyright-note">{{ text }}</p>
</template>
