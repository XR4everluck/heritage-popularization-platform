<script setup>
/**
 * 国风淡入淡出轮播图：全宽图 + 底部渐变遮罩 + 大字主标题/副标题叠加层；
 * title 中若含 ：／，／—— 分隔符则自动拆分为主/副标题，否则副标题用平台口号兜底
 */
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { coverFallback } from '../utils/placeholder'

const props = defineProps({
  banners: { type: Array, default: () => [] },
  /** 点击轮播图（透传给父级处理跳转） */
  open: { type: Function, default: null }
})

const active = ref(0)
let timer = null

/** 拆分主/副标题 */
function splitTitle(title = '') {
  const m = title.split(/[:：]|——|，/)
  if (m.length >= 2) {
    return { main: m[0].trim(), sub: m.slice(1).join('，').trim() }
  }
  return { main: title.trim(), sub: '非遗知识科普平台 · 传承中华优秀传统文化' }
}

function startTimer() {
  stopTimer()
  timer = setInterval(() => {
    if (props.banners.length) active.value = (active.value + 1) % props.banners.length
  }, 4500)
}
function stopTimer() {
  if (timer) clearInterval(timer)
  timer = null
}
function go(index) {
  active.value = index
  startTimer()
}
function click(banner) {
  props.open?.(banner)
}

onMounted(startTimer)
onBeforeUnmount(stopTimer)
</script>

<template>
  <div class="fade-carousel" v-if="banners.length" @mouseenter="stopTimer" @mouseleave="startTimer">
    <transition-group name="banner-fade">
      <div v-for="(banner, index) in banners" v-show="index === active" :key="banner.id" class="fade-item">
        <img :src="banner.image" class="fade-img"
             @error="coverFallback($event, banner.title || '非遗之美')" @click="click(banner)" />
        <!-- 大字标题叠加层：主标题 + 副标题 -->
        <div class="fade-overlay">
          <h2 class="fade-main" @click="click(banner)">{{ splitTitle(banner.title).main }}</h2>
          <p class="fade-sub" @click="click(banner)">{{ splitTitle(banner.title).sub }}</p>
        </div>
      </div>
    </transition-group>
    <!-- 左右切换箭头 -->
    <button class="fade-arrow left" @click="go((active - 1 + banners.length) % banners.length)">‹</button>
    <button class="fade-arrow right" @click="go((active + 1) % banners.length)">›</button>
    <!-- 指示点 -->
    <div class="fade-dots">
      <span v-for="(banner, index) in banners" :key="banner.id" class="fade-dot"
            :class="{ active: index === active }" @click="go(index)"></span>
    </div>
  </div>
</template>

<style scoped>
.fade-carousel {
  position: relative;
  height: 380px;
  border-radius: 12px;
  overflow: hidden;
  background: var(--gq-secondary);
}
.fade-item {
  position: absolute;
  inset: 0;
}
.fade-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
}
/* 底部渐变遮罩 + 大字标题 */
.fade-overlay {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 60px 40px 30px;
  background: linear-gradient(180deg, transparent, rgba(26, 26, 26, 0.72));
  color: #fff;
  pointer-events: none;
}
.fade-main {
  font-family: var(--font-heading);
  font-size: 34px;
  font-weight: 700;
  letter-spacing: 3px;
  text-shadow: 0 3px 10px rgba(0, 0, 0, 0.55);
  cursor: pointer;
  pointer-events: auto;
}
.fade-sub {
  margin-top: 8px;
  font-size: 15px;
  letter-spacing: 1px;
  opacity: 0.88;
  cursor: pointer;
  pointer-events: auto;
}
/* 淡入淡出动画 */
.banner-fade-enter-active,
.banner-fade-leave-active {
  transition: opacity 0.9s ease;
}
.banner-fade-enter-from,
.banner-fade-leave-to {
  opacity: 0;
}
.banner-fade-leave-active {
  position: absolute;
  inset: 0;
}
/* 左右箭头 */
.fade-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 56px;
  border: none;
  background: rgba(26, 26, 26, 0.35);
  color: #fff;
  font-size: 26px;
  line-height: 1;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.25s ease, background 0.25s ease;
  z-index: 5;
}
.fade-arrow.left { left: 0; border-radius: 0 8px 8px 0; }
.fade-arrow.right { right: 0; border-radius: 8px 0 0 8px; }
.fade-carousel:hover .fade-arrow {
  opacity: 1;
}
.fade-arrow:hover {
  background: rgba(192, 57, 43, 0.8);
}
/* 指示点 */
.fade-dots {
  position: absolute;
  right: 24px;
  bottom: 18px;
  display: flex;
  gap: 8px;
  z-index: 5;
}
.fade-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.45);
  cursor: pointer;
  transition: all 0.25s ease;
}
.fade-dot.active {
  background: var(--gq-primary);
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.6);
}
@media (max-width: 640px) {
  .fade-carousel { height: 220px; }
  .fade-main { font-size: 22px; letter-spacing: 1px; }
  .fade-sub { font-size: 12px; }
  .fade-overlay { padding: 30px 20px 16px; }
}
</style>
