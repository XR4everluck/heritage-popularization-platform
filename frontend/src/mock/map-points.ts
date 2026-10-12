/**
 * 非遗地图点位 mock 数据（docs/11 第 5.6 节：可选拓展模块，仅 mock，不接真实接口）
 *
 * 用途：
 * 1. 当前 ECharts 地图页的省份统计由本文件点位数据推导（regionStatsRaw）；
 * 2. 后续升级高德 JSAPI（docs/10 第 8 节）时，经 useMapPoints() 组合式函数直接消费，
 *    届时仅需把本文件数据换成后端接口返回，组件层零改动。
 */
import { defaultCover } from '../utils/placeholder'

export interface MapPoint {
  id: number
  /** 关联非遗项目名 */
  name: string
  /** 所属分类 */
  category: string
  /** 省份简称（与列表页 REGIONS、地图省份名一致） */
  province: string
  /** 经度 */
  lng: number
  /** 纬度 */
  lat: number
  /** 关联非遗项目 id（点位弹窗跳详情用） */
  heritageId: number
  /** 点位弹窗缩略图（本地占位图） */
  coverImage: string
}

/** mock 点位：覆盖东中部主要省份，含经纬度（GCJ-02 语义，供高德直接使用） */
export const mapPoints: MapPoint[] = [
  { id: 1, name: '景泰蓝制作技艺', category: '传统技艺', province: '北京', lng: 116.407, lat: 39.904, heritageId: 1, coverImage: defaultCover('景泰蓝', 200, 150) },
  { id: 2, name: '昆曲', category: '传统戏剧', province: '江苏', lng: 120.585, lat: 31.299, heritageId: 2, coverImage: defaultCover('昆曲', 200, 150) },
  { id: 3, name: '中国剪纸（蔚县剪纸）', category: '传统美术', province: '河北', lng: 114.589, lat: 39.844, heritageId: 3, coverImage: defaultCover('蔚县剪纸', 200, 150) },
  { id: 4, name: '京剧', category: '传统戏剧', province: '北京', lng: 116.435, lat: 39.918, heritageId: 4, coverImage: defaultCover('京剧', 200, 150) },
  { id: 5, name: '苏绣', category: '传统美术', province: '江苏', lng: 120.619, lat: 31.299, heritageId: 5, coverImage: defaultCover('苏绣', 200, 150) },
  { id: 6, name: '龙泉青瓷烧制技艺', category: '传统技艺', province: '浙江', lng: 119.142, lat: 28.074, heritageId: 6, coverImage: defaultCover('龙泉青瓷', 200, 150) },
  { id: 7, name: '潍坊风筝制作技艺', category: '传统技艺', province: '山东', lng: 119.107, lat: 36.709, heritageId: 7, coverImage: defaultCover('潍坊风筝', 200, 150) },
  { id: 8, name: '古琴艺术', category: '传统音乐', province: '北京', lng: 116.397, lat: 39.918, heritageId: 8, coverImage: defaultCover('古琴艺术', 200, 150) },
  { id: 9, name: '太极拳（陈氏太极拳）', category: '传统体育、游艺与杂技', province: '河南', lng: 113.083, lat: 35.207, heritageId: 9, coverImage: defaultCover('太极拳', 200, 150) },
  { id: 10, name: '苗族银饰锻制技艺', category: '传统技艺', province: '贵州', lng: 106.630, lat: 26.647, heritageId: 11, coverImage: defaultCover('苗族银饰', 200, 150) },
  { id: 11, name: '德格印经院藏族雕版印刷技艺', category: '传统技艺', province: '四川', lng: 98.580, lat: 31.806, heritageId: 12, coverImage: defaultCover('雕版印刷', 200, 150) },
  { id: 12, name: '二十四节气', category: '民间文学', province: '全国', lng: 0, lat: 0, heritageId: 10, coverImage: defaultCover('二十四节气', 200, 150) },
  // —— 扩充批次点位 ——
  { id: 13, name: '越剧', category: '传统戏剧', province: '浙江', lng: 120.494, lat: 29.588, heritageId: 15, coverImage: defaultCover('越剧', 200, 150) },
  { id: 14, name: '龙舞（铜梁龙舞）', category: '传统舞蹈', province: '重庆', lng: 106.056, lat: 29.839, heritageId: 16, coverImage: defaultCover('铜梁龙舞', 200, 150) },
  { id: 15, name: '中医针灸', category: '传统医药', province: '全国', lng: 0, lat: 0, heritageId: 17, coverImage: defaultCover('中医针灸', 200, 150) },
  { id: 16, name: '苏州评弹', category: '曲艺', province: '江苏', lng: 120.624, lat: 31.321, heritageId: 18, coverImage: defaultCover('苏州评弹', 200, 150) },
  { id: 17, name: '蒙古族长调民歌', category: '传统音乐', province: '内蒙古', lng: 111.752, lat: 40.842, heritageId: 19, coverImage: defaultCover('长调民歌', 200, 150) },
  { id: 18, name: '妈祖祭典（妈祖信俗）', category: '民俗', province: '福建', lng: 119.094, lat: 25.047, heritageId: 20, coverImage: defaultCover('妈祖信俗', 200, 150) },
  { id: 19, name: '宣纸制作技艺', category: '传统技艺', province: '安徽', lng: 118.419, lat: 30.686, heritageId: 21, coverImage: defaultCover('宣纸制作', 200, 150) },
  { id: 20, name: '格萨尔', category: '民间文学', province: '四川', lng: 98.596, lat: 31.620, heritageId: 22, coverImage: defaultCover('格萨尔史诗', 200, 150) }
]

/**
 * 推导地图页省份统计数据（模拟 getRegionStats 后端返回的自由文本地区 → 数量）。
 * 「全国」等非具体省份条目由地图页归并逻辑计入"未定位"统计，用于验证边界表现。
 */
export function regionStatsRaw(): Record<string, number> {
  const stats: Record<string, number> = {}
  for (const point of mapPoints) {
    // 模拟后端的自由文本地区格式（如「江苏省苏州市」），地图页按省份简称归并
    const regionText = point.province === '全国' ? '全国各地' : `${point.province}省内多地`
    stats[regionText] = (stats[regionText] || 0) + 1
  }
  return stats
}
