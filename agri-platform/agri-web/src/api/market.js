import request from '../utils/request'

export const getPrices = (category) => request.get('/market/price', { params: { category } })
export const getTrend = (category, days) => request.get(`/market/trend/${category}`, { params: { days } })
export const getCategories = () => request.get('/market/categories')
export const getNewsList = (params) => request.get('/news/list', { params })
export const getNewsDetail = (id) => request.get(`/news/${id}`)
export const getHomeStats = () => request.get('/market/home-stats')
