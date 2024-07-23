import { request } from '@/utils/request'

export function userinfo(params?: any, data?: any) {
    return request({ url: '/user/userinfo', method: 'get', params, data })
}