<script setup>
/**
 * 首页轮播图（docs/11 第 5.1 节）
 * 自动切换（水平淡入淡出，功能性动效，≥4.5s）；图上左下角白底标题条（主色竖线 + #333 标题）；
 * 支持左右箭头、指示器、悬停暂停；图片 alt 必填。
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
  }, 5000)
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
  <div
    class="fade-carousel"
    v-if="banners.length"
    role="region"
    aria-roledescription="轮播"
    aria-label="推荐 banner"
    @mouseenter="stopTimer"
    @mouseleave="startTimer"
  >
    <transition-group name="banner-fade">
      <div v-for="(banner, index) in banners" v-show="index === active" :key="banner.id" class="fade-item">
        <img :src="banner.image" :alt="banner.title || '非遗宣传图'" class="fade-img"
             @error="coverFallback($event, banner.title || '非遗之美')" @click="click(banner)" />
        <!-- 左下角标题条：白底 85% 不透明 + 主色竖线 + 重文本标题（docs/11 第 5.1 节） -->
        <div class="fade-overlay">
          <h2 class="fade-main" @click="click(banner)">{{ splitTitle(banner.title).main }}</h2>
          <p class="fade-sub" @click="click(banner)">{{ splitTitle(banner.title).sub }}</p>
        </div>
      </div>
    </transition-group>
    <!-- 左右切换箭头（纯图标按钮，必须带 aria-label） -->
    <button class="fade-arrow left" aria-label="上一张 banner" @click="go((active - 1 + banners.length) % banners.length)">‹</button>
    <button class="fade-arrow right" aria-label="下一张 banner" @click="go((active + 1) % banners.length)">›</button>
    <!-- 指示器：短横线，激活态主色 -->
    <div class="fade-dots">
      <button v-for="(banner, index) in banners" :key="banner.id" class="fade-dot"
              :class="{ active: index === active }" :aria-label="`切换到第 ${index + 1} 张 banner`"
              :aria-current="index === active ? 'true' : undefined" @click="go(index)"></button>
    </div>
  </div>
</template>

<style scoped>
.fade-carousel {
  position: relative;
  height: 420px;
  border-radius: 4px;
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
/* 左下角标题条：白底 85% 不透明（docs/11 第 5.1 节，不做全宽渐变遮罩） */
.fade-overlay {
  position: absolute;
  left: 24px;
  bottom: 24px;
  max-width: 70%;
  padding: 14px 20px 14px 18px;
  background: rgba(255, 255, 255, 0.85);
  border-left: 4px solid var(--gq-primary);
  color: var(--gq-text);
  pointer-events: none;
}
.fade-main {
  font-family: var(--font-heading);
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 1px;
  cursor: pointer;
  pointer-events: auto;
}
.fade-sub {
  margin-top: 6px;
  font-size: 14px;
  letter-spacing: 1px;
  color: var(--gq-text-secondary);
  cursor: pointer;
  pointer-events: auto;
}
/* 切换过渡：轮播自动切换属功能性动效（docs/11 第 2 节允许项） */
.banner-fade-enter-active,
.banner-fade-leave-active {
  transition: opacity 0.6s ease;
}
.banner-fade-enter-from,
.banner-fade-leave-to {
  opacity: 0;
}
.banner-fade-leave-active {
  position: absolute;
  inset: 0;
}
/* 左右箭头：仅允许 hover 变色 */
.fade-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 56px;
  border: none;
  background: rgba(51, 51, 51, 0.45);
  color: #fff;
  font-size: 26px;
  line-height: 1;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s ease;
  z-index: 5;
}
.fade-arrow.left { left: 0; border-radius: 2px 0 0 2px; }
.fade-arrow.right { right: 0; border-radius: 0 2px 2px 0; }
.fade-carousel:hover .fade-arrow,
.fade-arrow:focus-visible {
  opacity: 1;
}
.fade-arrow:hover {
  background: var(--gq-primary);
}
/* 指示器：短横线 */
.fade-dots {
  position: absolute;
  right: 24px;
  bottom: 18px;
  display: flex;
  gap: 8px;
  z-index: 5;
}
.fade-dot {
  width: 20px;
  height: 3px;
  padding: 0;
  border: none;
  border-radius: 0;
  background: rgba(255, 255, 255, 0.55);
  cursor: pointer;
}
.fade-dot.active {
  background: var(--gq-primary);
}
@media (max-width: 768px) {
  .fade-carousel { height: 220px; }
  .fade-main { font-size: 18px; letter-spacing: 0; }
  .fade-sub { font-size: 12px; }
  .fade-overlay { left: 12px; bottom: 12px; padding: 10px 12px; border-left-width: 3px; max-width: 85%; }
  .fade-arrow { display: none; }
}
</style>
