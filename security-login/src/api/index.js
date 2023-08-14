import http from "@/utils/request";
import qs from "qs";
// 请求首页数据
export const getData=(n)=>{
    // 返回promise对象
    return http.get('/vip/'+n)
}
//登陆
export const login=(loginForm)=>{
    return http.post('/auth/login', qs.stringify(loginForm))
}
//校验token合法性
export const checkToken=(token)=>{
    return http({
        url:'/checkToken',
        method:'get',
        headers:{
            'token':token
        }
    })
}
