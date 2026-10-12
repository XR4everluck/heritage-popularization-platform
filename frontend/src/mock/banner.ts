/**
 * 轮播图 mock 数据（docs/11 第 6.2 节：mock 与业务代码分离）
 * 图像：优先复用关联非遗项目已就位的视觉中国素材（coverFor），
 * 未就位时退回本地生成的占位图（utils/placeholder.js）。
 * 素材版权约束见 src/assets/intangible-images/README.md：仅本地资源，禁止外链。
 */
import { defaultCover } from '../utils/placeholder'
import { coverFor } from './heritage-images'

export interface BannerItem {
  id: number
  title: string
  image: string
  linkUrl: string
}

/** 轮播图：图上标题条文案 + 站内跳转链接 */
export const banners: BannerItem[] = [
  { id: 1, title: '昆曲：百戏之祖，雅韵六百年', image: coverFor(2) || defaultCover('昆曲：百戏之祖', 1200, 420), linkUrl: '/heritage/2' },
  { id: 2, title: '景泰蓝制作技艺：铜胎掐丝，釉色千年', image: coverFor(1) || defaultCover('景泰蓝制作技艺', 1200, 420), linkUrl: '/heritage/1' },
  { id: 3, title: '二十四节气：中国人的时间智慧', image: coverFor(10) || defaultCover('二十四节气', 1200, 420), linkUrl: '/heritage/10' },
  { id: 4, title: '中国剪纸：纸上生花，剪出人间烟火', image: coverFor(3) || defaultCover('中国剪纸', 1200, 420), linkUrl: '/heritage/3' }
]
