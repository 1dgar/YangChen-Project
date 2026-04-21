import request from '../utils/request'

// 上传文件
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 上传图片
export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
