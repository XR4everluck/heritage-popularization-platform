/**
 * 图集素材清单（全量 14 个非遗项目，docs/15 检索清单的代码侧映射）
 *
 * 工作方式：
 * - 每个项目的图集按「分类子目录 + 文件名前缀 + 序号」声明，素材文件放入后自动生效，无需改代码；
 * - 封面 = 该项目第 1 张图集图（coverFor），轮播图复用关联项目的封面；
 * - 素材未就位时 src 为空串，组件展示占位态，不报错；
 * - 素材来源与版权约束见 src/assets/intangible-images/README.md 与 docs/15
 *   （视觉中国授权副本，仅毕设本地演示，禁止外链，v-lazy 会拦截 http/https）。
 */
import type { GalleryImage } from '../types/models'
import { buildAlt } from '../utils/alt-text'

// 收集图集目录下全部图片资源（webp 命名优先，兼容 jpg/png；eager 直接取 URL 字符串）
const assetModules = import.meta.glob<string>('../assets/intangible-images/**/*.{webp,jpg,jpeg,png}', {
  import: 'default',
  eager: true
})

/** 按分类子目录 + 文件名解析资源 URL（扩展名兼容 webp/jpg/png，按此优先级）；未找到返回空串 */
export function resolveAsset(classification: string, file: string): string {
  const base = file.replace(/\.(webp|jpg|jpeg|png)$/i, '')
  const entries = Object.entries(assetModules)
  for (const ext of ['webp', 'jpg', 'jpeg', 'png']) {
    const suffix = `/intangible-images/${classification}/${base}.${ext}`.toLowerCase()
    const hit = entries.find(([path]) => path.toLowerCase().endsWith(suffix))
    if (hit) return hit[1]
  }
  return ''
}

interface GalleryDecl {
  /** 关联非遗项目 id（mock/heritage.ts 的 seeds） */
  heritageId: number
  /** 分类子目录：craft(传统技艺) / fine-arts(传统美术) / music(传统音乐) / opera(传统戏剧) / sports(传统体育游艺杂技) / folk(民俗) / news(快讯配图) */
  classification: string
  /** 文件名前缀，成图命名 `${prefix}-01.webp` 起连续编号 */
  prefix: string
  /** 每张图的场景描述（按顺序与文件序号对应，用于拼接 alt 模板） */
  scenes: string[]
}

/** 全量声明：14 个项目 × 每项目 2–4 张（检索关键词见 docs/15） */
const declarations: GalleryDecl[] = [
  { heritageId: 1, classification: 'craft', prefix: 'cloisonne', scenes: ['掐丝工艺实拍图', '点蓝工艺实拍图', '烧制工序实拍图', '成品陈列实拍图'] },
  { heritageId: 2, classification: 'opera', prefix: 'kunqu', scenes: ['舞台剧照', '水磨腔演唱实拍图', '戏装造型实拍图', '曲谱文献实拍图'] },
  { heritageId: 3, classification: 'fine-arts', prefix: 'papercut', scenes: ['刻制工艺实拍图', '点彩染色实拍图', '戏曲脸谱作品实拍图', '窗花作品实拍图'] },
  { heritageId: 4, classification: 'opera', prefix: 'jingju', scenes: ['舞台剧照', '勾脸妆容实拍图', '戏服道具实拍图', '经典剧目演出实拍图'] },
  { heritageId: 5, classification: 'fine-arts', prefix: 'suxiu', scenes: ['劈丝配色实拍图', '双面绣作品实拍图', '绣娘工作台实拍图', '成品展示实拍图'] },
  { heritageId: 6, classification: 'craft', prefix: 'celadon', scenes: ['拉坯工艺实拍图', '施釉工艺实拍图', '龙窑烧制实拍图', '梅子青成品实拍图'] },
  { heritageId: 7, classification: 'craft', prefix: 'kite', scenes: ['扎制骨架实拍图', '糊面绘色实拍图', '龙头蜈蚣风筝实拍图', '放飞场景实拍图'] },
  { heritageId: 8, classification: 'music', prefix: 'guqin', scenes: ['古琴形制实拍图', '演奏场景实拍图', '斫琴工艺实拍图', '减字谱文献实拍图'] },
  { heritageId: 9, classification: 'sports', prefix: 'taiji', scenes: ['拳法演练实拍图', '推手对练实拍图', '晨练场景实拍图', '传承教学实拍图'] },
  { heritageId: 10, classification: 'folk', prefix: 'solar-terms', scenes: ['农事节气场景实拍图', '节气民俗活动实拍图', '物候景观实拍图', '传统历法文献实拍图'] },
  { heritageId: 11, classification: 'craft', prefix: 'miao-silver', scenes: ['熔银锻打实拍图', '拉丝錾刻实拍图', '银饰成品实拍图', '盛装佩戴场景实拍图'] },
  { heritageId: 12, classification: 'craft', prefix: 'dege-printing', scenes: ['刻版工艺实拍图', '藏纸制作实拍图', '印经场景实拍图', '经版库房实拍图'] },
  { heritageId: 13, classification: 'news', prefix: 'workshop-meeting', scenes: ['会议现场实拍图', '非遗工坊实拍图'] },
  { heritageId: 14, classification: 'news', prefix: 'campus-activity', scenes: ['校园课堂实拍图', '学生体验实拍图'] },
  // —— 扩充批次（覆盖舞蹈/曲艺/医药/民俗/音乐/民间文学） ——
  { heritageId: 15, classification: 'opera', prefix: 'yueju', scenes: ['舞台剧照', '水袖身段实拍图', '经典剧目演出实拍图'] },
  { heritageId: 16, classification: 'dance', prefix: 'longwu', scenes: ['龙灯扎制实拍图', '舞龙表演实拍图', '火龙夜景实拍图'] },
  { heritageId: 17, classification: 'medicine', prefix: 'zhenjiu', scenes: ['针灸铜人实拍图', '施针手法实拍图', '艾灸治疗场景实拍图'] },
  { heritageId: 18, classification: 'quyi', prefix: 'pingtan', scenes: ['书场演出实拍图', '琵琶三弦伴奏实拍图', '长篇弹词场景实拍图'] },
  { heritageId: 19, classification: 'music', prefix: 'changdiao', scenes: ['草原长调演唱实拍图', '那达慕演出实拍图', '马头琴伴奏实拍图'] },
  { heritageId: 20, classification: 'folk', prefix: 'mazu', scenes: ['妈祖祖庙实拍图', '祭典仪式实拍图', '巡安绕境实拍图'] },
  { heritageId: 21, classification: 'craft', prefix: 'xuanzhi', scenes: ['捞纸工艺实拍图', '晒纸场景实拍图', '成品宣纸实拍图'] },
  { heritageId: 22, classification: 'folk-literature', prefix: 'gesaer', scenes: ['说唱艺人实拍图', '史诗文献实拍图', '传习场景实拍图'] }
]

