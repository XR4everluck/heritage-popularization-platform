/**
 * mock 接口路由与统一入口（docs/11 第 6.2 节）
 *
 * 设计原则：
 * - URL 与真实 SpringBoot 接口完全一致（见 docs/08 接口文档），切换开关即可无缝对接后端；
 * - 统一返回 Result 包装 { code, msg, data }，与 axios 响应拦截器的输出形状一致；
 * - 模拟 300ms 网络延迟 + 数据深拷贝，避免 mock 数据被页面意外污染；
 * - 需登录接口通过 token 头识别当前用户（token 由 mock 登录签发，格式：mock-token|昵称|角色）。
 */
import { ElMessage } from 'element-plus'
import router from '../router'
import { useUserStore } from '../store/user'
import { defaultCover } from '../utils/placeholder'
import * as heritage from './heritage'
import * as inheritor from './inheritor'
import * as submissions from './submissions'
import { banners } from './banner'
import { regionStatsRaw, mapPoints } from './map-points'

// ---------- mock 用户态 ----------
interface MockUser { nickname: string; role: string; avatar: string; phone: string; email: string; introduction: string }
const adminUser: MockUser = { nickname: '管理员', role: 'admin', avatar: '', phone: '', email: '', introduction: '平台管理员' }

/** 用户资料库（localStorage 持久化：登录/注册建档，个人资料保存后跨刷新保留） */
function loadProfiles(): Record<string, MockUser> {
  try {
    return JSON.parse(localStorage.getItem('heritage_mock_profiles') || '{}')
  } catch {
    return {}
  }
}
function saveProfiles(): void {
  try {
    localStorage.setItem('heritage_mock_profiles', JSON.stringify(profileDb))
  } catch { /* 降级为内存态 */ }
}
const profileDb: Record<string, MockUser> = loadProfiles()

/** 从请求头解析当前用户；未登录返回 null */
function currentUser(token?: string): MockUser | null {
  if (!token || !token.startsWith('mock-token|')) return null
  const [, nickname, role] = token.split('|')
  return profileDb[nickname] || { nickname: nickname || '演示用户', role: role || 'user', avatar: '', phone: '', email: '', introduction: '' }
}

// ---------- Result 包装 ----------
function ok(data: unknown) {
  return { code: 200, msg: 'ok', data }
}
function fail(code: number, msg: string) {
  return { code, msg, data: null }
}

// ---------- 路由表 ----------
type Ctx = { params?: Record<string, unknown>; data?: Record<string, unknown>; token?: string; match: RegExpMatchArray }
type Handler = (ctx: Ctx) => unknown

