/**
 * v-lazy 图片懒加载指令（自定义零依赖实现，docs/11 第 4.1 节）
 *
 * 用法：<img v-lazy="本地资源URL" alt="..." width="1600" height="1067" />
 *
 * 约束与设计：
 * - 仅接受本地静态资源：http/https 外链一律拦截并在控制台报错
 *   （视觉中国 vcg.com 等站点有防盗链，且毕设素材不允许在线引用，docs/12 版权约束）；
 * - IntersectionObserver 提前 200px 预加载；不支持时降级为立即加载；
 * - 加载前/失败时显示中性占位图，宽度高度由模板 width/height 属性兜住（防 CLS）。
 */
import type { Directive } from 'vue'

/** 中性占位图（与页面背景同系的浅灰，无装饰性） */
const PLACEHOLDER = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="4" height="3"><rect width="4" height="3" fill="#f0f0f0"/></svg>'
)}`

let observer: IntersectionObserver | undefined

function ensureObserver(): IntersectionObserver | undefined {
  if (typeof IntersectionObserver === 'undefined') return undefined
  if (!observer) {
    observer = new IntersectionObserver(
      (entries) => {
        for (const entry of entries) {
          if (entry.isIntersecting) {
            loadImage(entry.target as HTMLImageElement)
            observer?.unobserve(entry.target)
          }
        }
      },
      { rootMargin: '200px 0px' }
    )
  }
  return observer
}

function loadImage(el: HTMLImageElement): void {
  const src = el.dataset.lazySrc
  if (!src || el.getAttribute('src') === src) return
  el.src = src
}

/** 挂载观察，等待进入视口后加载 */
function schedule(el: HTMLImageElement, src: string): void {
  el.dataset.lazySrc = src
  el.src = PLACEHOLDER
  const io = ensureObserver()
  if (io) io.observe(el)
  else loadImage(el) // 降级：无 IntersectionObserver 时直接加载
}

function onLoad(this: HTMLImageElement): void {
  this.classList.remove('is-lazy-pending')
  this.classList.add('is-lazy-loaded')
}

function onError(this: HTMLImageElement): void {
  this.classList.remove('is-lazy-pending')
  this.classList.add('is-lazy-error')
}

function apply(el: HTMLImageElement, binding: { value?: string; oldValue?: string }): void {
  el.removeEventListener('load', onLoad)
  el.removeEventListener('error', onError)
  el.addEventListener('load', onLoad)
  el.addEventListener('error', onError)

  const src = binding.value || ''
  observer?.unobserve(el)
  delete el.dataset.lazySrc

  if (!src) {
    // 数据未就位（素材缺失）→ 常驻占位
    el.classList.remove('is-lazy-loaded')
    el.classList.add('is-lazy-pending', 'is-lazy-error')
    el.src = PLACEHOLDER
    return
  }

  if (/^https?:/i.test(src)) {
    // 版权/防盗链约束：图集素材禁止在线引用，仅允许构建产物内的本地资源
    console.error('[v-lazy] 已拦截外链图片（仅允许本地静态资源，禁止 vcg.com 等在线引用）：', src)
    el.classList.remove('is-lazy-loaded')
    el.classList.add('is-lazy-pending', 'is-lazy-error')
    el.src = PLACEHOLDER
    return
  }

  el.classList.remove('is-lazy-loaded', 'is-lazy-error')
  el.classList.add('is-lazy-pending')
  schedule(el, src)
}

export const lazy: Directive<HTMLImageElement, string> = {
  mounted(el, binding) {
    apply(el, binding)
  },
  updated(el, binding) {
    if (binding.value !== binding.oldValue) apply(el, binding)
  },
  unmounted(el) {
    observer?.unobserve(el)
  }
}
