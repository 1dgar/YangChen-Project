import request from '../utils/request'

export const getSupplyDemandList = (params) => request.get('/supply-demand/list', { params })
export const getSupplyDemandDetail = (id) => request.get(`/supply-demand/${id}`)
export const getMySupplyDemand = (userId, params) => request.get(`/supply-demand/my/${userId}`, { params })
export const publishSupplyDemand = (data) => request.post('/supply-demand', data)
export const updateSupplyDemand = (id, data) => request.put(`/supply-demand/${id}`, data)
export const deleteSupplyDemand = (id) => request.delete(`/supply-demand/${id}`)