const routes: Array<{ method: string; pattern: RegExp; handler: Handler }> = [
  // 非遗项目
  { method: 'GET', pattern: /^\/api\/category\/list$/, handler: () => heritage.categories },
  { method: 'GET', pattern: /^\/api\/heritage\/page$/, handler: (c) => heritage.page(c.params ?? {}) },
  { method: 'GET', pattern: /^\/api\/heritage\/region\/stats$/, handler: () => regionStatsRaw() },
  { method: 'GET', pattern: /^\/api\/heritage\/recommend$/, handler: (c) => heritage.recommend(c.params?.heritageId as number, Number(c.params?.limit) || 4) },
  { method: 'GET', pattern: /^\/api\/heritage\/tip$/, handler: () => heritage.randomTip() },
  { method: 'GET', pattern: /^\/api\/heritage\/(\d+)\/history$/, handler: (c) => heritage.history(Number(c.match[1])) },
  { method: 'POST', pattern: /^\/api\/heritage\/(\d+)\/view$/, handler: () => ({}) },
  { method: 'GET', pattern: /^\/api\/heritage\/(\d+)$/, handler: (c) => heritage.detail(Number(c.match[1])) },
  // 科普快讯
  { method: 'GET', pattern: /^\/api\/heritage-news\/list$/, handler: (c) => heritage.newsList(Number(c.params?.limit) || 6) },
  { method: 'GET', pattern: /^\/api\/heritage-news\/(\d+)$/, handler: (c) => heritage.detail(Number(c.match[1])) },
  // 公告
  { method: 'GET', pattern: /^\/api\/portal\/banners$/, handler: () => banners },
  { method: 'GET', pattern: /^\/api\/portal\/notices$/, handler: () => heritage.noticePage() },
  { method: 'GET', pattern: /^\/api\/portal\/notices\/(\d+)$/, handler: (c) => heritage.noticeDetail(Number(c.match[1])) },
  // 课程
  { method: 'GET', pattern: /^\/api\/course\/hot$/, handler: (c) => heritage.hotCourses(Number(c.params?.limit) || 4) },
  { method: 'GET', pattern: /^\/api\/course\/list$/, handler: (c) => heritage.coursesByHeritage(c.params?.heritageId as number) },
  { method: 'GET', pattern: /^\/api\/course\/(\d+)\/chapters$/, handler: (c) => heritage.courseChapters(Number(c.match[1])) },
  { method: 'POST', pattern: /^\/api\/course\/(\d+)\/view$/, handler: () => ({}) },
  { method: 'GET', pattern: /^\/api\/course\/(\d+)$/, handler: (c) => heritage.courseDetail(Number(c.match[1])) },
  // 传承人
  { method: 'GET', pattern: /^\/api\/inheritor\/list$/, handler: () => inheritor.list() },
  { method: 'GET', pattern: /^\/api\/inheritor\/(\d+)$/, handler: (c) => inheritor.detail(Number(c.match[1])) },
  // 评论
  { method: 'GET', pattern: /^\/api\/comment\/page$/, handler: (c) => heritage.commentPage(c.params ?? {}) },
  { method: 'POST', pattern: /^\/api\/comment$/, handler: (c) => {
      const user = currentUser(c.token)
      if (!user) return fail(401, '请先登录')
      heritage.addComment({ heritageId: Number(c.data?.heritageId), content: String(c.data?.content ?? '') }, user.nickname)
      return {}
    } },
  // 个人中心：我的评论 + 删除
  { method: 'GET', pattern: /^\/api\/comment\/my$/, handler: (c) => {
      const user = currentUser(c.token)
      if (!user) return fail(401, '请先登录')
      return heritage.myComments(user.nickname, c.params ?? {})
    } },
  { method: 'DELETE', pattern: /^\/api\/comment\/(\d+)$/, handler: (c) => {
      const user = currentUser(c.token)
      if (!user) return fail(401, '请先登录')
      return heritage.deleteMyComment(Number(c.match[1]), user.nickname) ? {} : fail(404, '评论不存在或无权删除')
    } },
  // 收藏
  { method: 'GET', pattern: /^\/api\/collection\/my$/, handler: () => heritage.myCollections() },
  { method: 'POST', pattern: /^\/api\/collection$/, handler: (c) => heritage.addCollection(Number(c.params?.heritageId)) },
  { method: 'DELETE', pattern: /^\/api\/collection$/, handler: (c) => heritage.removeCollection(Number(c.params?.heritageId)) },
  // 测验
  { method: 'GET', pattern: /^\/api\/quiz\/random-questions$/, handler: (c) => heritage.randomQuestions(c.params?.heritageId as number) },
  { method: 'POST', pattern: /^\/api\/quiz\/submit$/, handler: (c) => heritage.judgeAnswer(Number(c.data?.questionId), String(c.data?.answer ?? '')) },
  { method: 'GET', pattern: /^\/api\/quiz\/records$/, handler: () => heritage.quizRecords() },
  { method: 'GET', pattern: /^\/api\/quiz\/total-score$/, handler: () => heritage.totalScore() },
  // 学习进度（个人中心"我的观看" + 课程页进度上报）
  { method: 'PUT', pattern: /^\/api\/progress$/, handler: (c) => {
      const user = currentUser(c.token)
      if (!user) return fail(401, '请先登录')
      heritage.saveProgress({
        courseId: Number(c.data?.courseId),
        chapterId: Number(c.data?.chapterId),
        studyDuration: Number(c.data?.studyDuration) || 0,
        finished: c.data?.finished ? 1 : 0
      })
      return {}
    } },
  { method: 'GET', pattern: /^\/api\/progress\/my$/, handler: (c) => heritage.myProgress(c.params?.courseId as number | undefined) },
  { method: 'GET', pattern: /^\/api\/progress\/latest$/, handler: () => heritage.getLatest() },
  // 文件上传（mock 演示：返回内置占位头像，真实后端为 /files/** 静态地址）
  { method: 'POST', pattern: /^\/api\/file\/upload$/, handler: () => defaultCover('用户头像', 200, 200) },
  // 用户内容发布与审核（docs/17：用户提交 → 管理员审核 → 通过后上线）
  { method: 'POST', pattern: /^\/api\/submission$/, handler: (c) => {
      const user = currentUser(c.token)
      if (!user) return fail(401, '请先登录')
      const title = String(c.data?.title ?? '').trim()
      if (!title) return fail(400, '标题不能为空')
      const heritageId = c.data?.heritageId ? Number(c.data.heritageId) : null
      const sub = submissions.submit({
        type: String(c.data?.type ?? 'news'),
        title,
        heritageId,
        heritageName: heritage.detail(heritageId ?? 0)?.name ?? '',
        content: String(c.data?.content ?? ''),
        videoUrl: String(c.data?.videoUrl ?? '')
      }, user.nickname)
      return sub
    } },
  { method: 'GET', pattern: /^\/api\/submission\/my$/, handler: (c) => {
      const user = currentUser(c.token)
      if (!user) return fail(401, '请先登录')
      return submissions.mySubmissions(user.nickname)
    } },
  { method: 'GET', pattern: /^\/api\/admin\/submission\/list$/, handler: (c) => {
      const user = currentUser(c.token)
      if (!user || user.role !== 'admin') return fail(403, '仅管理员可访问')
      const status = c.params?.status === undefined || c.params?.status === '' ? undefined : Number(c.params.status)
      return submissions.adminList(status)
    } },
  { method: 'POST', pattern: /^\/api\/admin\/submission\/(\d+)\/review$/, handler: (c) => {
      const user = currentUser(c.token)
      if (!user || user.role !== 'admin') return fail(403, '仅管理员可审核')
      const status = Number(c.data?.status)
      if (status !== 1 && status !== 2) return fail(400, '审核状态非法')
      const reviewed = submissions.review(Number(c.match[1]), status, String(c.data?.reviewNote ?? ''), user.nickname)
      return reviewed ? {} : fail(404, '记录不存在或已审核')
    } },
  // 用户认证
  { method: 'POST', pattern: /^\/api\/user\/login$/, handler: (c) => {
      const username = String(c.data?.username ?? '').trim()
      const password = String(c.data?.password ?? '')
      if (!username || !password) return fail(400, '用户名与密码不能为空')
      const role = username === 'admin' ? 'admin' : 'user'
      const user: MockUser = role === 'admin'
        ? adminUser
        : profileDb[username] || { nickname: username, role, avatar: '', phone: '', email: '', introduction: '' }
      profileDb[username] = user
      saveProfiles()
      return { token: `mock-token|${user.nickname}|${role}`, user }
    } },
  { method: 'POST', pattern: /^\/api\/user\/register$/, handler: (c) => {
      const username = String(c.data?.username ?? '').trim()
      if (!username) return fail(400, '用户名不能为空')
      profileDb[username] = profileDb[username] || { nickname: username, role: 'user', avatar: '', phone: '', email: '', introduction: '' }
      return {}
    } },
  // 个人资料（个人中心"个人资料"Tab）
  { method: 'GET', pattern: /^\/api\/user\/profile$/, handler: (c) => {
      const user = currentUser(c.token)
      if (!user) return fail(401, '请先登录')
      return user
    } },
  { method: 'PUT', pattern: /^\/api\/user\/profile$/, handler: (c) => {
      const user = currentUser(c.token)
      if (!user) return fail(401, '请先登录')
      user.nickname = String(c.data?.nickname ?? user.nickname)
      user.phone = String(c.data?.phone ?? '')
      user.email = String(c.data?.email ?? '')
      user.avatar = String(c.data?.avatar ?? '')
      user.introduction = String(c.data?.introduction ?? '')
      profileDb[user.nickname] = user
      saveProfiles()
      return user
    } }
]

