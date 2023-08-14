import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from "@/views/LoginView";
import ErrorView from "@/views/ErrorView";
import VIP from "@/views/VIP";
// import axios from "axios";
// import {checkToken} from "@/api";

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        redirect: '/home'
    },
    {
        path: '/home',
        name: 'home',
        component: HomeView,
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView
    },
    {
        path: '/vip/:n',
        component: VIP,
        props: true
    },
    {
        path: '/error',
        component: ErrorView
    }
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

// router.beforeEach((to, from, next) => {
//
// })
export default router
