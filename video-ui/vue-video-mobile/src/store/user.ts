import { defineStore } from 'pinia'

export const useUserStore = defineStore({
    id: 'user', // id必填，且需要唯一
    state: () => {
        return {
            name: '张三'
        }
    },
    actions: {
        updateName(name) {
            this.name = name
        },
        getRoutes() {
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    resolve({
                        name: '张三',
                        menus: [
                            {
                                path: '/dashboard',
                                name: 'dashboard',
                                meta: {
                                    title: '首页',
                                    icon: 'dashboard',
                                    affix: true
                                }
                            },
                            {
                                path: '/user',
                               name: 'user',
                            }
                        ]
                    })
                })
            })
        }
    }
})
