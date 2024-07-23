import { defineStore } from 'pinia'

export const usePermissionStore = defineStore({
    id: 'permission', // id必填，且需要唯一
    state: () => {
        return {
            code: ['customer:add', 'customer:edit', 'customer:delete']
        }
    },
    actions: {
        generateRoutes(name) {

        }
    }
})
