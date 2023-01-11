import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from "@/views/LoginView";
import ErrorView from "@/views/ErrorView";
import VIP from "@/views/VIP";
// import axios from "axios";
import {checkToken} from "@/api";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect:'/home'
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
    props:true
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


router.beforeEach((to,from,next)=>{

  if (to.path.startsWith('/login')){
    localStorage.removeItem('jwt-token')
    next()
  }else {
    let userinfo=JSON.parse(localStorage.getItem('jwt-token'))
    console.log(userinfo)
    if (!userinfo){
      next('/login')
    }else {
      //校验token合法性
      // axios({
      //   url:'http://localhost:8082/checkToken',
      //   method:'get',
      //   headers:{
      //     'token':userinfo.token
      //   }
      // })
      checkToken(userinfo.token).then((res)=>{
        console.log("check:",res)
        if (res.data){//如果校验成功则放行
          next()
        }else { //小心掉坑: 排除此时地址 = 转向的地址 的情况，避免dead loop.
          if (to.path=='/error'){
            next()
          }else {
            next('/error')
          }
        }
      })
    }
  }
})
export default router
