/**
 * 全局领域类型定义（docs/10 第 4.2 节：类型集中管理）
 */

/** 图集图片条目（PhotoGallery 组件数据结构） */
export interface GalleryImage {
  /** 图片 URL，仅允许本地静态资源（v-lazy 指令会拦截外链） */
  src: string
  /** 无障碍描述，模板见 utils/alt-text.ts 的 buildAlt，禁止"图片"等无效描述 */
  alt: string
  /** 实际像素宽，用于 width 属性占位防止 CLS */
  width: number
  /** 实际像素高，用于 height 属性占位防止 CLS */
  height: number
  /** 图注（可选） */
  caption?: string
}