/**
 * 默认登记尺寸：真实图片放入后，请把对应条目的 width/height 更新为实际像素值
 * （用于 <img> 占位防 CLS；此处统一按 3:2 常见比例登记）
 */
const DEFAULT_WIDTH = 1600
const DEFAULT_HEIGHT = 1067

/** 图集条目构造：src 本地解析 + alt 走统一模板（docs/12 无障碍要求） */
function buildGallery(decl: GalleryDecl): GalleryImage[] {
  return decl.scenes.map((scene, index) => {
    const file = `${decl.prefix}-${String(index + 1).padStart(2, '0')}.webp`
    return {
      src: resolveAsset(decl.classification, file),
      alt: buildAlt(projectNameOf(decl.heritageId), scene),
      width: DEFAULT_WIDTH,
      height: DEFAULT_HEIGHT,
      caption: scene.replace('实拍图', '')
    }
  })
}

/** 项目名映射（alt 模板需要；与 mock/heritage.ts 的 seeds 保持一致） */
const PROJECT_NAMES: Record<number, string> = {
  1: '景泰蓝制作技艺', 2: '昆曲', 3: '中国剪纸（蔚县剪纸）', 4: '京剧',
  5: '苏绣', 6: '龙泉青瓷烧制技艺', 7: '潍坊风筝制作技艺', 8: '古琴艺术',
  9: '太极拳（陈氏太极拳）', 10: '二十四节气', 11: '苗族银饰锻制技艺',
  12: '德格印经院藏族雕版印刷技艺', 13: '全国非遗工坊建设现场会', 14: '"非遗进校园"系列活动',
  15: '越剧', 16: '龙舞（铜梁龙舞）', 17: '中医针灸', 18: '苏州评弹',
  19: '蒙古族长调民歌', 20: '妈祖祭典（妈祖信俗）', 21: '宣纸制作技艺', 22: '格萨尔'
}

function projectNameOf(heritageId: number): string {
  return PROJECT_NAMES[heritageId] ?? '非遗'
}

/** 全量图集：heritageId → GalleryImage[]（mock 数据源使用；素材未就位的条目 src 为空串） */
export const galleriesByHeritageId: Record<number, GalleryImage[]> = Object.fromEntries(
  declarations.map((decl) => [decl.heritageId, buildGallery(decl)])
)

/**
 * 全量图集：项目名称 → GalleryImage[]（详情页回退使用）。
 * 真实后端的 id 顺序与 mock 不同，按名称映射才不会张冠李戴。
 */
export const galleriesByName: Record<string, GalleryImage[]> = Object.fromEntries(
  declarations.map((decl) => [projectNameOf(decl.heritageId), buildGallery(decl)])
)

/** 项目封面：取该项目第 1 张已就位的图集图；全部未就位返回空串（调用方退回占位图） */
export function coverFor(heritageId: number): string {
  const gallery = galleriesByHeritageId[heritageId] ?? []
  return gallery.find((img) => img.src)?.src ?? ''
}

/** 项目封面（按名称）：同 coverFor，供真实后端数据回退使用 */
export function coverForName(name: string): string {
  const gallery = getGalleryByName(name)
  return gallery.find((img) => img.src)?.src ?? ''
}

/** 按项目名称查图集（名称容错：真实库与清单名称可能有"（蔚县剪纸）"类后缀差异，双向前缀匹配） */
export function getGalleryByName(name: string): GalleryImage[] {
  if (!name) return []
  if (galleriesByName[name]) return galleriesByName[name]
  const key = Object.keys(galleriesByName).find(
    (k) => k.startsWith(name) || name.startsWith(k)
  )
  return key ? galleriesByName[key] : []
}
