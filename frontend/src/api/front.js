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

export function addHeritageView(id) {
  return request.post(`/api/heritage/${id}/view`)
}

export function getCourseListByHeritage(heritageId) {
  return request.get('/api/course/list', { params: { heritageId } })
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
