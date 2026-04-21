import request from '../utils/request'

export const chat = (message, history = null) => {
  return request.post('/ai/chat', { message, history })
}

export const chatWithContext = (message, context = null) => {
  return request.post('/ai/chat-with-context', { message, context })
}
