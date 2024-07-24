import { defineStore } from 'pinia'

export const useUserStore = defineStore({
    id: 'user', // id必填，且需要唯一
    state: () => {
        return {
            userId: '',
            username: '',
            roleId: '',
            roleName: '',
            status: '',
            email: ''
        }
    },
    actions: {
        setUser(user) {
            this.userId = user.userId
            this.username = user.username
            this.roleId = user.roleId
            this.roleName = user.roleName
            this.status = user.status
            this.email = user.email
        },
        getRoutes() {

            return []
        }
    }
})
