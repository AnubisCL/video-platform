import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';

// 静态路由
const constantRoutes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'Index',
        meta: {
            title: '首页',
            keepAlive: true,
            requireAuth: true
        },
        component: () => import('@/pages/index.vue')
    },
    {
        path: '/login',
        name: 'Login',
        meta: {
            title: '登录',
            keepAlive: true,
            requireAuth: false
        },
        component: () => import('@/pages/login.vue')
    }
]

//动态路由 asyncRoutes
export const asyncRoutes = []

const router = createRouter({
    history: createWebHistory(),
    routes: constantRoutes
});
export default router;
