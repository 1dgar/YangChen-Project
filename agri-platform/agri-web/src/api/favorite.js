import request from '../utils/request'

export const getMyFavorites = (userId, params) => request.get(`/favorite/my/${userId}`, { params })
export const addFavorite = (data) => request.post('/favorite', data)
export const removeFavorite = (productId) => request.delete(`/favorite/${productId}`)
export const checkFavorite = (productId) => request.get(`/favorite/check/${productId}`)
