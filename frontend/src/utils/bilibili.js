/**
 * B 站链接工具：从视频页链接解析 BV 号并生成官方播放器嵌入地址
 */
/** 是否为 B 站链接（含 b23.tv 短链） */
export function isBilibiliUrl(url = '') {
  return /bilibili\.com|b23\.tv/.test(url)
}

/**
 * 解析 B 站官方播放器嵌入地址
 * @returns 形如 https://player.bilibili.com/player.html?bvid=xxx&page=1 的地址；
 *          非 B 站链接或解析不到 BV 号时返回空串（调用方走"新窗口打开"兜底）
 */
export function buildBiliEmbed(url = '') {
  const bv = url.match(/BV[0-9A-Za-z]+/)
  if (!bv) return ''
  const page = url.match(/[?&]p=(\d+)/)
  return `https://player.bilibili.com/player.html?bvid=${bv[0]}&page=${page ? page[1] : 1}&autoplay=0&danmaku=0&high_quality=1`
}
