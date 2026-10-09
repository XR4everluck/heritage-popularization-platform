/**
 * 本地占位图工具：生成渐变 SVG data-URI 作为默认封面
 * （无外网依赖，解决种子数据无图片文件时的破图问题；后台上传真实图片后自然覆盖）
 */

/** 预设渐变色板，按 id 取色保证同一张图稳定 */
const PALETTES = [
  ['#c0392b', '#e67e22'],
  ['#8e44ad', '#3498db'],
  ['#16a085', '#a3e4d7'],
  ['#d35400', '#f1c40f'],
  ['#2c3e50', '#34495e'],
  ['#c0392b', '#8e44ad']
]

/** 生成默认封面 data-URI（text 单行居中显示，超出按宽度等比缩小并截断） */
export function defaultCover(text = '非遗之美', width = 600, height = 360) {
  const raw = String(text ?? '').replace(/\s+/g, ' ').trim() || '非遗之美'
  const label = raw.length > 14 ? raw.slice(0, 13) + '…' : raw
  const palette = PALETTES[Math.abs(hashCode(raw)) % PALETTES.length]
  // 中文字宽约等于字号，按 86% 可用宽度反推字号，避免长标题溢出裁切
  const fontSize = Math.max(18, Math.min(Math.round(height / 6), Math.floor((width * 0.86) / label.length)))
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}">
  <defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
    <stop offset="0" stop-color="${palette[0]}"/><stop offset="1" stop-color="${palette[1]}"/>
  </linearGradient></defs>
  <rect width="100%" height="100%" fill="url(#g)"/>
  <text x="50%" y="52%" text-anchor="middle" dominant-baseline="middle"
    font-family="PingFang SC, Microsoft YaHei, sans-serif" font-size="${fontSize}"
    fill="rgba(255,255,255,0.92)" font-weight="bold">${escapeXml(label)}</text>
</svg>`
  return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svg)
}

/** SVG 文本转义：标题含 & < > 时否则会生成非法 SVG，导致图片无法解析 */
function escapeXml(str) {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function hashCode(str) {
  let hash = 0
  for (let i = 0; i < str.length; i++) {
    hash = (hash << 5) - hash + str.charCodeAt(i)
    hash |= 0
  }
  return hash
}

/** 图片加载失败兜底：把 img.src 换成本地占位图（轮播图等后端路径暂无文件时使用） */
export function coverFallback(event, text = '非遗之美') {
  event.target.src = defaultCover(text)
}
