import request from '../utils/request'

export const getUsers = (params) => request.get('/admin/users', { params })
export const updateUserStatus = (id, data) => request.put(`/admin/user/${id}/status`, data)
export const getAllProducts = (params) => request.get('/admin/products', { params })
export const updateProductStatus = (id, data) => request.put(`/admin/product/${id}/status`, data)
export const addNews = (data) => request.post('/admin/news', data)
export const updateNews = (id, data) => request.put(`/admin/news/${id}`, data)
export const deleteNews = (id) => request.delete(`/admin/news/${id}`)
export const getStatistics = () => request.get('/admin/statistics')
export const getCategoryStats = () => request.get('/admin/category-stats')
export const getRecentActivities = () => request.get('/admin/recent-activities')
