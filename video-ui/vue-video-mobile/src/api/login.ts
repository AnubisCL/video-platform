import { request } from '@/utils/request'

const auth = '/api/authentication/'
export function signIn(params?: any, data?: any) {
    return request({ url: auth + 'signIn', method: 'post', params, data })
}
export function doLogin(params?: any, data?: any) {
    return request({ url: auth + 'doLogin', method: 'post', params, data })
}
export function isLogin(params?: any, data?: any) {
    return request({ url: auth + 'isLogin', method: 'get', params, data })
}
export function signOut(params?: any, data?: any) {
    return request({ url: auth + 'signOut', method: 'get', params, data })
}
export function getUserInfo(params?: any, data?: any) {
    return request({ url: auth + 'getUserInfo', method: 'get', params, data })
}
export function getPermissionList(params?: any, data?: any) {
    return request({ url: auth + 'getPermissionList', method: 'get', params, data })
}
export function getMenuList(params?: any, data?: any) {
    return request({ url: auth + 'getMenuList', method: 'get', params, data })
}