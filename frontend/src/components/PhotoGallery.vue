<!--
  PhotoGallery 图片图集组件（图文详情 / 传承人详情通用）
  规范依据：docs/11 全局设计规范 第 4.1 节
  数据来源：mock 阶段传入 src/mock/heritage-images.ts 的清单；组件不感知数据来源
  素材约束：仅本地 webp（v-lazy 指令拦截外链），底部固定版权备注
-->
<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElImageViewer } from 'element-plus'
import type { GalleryImage } from '../types/models'
import { warnInvalidAlt } from '../utils/alt-text'

const props = withDefaults(
  defineProps<{
    /** 图集数据；src 仅允许本地静态资源，alt 必须为具体描述 */
    images: GalleryImage[]
    /** 初始展示下标（默认第一张） */
    initial?: number
    /** 底部版权备注文案（默认为毕设合规文案） */
    copyrightNote?: string
  }>(),
  {
    initial: 0,
    // 素材来自免费图库（Wikimedia Commons 等，自由授权），仅毕设本地演示
    copyrightNote: '素材来源：免费图库（Wikimedia Commons 等），本作品仅用于毕业设计本地演示，非商用，不对外发布。'
  }
)

const emit = defineEmits<{ (e: 'change', index: number): void }>()

const current = ref(props.initial)
const previewVisible = ref(false)

/** 已就位的图片（src 非空）；素材未放入的槽位不渲染，避免出现灰色空块 */
const displayImages = computed(() => props.images.filter((img) => img.src))

// 数据整体替换时回到第一张，并对每张图做 alt 合规校验（开发期告警）
watch(
  () => props.images,
  (images) => {
    current.value = 0
    images.forEach((img) => warnInvalidAlt(img.alt, img.src || '未就位素材'))
  },
  { immediate: true }
)

const total = computed(() => displayImages.value.length)
const currentImage = computed(() => displayImages.value[current.value])
// 大图查看器列表（displayImages 已过滤空 src，不会出现空白帧）
const viewerList = computed(() => displayImages.value.map((img) => img.src))

function goTo(index: number): void {
  if (!total.value) return
  const next = Math.min(Math.max(index, 0), total.value - 1)
  if (next === current.value) return
  current.value = next
  emit('change', next)
}

function prev(): void {
  goTo(current.value > 0 ? current.value - 1 : total.value - 1)
}

function next(): void {
  goTo((current.value + 1) % total.value)
}

/** 图集区域键盘操作：←/→ 切换（大图查看器内部由 Element Plus 处理 Esc/方向键） */
function onKeydown(event: KeyboardEvent): void {
  if (event.key === 'ArrowLeft') {
    event.preventDefault()
    prev()
  } else if (event.key === 'ArrowRight') {
    event.preventDefault()
    next()
  }
}
</script>

