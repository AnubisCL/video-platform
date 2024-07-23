import router from './router/index'
import { useUserStore } from '@/store/user'
import { usePermissionStore } from '@/store/permission'
import { getToken } from '@/utils/storage.ts'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false }) // NProgress Configuration
const whiteList = ['/login'] // 白名单
router.beforeEach(async (to) => {
    NProgress.start();
    document.title = `${to.meta.title} | vu3dny-admin`
    const hasToken = getToken('Access-Token')
    const userStore = useUserStore()
    const permissionStore = usePermissionStore()
    if (hasToken) {//判断token是否存在 存在即为已经登录
        if (to.path !== "/login") {
            if (userStore.init) { // 获取了动态路由 init一定true,就无需再次请求 直接放行
                return true
            } else {
                // init为false,一定没有获取动态路由,就跳转到获取动态路由的方法
                const result = await userStore.getRoutes() //获取路由
                const accessRoutes = await permissionStore.generateRoutes(result.menus) //解析路由,存储路由
                console.log(accessRoutes);
                // 动态挂载路由 FIXME
                accessRoutes.forEach((route) => {
                    router.addRoute(route)
                })
                userStore.init = true//init改为true,路由初始化完成
                return { ...to, replace: true }// hack方法 确保addRoute已完成

            }
        }else {
            NProgress.done()
            return '/'
        }

    } else {
        // 白名单，直接放行
        if (whiteList.indexOf(to.path) > -1) {
            return true
        } else {
            return '/login'
        }
        NProgress.done()
    }
})
router.afterEach(() => {
    // finish progress bar
    NProgress.done()
})
