import request from '../utils/request'

// ========== 后台管理接口（均需 admin 角色登录） ==========

// 分类管理
export const categoryApi = {
  page: (params) => request.get('/api/admin/category/page', { params }),
  add: (data) => request.post('/api/admin/category', data),
  update: (data) => request.put('/api/admin/category', data),
  remove: (id) => request.delete(`/api/admin/category/${id}`)
}

// 非遗项目管理
export const heritageApi = {
  page: (params) => request.get('/api/admin/heritage/page', { params }),
  add: (data) => request.post('/api/admin/heritage', data),
  update: (data) => request.put('/api/admin/heritage', data),
  remove: (id) => request.delete(`/api/admin/heritage/${id}`)
}

// 非遗历史节点管理
export const historyApi = {
  listByHeritage: (heritageId) => request.get('/api/admin/history/list', { params: { heritageId } }),
  add: (data) => request.post('/api/admin/history', data),
  update: (data) => request.put('/api/admin/history', data),
  remove: (id) => request.delete(`/api/admin/history/${id}`)
}

// 课程与章节管理
export const courseApi = {
  page: (params) => request.get('/api/admin/course/page', { params }),
  add: (data) => request.post('/api/admin/course', data),
  update: (data) => request.put('/api/admin/course', data),
  remove: (id) => request.delete(`/api/admin/course/${id}`),
  chapterList: (courseId) => request.get('/api/admin/chapter/list', { params: { courseId } }),
  chapterAdd: (data) => request.post('/api/admin/chapter', data),
  chapterUpdate: (data) => request.put('/api/admin/chapter', data),
  chapterRemove: (id) => request.delete(`/api/admin/chapter/${id}`)
}

// 用户管理
export const userApi = {
  page: (params) => request.get('/api/admin/user/page', { params }),
  updateStatus: (id, status) => request.put(`/api/admin/user/${id}/status`, null, { params: { status } })
}

// 评论管理
export const commentApi = {
  page: (params) => request.get('/api/admin/comment/page', { params }),
  updateStatus: (id, status) => request.put(`/api/admin/comment/${id}/status`, null, { params: { status } }),
  remove: (id) => request.delete(`/api/admin/comment/${id}`)
}

// 轮播图管理
export const bannerApi = {
  page: (params) => request.get('/api/admin/banner/page', { params }),
  add: (data) => request.post('/api/admin/banner', data),
  update: (data) => request.put('/api/admin/banner', data),
  remove: (id) => request.delete(`/api/admin/banner/${id}`)
}

// 公告管理
export const noticeApi = {
  page: (params) => request.get('/api/admin/notice/page', { params }),
  add: (data) => request.post('/api/admin/notice', data),
  update: (data) => request.put('/api/admin/notice', data),
  remove: (id) => request.delete(`/api/admin/notice/${id}`)
}

// 冷知识管理
export const tipApi = {
  page: (params) => request.get('/api/admin/tip/page', { params }),
  add: (data) => request.post('/api/admin/tip', data),
  update: (data) => request.put('/api/admin/tip', data),
  remove: (id) => request.delete(`/api/admin/tip/${id}`)
}

// 传承人管理
export const inheritorApi = {
  page: (params) => request.get('/api/admin/inheritor/page', { params }),
  add: (data) => request.post('/api/admin/inheritor', data),
  update: (data) => request.put('/api/admin/inheritor', data),
  remove: (id) => request.delete(`/api/admin/inheritor/${id}`),
  get: (id) => request.get(`/api/admin/inheritor/${id}`),
  updateStatus: (id, status) => request.put(`/api/admin/inheritor/status/${id}`, null, { params: { status } }),
  list: () => request.get('/api/admin/inheritor/list')
}

// 题库管理
export const quizQuestionApi = {
  page: (params) => request.get('/api/admin/quiz-question/page', { params }),
  add: (data) => request.post('/api/admin/quiz-question', data),
  update: (data) => request.put('/api/admin/quiz-question', data),
  remove: (id) => request.delete(`/api/admin/quiz-question/${id}`),
  get: (id) => request.get(`/api/admin/quiz-question/${id}`),
  updateStatus: (id, status) => request.put(`/api/admin/quiz-question/status/${id}`, null, { params: { status } }),
  getByHeritage: (heritageId) => request.get(`/api/admin/quiz-question/heritage/${heritageId}`),
  batchImport: (data) => request.post('/api/admin/quiz-question/batch-import', data),
  getRandom: (params) => request.get('/api/admin/quiz-question/random', { params })
}

// 冷知识管理（新接口）
export const heritageTipApi = {
  page: (params) => request.get('/api/admin/heritage-tip/page', { params }),
  add: (data) => request.post('/api/admin/heritage-tip', data),
  update: (data) => request.put('/api/admin/heritage-tip', data),
  remove: (id) => request.delete(`/api/admin/heritage-tip/${id}`),
  get: (id) => request.get(`/api/admin/heritage-tip/${id}`),
  updateStatus: (id, status) => request.put(`/api/admin/heritage-tip/status/${id}`, null, { params: { status } }),
  getByHeritage: (heritageId) => request.get(`/api/admin/heritage-tip/heritage/${heritageId}`),
  randomOne: (params) => request.get('/api/admin/heritage-tip/random', { params })
}

// 科普快讯管理
export const heritageNewsApi = {
  page: (params) => request.get('/api/admin/heritage-news/page', { params }),
  add: (data) => request.post('/api/admin/heritage-news', data),
  update: (data) => request.put('/api/admin/heritage-news', data),
  remove: (id) => request.delete(`/api/admin/heritage-news/${id}`),
  get: (id) => request.get(`/api/admin/heritage-news/${id}`),
  updateStatus: (id, status) => request.put(`/api/admin/heritage-news/status/${id}`, null, { params: { status } }),
  updateTop: (id, isTop) => request.put(`/api/admin/heritage-news/top/${id}`, null, { params: { isTop } }),
  getByHeritage: (heritageId) => request.get(`/api/admin/heritage-news/heritage/${heritageId}`),
  getTop: () => request.get('/api/admin/heritage-news/top'),
  getLatest: (limit) => request.get('/api/admin/heritage-news/latest', { params: { limit } })
}

// 数据看板
export const dashboardApi = {
  getBasic: () => request.get('/api/admin/dashboard/basic'),
  getQuizParticipation: () => request.get('/api/admin/dashboard/quiz-participation'),
  getTotalScore: () => request.get('/api/admin/dashboard/total-score'),
  getInheritorCount: () => request.get('/api/admin/dashboard/inheritor-count'),
  getTipCount: () => request.get('/api/admin/dashboard/tip-count'),
  getRegionDistribution: () => request.get('/api/admin/dashboard/region-distribution'),
  getLevelDistribution: () => request.get('/api/admin/dashboard/level-distribution'),
  getQuizTrend: () => request.get('/api/admin/dashboard/quiz-trend'),
  getPopularHeritage: (limit) => request.get('/api/admin/dashboard/popular-heritage', { params: { limit } }),
  getOverview: () => request.get('/api/admin/dashboard/overview')
}