<template>
  <figure
    class="photo-gallery"
    role="region"
    aria-label="项目图片图集"
    tabindex="0"
    @keydown="onKeydown"
  >
    <!-- 主图区：width/height 属性占位防 CLS；占位底色与页面背景一致 -->
    <div class="gallery-main">
      <img
        v-if="currentImage"
        v-lazy="currentImage.src"
        class="gallery-main-img"
        :alt="currentImage.alt"
        :width="currentImage.width"
        :height="currentImage.height"
        :title="currentImage.caption"
        @click="previewVisible = true"
      />
      <!-- 素材未就位：显示友好占位说明而非灰色空块（docs/15 素材就位后自动切换为图集） -->
      <div v-else class="gallery-empty">该项目的图片素材整理中（仅限毕业设计本地演示）</div>

      <template v-if="total > 1">
        <button type="button" class="gallery-nav gallery-nav--prev" aria-label="上一张图片" @click="prev">‹</button>
        <button type="button" class="gallery-nav gallery-nav--next" aria-label="下一张图片" @click="next">›</button>
      </template>

      <!-- 计数角标：数字用 Times New Roman（docs/11 第 1.2 节） -->
      <span v-if="total" class="gallery-count" aria-hidden="true">{{ current + 1 }}/{{ total }}</span>
    </div>

    <figcaption class="gallery-caption">
      <!-- 读屏用户的状态播报（视觉隐藏） -->
      <span class="visually-hidden" aria-live="polite">第 {{ current + 1 }} 张，共 {{ total }} 张</span>
      {{ currentImage?.caption }}
    </figcaption>

    <!-- 缩略图条：装饰性重复图，alt 置空，按钮 aria-label 承载名称 -->
    <ul v-if="total > 1" class="gallery-thumbs">
      <li v-for="(img, index) in displayImages" :key="`${img.src}-${index}`">
        <button
          type="button"
          class="gallery-thumb"
          :class="{ 'is-active': index === current }"
          :aria-current="index === current ? 'true' : undefined"
          :aria-label="`查看第 ${index + 1} 张：${img.alt}`"
          @click="goTo(index)"
        >
          <img v-lazy="img.src" alt="" :width="img.width" :height="img.height" />
        </button>
      </li>
    </ul>

    <!-- 版权备注：12px / #999 / 居中 / 与图片区块留 16px -->
    <p class="gallery-copyright">{{ copyrightNote }}</p>
  </figure>

  <!-- 大图查看器：功能必需的预览能力，其过渡动画已在 global.css 全局关闭 -->
  <el-image-viewer
    v-if="previewVisible && currentImage?.src"
    :url-list="viewerList"
    :initial-index="current"
    hide-on-click-modal
    teleported
    @close="previewVisible = false"
  />
</template>

<style scoped>
.photo-gallery {
  margin: 0;
}
.photo-gallery:focus-visible {
  outline: 2px solid #9e2021;
  outline-offset: 2px;
}

/* 主图区 */
.gallery-main {
  position: relative;
  background: #f8f8f8;
  border: 1px solid #e5e5e5;
  border-radius: 2px;
  overflow: hidden;
}
.gallery-main-img {
  display: block;
  width: 100%;
  height: auto;
  max-height: 520px;
  object-fit: contain;
  cursor: zoom-in;
}
.gallery-empty {
  padding: 64px 16px;
  text-align: center;
  font-size: 14px;
  color: #999999;
}

/* 左右切换：仅允许 hover 变色，不做位移/阴影（docs/11 第 2 节） */
.gallery-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  border: 1px solid #e5e5e5;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  color: #333333;
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
}
.gallery-nav:hover {
  color: #9e2021;
  border-color: #9e2021;
}
.gallery-nav--prev {
  left: 8px;
}
.gallery-nav--next {
  right: 8px;
}

.gallery-count {
  position: absolute;
  right: 8px;
  bottom: 8px;
  padding: 2px 8px;
  border-radius: 2px;
  background: rgba(0, 0, 0, 0.55);
  color: #ffffff;
  font-size: 12px;
  font-family: 'Times New Roman', serif;
}

/* 图注 */
.gallery-caption {
  min-height: 1em;
  margin-top: 8px;
  text-align: center;
  font-size: 14px;
  color: #666666;
}

/* 缩略图条：横向滚动，当前项主色描边 */
.gallery-thumbs {
  display: flex;
  gap: 8px;
  margin: 16px 0 0;
  padding: 0;
  list-style: none;
  overflow-x: auto;
}
.gallery-thumb {
  display: block;
  padding: 0;
  border: 2px solid transparent;
  border-radius: 2px;
  background: #ffffff;
  overflow: hidden;
  cursor: pointer;
}
.gallery-thumb.is-active {
  border-color: #9e2021;
}
.gallery-thumb img {
  display: block;
  width: 72px;
  height: 72px;
  object-fit: cover;
}

/* 版权备注（毕设合规要求，固定样式） */
.gallery-copyright {
  margin: 16px 0 0;
  text-align: center;
  font-size: 12px;
  color: #999999;
}

/* 读屏专用（视觉隐藏） */
.visually-hidden {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0 0 0 0);
  white-space: nowrap;
  border: 0;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .gallery-main-img {
    max-height: 280px;
  }
  .gallery-nav {
    width: 30px;
    height: 30px;
  }
  .gallery-thumb img {
    width: 56px;
    height: 56px;
  }
}
</style>
