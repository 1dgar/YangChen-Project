import request from '../utils/request'

export const login = (data) => request.post('/user/login', data)
export const register = (data) => request.post('/user/register', data)
export const getUserInfo = (id) => request.get(`/user/${id}`)
export const updateUser = (id, data) => request.put(`/user/${id}`, data)
export const updatePassword = (data) => request.put('/user/password', data)
export const getCurrentUser = () => request.get('/user/info')
