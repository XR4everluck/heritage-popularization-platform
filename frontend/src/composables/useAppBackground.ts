/**
 * 全站背景衬底（docs/14 背景效果方案）
 *
 * 呈现方式对标中国非物质文化遗产网：内容区之下铺一层极低对比度的"纸纹/纹样"衬底，
 * 白色内容卡片浮于其上；衬底固定不随滚动、无动画（符合 docs/11 动效禁令）。
 *
 * 素材约定（与图集一致，docs/12 版权约束）：
 * - 仅本地静态资源，目录 src/assets/intangible-images/bg/；
 * - 优先级：bg.webp > bg.jpg > bg.png > 其他文件（demo-pattern.svg 为内置演示纹理）；
 * - 素材未就位时变量为空，页面退回纯色 #f8f8f8 底，与现状无差异；
 * - 视觉中国授权素材下载后转 webp 命名为 bg.webp 放入即自动生效，无需改代码。
 */
const bgModules = import.meta.glob<string>('../assets/intangible-images/bg/*.{webp,jpg,png,svg}', {
  import: 'default',
  eager: true
})

/** 按优先级选出背景图 URL；未就位返回空串（CSS 侧退回纯色底） */
export function resolveAppBackground(): string {
  const entries = Object.entries(bgModules)
  if (!entries.length) return ''
  const byPriority = (re: RegExp) => entries.find(([path]) => re.test(path))
  const hit = byPriority(/bg\.webp$/) ?? byPriority(/bg\.jpg$/) ?? byPriority(/bg\.png$/) ?? entries[0]
  return hit ? hit[1] : ''
}

/** 把背景图写入根元素 CSS 变量，供 .app-bg 衬底层引用 */
export function applyAppBackground(): void {
  const url = resolveAppBackground()
  if (typeof document === 'undefined') return
  if (url) {
    document.documentElement.style.setProperty('--app-bg-image', `url("${url}")`)
  } else {
    document.documentElement.style.removeProperty('--app-bg-image')
  }
}
