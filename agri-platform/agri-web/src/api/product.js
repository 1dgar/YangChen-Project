import request from '../utils/request'

export const getProductList = (params) => request.get('/product/list', { params })
export const getProductDetail = (id) => request.get(`/product/${id}`)
export const getMyProducts = (userId, params) => request.get(`/product/my/${userId}`, { params })
export const publishProduct = (data) => request.post('/product', data)
export const updateProduct = (id, data) => request.put(`/product/${id}`, data)
export const deleteProduct = (id) => request.delete(`/product/${id}`)
export const getCategories = () => request.get('/market/categories')
