import request from '../utils/request'

export const getProductComments = (productId, params) => request.get(`/comment/product/${productId}`, { params })
export const addComment = (data) => request.post('/comment', data)
export const replyComment = (id, data) => request.put(`/comment/${id}/reply`, data)
export const getMyComments = (userId, params) => request.get(`/comment/my/${userId}`, { params })
export const deleteComment = (id) => request.delete(`/comment/${id}`)
