import request from '../utils/request'

// ========== 前台展示类接口（公开） ==========
export function getCategoryList() {
  return request.get('/api/category/list')
}

export function getHeritagePage(params) {
  return request.get('/api/heritage/page', { params })
}

export function getHeritageDetail(id) {
  return request.get(`/api/heritage/${id}`)
}

export function getHeritageHistory(id) {
  return request.get(`/api/heritage/${id}/history`)
}

export function addHeritageView(id) {
  return request.post(`/api/heritage/${id}/view`)
}

export function getCourseListByHeritage(heritageId) {
  return request.get('/api/course/list', { params: { heritageId } })
}

export function getHotCourses(limit = 4) {
  return request.get('/api/course/hot', { params: { limit } })
}

export function getCourseDetail(id) {
  return request.get(`/api/course/${id}`)
}

export function getCourseChapters(id) {
  return request.get(`/api/course/${id}/chapters`)
}

export function addCourseView(id) {
  return request.post(`/api/course/${id}/view`)
}

export function getBanners() {
  return request.get('/api/portal/banners')
}

export function getNoticePage(params) {
  return request.get('/api/portal/notices', { params })
}

export function getNoticeDetail(id) {
  return request.get(`/api/portal/notices/${id}`)
}

export function getCommentPage(params) {
  return request.get('/api/comment/page', { params })
}

// ========== 需登录的用户操作接口 ==========
export function addCollection(heritageId) {
  return request.post('/api/collection', null, { params: { heritageId } })
}

export function removeCollection(heritageId) {
  return request.delete('/api/collection', { params: { heritageId } })
}

export function getMyCollections() {
  return request.get('/api/collection/my')
}

export function postComment(data) {
  return request.post('/api/comment', data)
}

export function getMyComments(params) {
  return request.get('/api/comment/my', { params })
}

export function deleteMyComment(id) {
  return request.delete(`/api/comment/${id}`)
}

export function updateProgress(data) {
  return request.put('/api/progress', data)
}

export function getMyProgress(params) {
  return request.get('/api/progress/my', { params })
}

export function getLatestProgress() {
  return request.get('/api/progress/latest')
}

// ---------- 非遗小测验相关接口 ----------
export function getRandomQuestions(heritageId) {
  return request.get('/api/quiz/random-questions', { params: { heritageId } })
}

export function submitAnswer(data) {
  return request.post('/api/quiz/submit', data)
}

export function getQuizRecords(userId) {
  return request.get('/api/quiz/records', { params: { userId } })
}

export function getTotalScore() {
  return request.get('/api/quiz/total-score')
}

export function getRecommend(params) {
  return request.get('/api/heritage/recommend', { params })
}

export function getHeritageRecommend(heritageId, limit = 4) {
  return request.get('/api/heritage/recommend', { params: { heritageId, limit } })
}

export function getRegionStats() {
  return request.get('/api/heritage/region/stats')
}

export function getInheritorList() {
  return request.get('/api/inheritor/list')
}

export function getInheritorDetail(id) {
  return request.get(`/api/inheritor/${id}`)
}

// 随机获取一条非遗冷知识（首页「今日非遗」板块）
export function getRandomTip(heritageId) {
  return request.get('/api/heritage/tip', { params: { heritageId } })
}
