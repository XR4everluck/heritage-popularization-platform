import request from '../utils/request'

// 用户认证与个人资料接口
export function login(data) {
  return request.post('/api/user/login', data)
}

export function register(data) {
  return request.post('/api/user/register', data)
}

export function getProfile() {
  return request.get('/api/user/profile')
}

export function updateProfile(data) {
  return request.put('/api/user/profile', data)
}
