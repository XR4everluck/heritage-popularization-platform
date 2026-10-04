import request from '../utils/request'

/**
 * 文件上传：type = image（图片，10MB内） / video（视频，500MB内）
 * @returns {Promise<string>} 文件访问 URL
 */
export function uploadFile(file, type = 'image') {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/file/upload', formData, {
    params: { type },
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 600000
  })
}
