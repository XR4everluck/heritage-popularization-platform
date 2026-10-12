/**
 * 免费图库素材批量下载脚本（一次性工具，可重复执行）
 * 数据源：Wikimedia Commons（自由授权，CC/公有领域，可免费使用）
 * 用法：node scripts/fetch-free-images.mjs
 * 产物：按 docs/15 清单命名，落位到 src/assets/intangible-images/ 对应子目录
 */
import { writeFileSync, mkdirSync, existsSync } from 'node:fs'
import { join, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'

const ROOT = join(dirname(fileURLToPath(import.meta.url)), '..', 'src', 'assets', 'intangible-images')
const API = 'https://commons.wikimedia.org/w/api.php'
const UA = 'HeritageGraduationProject/1.0 (educational; contact: student@example.com)'

/** 检索清单：目标文件（不含扩展名）→ Commons 检索词（依次尝试，命中不足换下一个）；count 为需要张数 */
const MANIFEST = [
  { dir: 'craft', prefix: 'cloisonne', queries: ['cloisonne vase china', 'cloisonné enamel', 'cloisonne'], count: 4 },
  { dir: 'opera', prefix: 'kunqu', queries: ['kunqu opera', 'Kunqu', 'chinese opera stage'], count: 4 },
  { dir: 'fine-arts', prefix: 'papercut', queries: ['chinese paper cutting'], count: 4 },
  { dir: 'opera', prefix: 'jingju', queries: ['peking opera', 'beijing opera costume', 'Peking opera'], count: 4 },
  { dir: 'fine-arts', prefix: 'suxiu', queries: ['chinese embroidery silk', 'silk embroidery'], count: 4 },
  { dir: 'craft', prefix: 'celadon', queries: ['longquan celadon'], count: 4 },
  { dir: 'craft', prefix: 'kite', queries: ['chinese kite flying', 'chinese kite'], count: 4 },
  { dir: 'music', prefix: 'guqin', queries: ['guqin player', 'guqin instrument', 'guqin'], count: 4 },
  { dir: 'sports', prefix: 'taiji', queries: ['tai chi practice', 'tai chi'], count: 4 },
  { dir: 'folk', prefix: 'solar-terms', queries: ['rice terraces china', 'longji rice terraces'], count: 4 },
  { dir: 'craft', prefix: 'miao-silver', queries: ['hmong silver jewelry', 'miao silver', 'hmong jewelry'], count: 3 },
  { dir: 'craft', prefix: 'dege-printing', queries: ['woodblock printing tibet', 'dege parkhang', 'tibetan woodblock'], count: 4 },
  { dir: 'news', prefix: 'workshop-meeting', queries: ['craft workshop handmade', 'pottery workshop hands'], count: 2 },
  { dir: 'news', prefix: 'campus-activity', queries: ['students classroom china', 'schoolchildren classroom'], count: 2 },
  { dir: 'opera', prefix: 'yueju', queries: ['yue opera chinese'], count: 3 },
  { dir: 'dance', prefix: 'longwu', queries: ['dragon dance china', 'chinese dragon dance festival', 'lion dance china'], count: 3 },
  { dir: 'medicine', prefix: 'zhenjiu', queries: ['acupuncture treatment'], count: 3 },
  { dir: 'quyi', prefix: 'pingtan', queries: ['suzhou pingtan', 'pingtan'], count: 3 },
  { dir: 'music', prefix: 'changdiao', queries: ['morin khuur mongolian', 'morin khuur', 'mongolian musician'], count: 3 },
  { dir: 'folk', prefix: 'mazu', queries: ['mazu temple taiwan', 'mazu temple'], count: 3 },
  { dir: 'craft', prefix: 'xuanzhi', queries: ['rice paper making craft'], count: 3 },
  { dir: 'folk-literature', prefix: 'gesaer', queries: ['tibetan manuscript'], count: 3 }
]

const sleep = (ms) => new Promise((r) => setTimeout(r, ms))

async function searchCommons(query, count) {
  const url = `${API}?action=query&format=json&generator=search&gsrsearch=${encodeURIComponent(query + ' filetype:bitmap')}&gsrnamespace=6&gsrlimit=${count * 3 + 4}&prop=imageinfo&iiprop=url%7Csize%7Cmime&iiurlwidth=1280`
  const res = await fetch(url, { headers: { 'User-Agent': UA } })
  if (!res.ok) throw new Error(`search ${res.status}`)
  const data = await res.json()
  const pages = Object.values(data?.query?.pages ?? {})
  return pages
    .map((p) => p.imageinfo?.[0])
    .filter((info) => info && /jpeg|png/.test(info.mime) && info.width >= 700 && info.thumburl)
    .slice(0, count)
}

async function download(url, dest) {
  const res = await fetch(url, { headers: { 'User-Agent': UA } })
  if (!res.ok) throw new Error(`download ${res.status}`)
  const buf = Buffer.from(await res.arrayBuffer())
  writeFileSync(dest, buf)
  return buf.length
}

const ext = (mime) => (mime.includes('png') ? 'png' : 'jpg')

const report = []
for (const item of MANIFEST) {
  const dir = join(ROOT, item.dir)
  mkdirSync(dir, { recursive: true })
  let ok = 0
  const existing = async (i) => {
    const dest = join(dir, `${item.prefix}-${String(i + 1).padStart(2, '0')}.jpg`)
    const destPng = join(dir, `${item.prefix}-${String(i + 1).padStart(2, '0')}.png`)
    for (const d of [dest, destPng]) {
      if (existsSync(d)) {
        const stat = (await import('node:fs')).statSync(d)
        if (stat.size > 20000) return true
      }
    }
    return false
  }
  // 统计已就位张数
  for (let i = 0; i < item.count; i++) if (await existing(i)) ok++
  if (ok < item.count) {
    for (const query of item.queries) {
      if (ok >= item.count) break
      let hits = []
      try {
        hits = await searchCommons(query, item.count)
      } catch (e) {
        console.error(`  检索失败 ${item.prefix} "${query}": ${e.message}`)
        continue
      }
      for (let i = 0; i < hits.length && ok < item.count; i++) {
        const file = `${item.prefix}-${String(ok + 1).padStart(2, '0')}.${ext(hits[i].mime)}`
        const dest = join(dir, file)
        try {
          await download(hits[i].thumburl, dest)
          ok++
        } catch (e) {
          console.error(`  下载失败 ${file}: ${e.message}`)
        }
        await sleep(350)
      }
      await sleep(350)
    }
  }
  report.push(`${item.dir}/${item.prefix}: ${ok}/${item.count}`)
  console.log(`${ok}/${item.count}  ${item.dir}/${item.prefix}`)
  await sleep(350)
}

// 背景：地图页（壮阔山水）+ 全站衬底（水墨山水）
const BGS = [
  { file: 'map-bg.jpg', query: 'guilin li river mountains', width: 1920 },
  { file: 'bg.jpg', query: 'huangshan mountains sea of clouds', width: 1920 }
]
for (const bg of BGS) {
  try {
    const hits = await searchCommons(bg.query, 5)
    for (const hit of hits) {
      try {
        const size = await download(hit.thumburl, join(ROOT, 'bg', bg.file))
        report.push(`bg/${bg.file}: OK ${Math.round(size / 1024)}KB`)
        console.log(`OK  bg/${bg.file} (${Math.round(size / 1024)}KB)`)
        break
      } catch (e) {
        console.error(`  候选下载失败，换下一张: ${e.message}`)
      }
      await sleep(350)
    }
  } catch (e) {
    console.error(`  背景下载失败 ${bg.file}: ${e.message}`)
  }
  await sleep(350)
}

console.log('\n===== 汇总 =====')
console.log(report.join('\n'))
