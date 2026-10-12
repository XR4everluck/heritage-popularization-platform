/**
 * 用户内容发布与审核（mock，localStorage 持久化）
 *
 * 审核流：用户提交（待审核 0）→ 管理员通过（1）/驳回（2 + 理由）→ 首页仅展示已通过内容。
 * 类型：news（科普快讯）/ course（科普视频，videoUrl 为 B 站链接）。
 * 真实后端对接时由 /api/submission* 系列接口替换，字段结构已对齐。
 */
import { defaultCover } from '../utils/placeholder'

export interface Submission {
  id: number
  /** news=科普快讯 / course=科普视频 */
  type: 'news' | 'course'
  title: string
  /** 关联非遗项目 id（可选） */
  heritageId: number | null
  heritageName: string
  /** 快讯正文 / 视频简介 */
  content: string
  /** 视频链接（B 站 BV 链接或检索页链接） */
  videoUrl: string
  submitter: string
  submitTime: string
  /** 0=待审核 1=已通过 2=未通过 */
  status: 0 | 1 | 2
  reviewNote?: string
  reviewTime?: string
}

interface PersistShape {
  seq: number
  list: Submission[]
}

const STORE_KEY = 'heritage_mock_submissions'

function load(): PersistShape {
  try {
    const raw = localStorage.getItem(STORE_KEY)
    if (raw) {
      const data = JSON.parse(raw)
      // seq 下限 200：与种子 id（100/101）保持安全距离，避免审核时 id 撞车
      data.seq = Math.max(200, Number(data.seq) || 200)
      return data
    }
  } catch { /* 降级为内存态 */ }
  // 演示种子：一条已通过、一条待审核，便于两端开箱可见
  return {
    seq: 100,
    list: [
      {
        id: 100,
        type: 'news',
        title: '社区非遗体验日：景泰蓝掐丝走进百姓家',
        heritageId: 1,
        heritageName: '景泰蓝制作技艺',
        content: '上周末，某社区文化活动中心举办非遗体验日，居民在传承人指导下体验掐丝工艺，现场气氛热烈。',
        videoUrl: '',
        submitter: '非遗爱好者小林',
        submitTime: '2026-10-09 10:20:00',
        status: 1,
        reviewNote: '内容属实，图片清晰，同意发布。',
        reviewTime: '2026-10-09 15:00:00'
      },
      {
        id: 101,
        type: 'course',
        title: '三分钟看懂剪纸的阴刻与阳刻',
        heritageId: 3,
        heritageName: '中国剪纸（蔚县剪纸）',
        content: '以蔚县剪纸为例，直观对比阴刻与阳刻的刀法差异。',
        videoUrl: 'https://www.bilibili.com/video/BV1xs411y7CG/',
        submitter: '演示用户',
        submitTime: '2026-10-10 09:05:00',
        status: 0
      }
    ]
  }
}

let db: PersistShape = load()

function persist(): void {
  try {
    localStorage.setItem(STORE_KEY, JSON.stringify(db))
  } catch { /* 降级为内存态 */ }
}

/** 提交内容（用户端）：默认待审核；heritageName 由调用方解析后传入（避免循环依赖） */
export function submit(data: { type: string; title: string; heritageId: number | null; heritageName?: string; content: string; videoUrl: string }, nickname: string): Submission {
  const submission: Submission = {
    id: db.seq++,
    type: data.type === 'course' ? 'course' : 'news',
    title: data.title,
    heritageId: data.heritageId ? Number(data.heritageId) : null,
    heritageName: data.heritageName ?? '',
    content: data.content,
    videoUrl: data.videoUrl || '',
    submitter: nickname,
    submitTime: new Date().toLocaleString('zh-CN', { hour12: false }),
    status: 0
  }
  db.list.push(submission)
  persist()
  return submission
}

/** 我的发布（用户端，按时间倒序） */
export function mySubmissions(nickname: string): Submission[] {
  return db.list.filter((s) => s.submitter === nickname).slice().reverse()
}

/** 待审核/全部列表（管理端） */
export function adminList(status?: number): Submission[] {
  const list = status === undefined || status === null || Number.isNaN(status)
    ? db.list
    : db.list.filter((s) => s.status === status)
  return list.slice().reverse()
}

/** 审核（管理端）：status 1=通过 2=驳回（附理由） */
export function review(id: number, status: 1 | 2, reviewNote: string, reviewer: string): Submission | null {
  const item = db.list.find((s) => s.id === Number(id))
  if (!item || item.status !== 0) return null
  item.status = status
  item.reviewNote = reviewNote || (status === 1 ? '审核通过' : '不符合发布要求')
  item.reviewTime = new Date().toLocaleString('zh-CN', { hour12: false })
  void reviewer
  persist()
  return item
}

/** 已通过的科普快讯（合并进首页快讯列表） */
export function approvedNews(): Array<Record<string, unknown>> {
  return db.list
    .filter((s) => s.type === 'news' && s.status === 1)
    .map((s) => ({
      id: s.id,
      title: s.title,
      content: s.content,
      coverImage: defaultCover(s.title, 600, 360),
      isTop: 0,
      heritageName: s.heritageName,
      heritageId: s.heritageId,
      publishTime: s.submitTime
    }))
}

/** 已通过的科普视频（合并进热门课程列表） */
export function approvedCourses(): Array<Record<string, any>> {
  return db.list
    .filter((s) => s.type === 'course' && s.status === 1)
    .map((s) => ({
      id: s.id,
      name: s.title,
      heritageId: s.heritageId,
      cover: defaultCover(s.title, 600, 360),
      teacher: s.submitter,
      duration: 0,
      viewCount: 0,
      publishTime: s.submitTime,
      chapterCount: 1,
      summary: s.content,
      chapters: [{ videoUrl: s.videoUrl }],
      /** 用户发布标记：详情页按单视频章节渲染 */
      userSubmitted: true
    }))
}
