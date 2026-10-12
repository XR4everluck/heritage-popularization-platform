/**
 * 图片 alt 描述工具（docs/11 第 6.4 节无障碍约定）
 * 统一 alt 模板："XX非遗项目，XX实拍图，图片用于毕业设计展示"
 * 禁止"图片""照片"等无信息量描述——开发期发现即告警，避免带病上线。
 */

/** 生成符合毕设规范的 alt 描述文本 */
export function buildAlt(projectName: string, description: string): string {
  return `${projectName}非遗项目，${description}，图片用于毕业设计展示`
}

/** 无效 alt 黑名单：纯泛称正则 */
const GENERIC_ALT = /^(图片|照片|图像|图|image|img|picture|photo|pic|\.{0,3})$/i

/** 校验 alt 是否有效（非空且非泛称） */
export function isValidAlt(alt: string | undefined | null): boolean {
  if (!alt) return false
  return !GENERIC_ALT.test(alt.trim())
}

/** 开发期告警：alt 缺失或泛称时在控制台提示来源，便于排查 */
export function warnInvalidAlt(alt: string | undefined | null, source?: string): void {
  if (import.meta.env.DEV && !isValidAlt(alt)) {
    console.warn(`[alt] 无效的图片描述（${source ?? '未知来源'}）：应为具体内容，禁止"图片"等泛称，收到：`, alt)
  }
}
