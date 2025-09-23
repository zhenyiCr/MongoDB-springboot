import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {   path: '/', redirect: '/manager/home'},
        {
            path: '/manager', component:() => import('../views/Manager.vue'),
            children: [
                {path: 'home',meta: {name: '主页'}, component:() => import('../views/home.vue')},
                {path: 'users',meta: {name: '用户管理'}, component:() => import('../views/users.vue')},
            ]
        },
        {path: '/login', component: import('../views/Login.vue'),},

    ],
})

export default router