// ---------- 管理端兜底 ----------
// mock 阶段后台管理其余模块未实现（直连真实后端使用）。这里提供结构化空数据兜底，
// 使 mock 模式下后台各页面可打开、无报错弹窗（列表为空 / 写操作静默成功）。
routes.push(
  { method: 'GET', pattern: /^\/api\/admin\/[^/]+\/page$/, handler: () => ({ records: [], total: 0 }) },
  { method: 'GET', pattern: /^\/api\/admin\/(inheritor|chapter|history)\/list$/, handler: () => [] },
  { method: 'GET', pattern: /^\/api\/admin\/[^/]+\/latest$/, handler: () => [] },
  { method: 'GET', pattern: /^\/api\/admin\/dashboard\/[a-z-]+$/, handler: () => ({}) },
  { method: 'GET', pattern: /^\/api\/admin\//, handler: () => [] },
  { method: 'POST', pattern: /^\/api\/admin\//, handler: () => ({}) },
  { method: 'PUT', pattern: /^\/api\/admin\//, handler: () => ({}) },
  { method: 'DELETE', pattern: /^\/api\/admin\//, handler: () => ({}) }
)

// ---------- 统一 mock 请求入口（形状对齐 axios 实例） ----------
export async function mockRequest(config: { url: string; method: string; params?: Record<string, unknown>; data?: Record<string, unknown>; headers?: Record<string, string> }): Promise<{ code: number; msg: string; data: unknown }> {
  // 模拟网络延迟
  await new Promise((resolve) => setTimeout(resolve, 300))

  const method = config.method.toUpperCase()
  const route = routes.find((r) => r.method === method && r.pattern.test(config.url))
  if (!route) {
    const msg = `[mock] 未实现的接口：${method} ${config.url}（后台管理模块不在本次 mock 范围）`
    console.error(msg)
    ElMessage.error(msg)
    return Promise.reject(new Error(msg))
  }

  const match = config.url.match(route.pattern) as RegExpMatchArray
  // mock 通道的"请求头"：直接从用户状态读取登录令牌（等价于 axios 请求拦截器注入 Authorization）
  const token = useUserStore().token || undefined
  const raw = route.handler({ params: config.params, data: config.data, token, match })
  // 处理器可返回原始数据（自动包装 Result）或已包装的 { code, msg, data }（如 401/400 分支）
  const res = raw && typeof raw === 'object' && typeof (raw as { code?: unknown }).code === 'number'
    ? raw
    : ok(raw)

  // 与 axios 响应拦截器保持一致的业务码处理
  if (res.code === 401) {
    const userStore = useUserStore()
    userStore.logout()
    ElMessage.error(res.msg || '登录已过期，请重新登录')
    const isAdminPath = router.currentRoute.value.path.startsWith('/admin')
    router.push(isAdminPath ? '/admin/login' : '/login')
    return Promise.reject(new Error(res.msg))
  }
  if (res.code !== 200) {
    ElMessage.error(res.msg || '操作失败')
    return Promise.reject(new Error(res.msg || 'Error'))
  }
  // 深拷贝，防止页面直接改写 mock 数据
  return JSON.parse(JSON.stringify(res))
}

/** 供调试/文档展示：当前 mock 点位数量 */
export const mockPointCount = mapPoints.length
