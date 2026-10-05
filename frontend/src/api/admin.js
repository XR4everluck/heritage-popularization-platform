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
