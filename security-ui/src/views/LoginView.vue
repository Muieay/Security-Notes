<template>
    <el-card class="form-card">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" >
            <el-form-item prop="username">
                <el-input v-model="loginForm.username" type="text" auto-complete="off" placeholder="账号" maxlength="20"
                          prefix-icon="iconfont icon-user" />
            </el-form-item>
            <el-form-item prop="password">
                <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="iconfont icon-lock" maxlength="20"
                          @keyup.enter.native="handleLogin" />
            </el-form-item>
            <el-form-item style="width:100%;">
                <el-button :loading="loading" class="login-btn" size="medium" type="primary" style="width:100%;"
                           @click.native.prevent="handleLogin">
                    <span v-if="!loading">登录</span>
                    <span v-else>登录中...</span>
                </el-button>
            </el-form-item>
        </el-form>
    </el-card>

</template>

<script>
import {login} from "@/api";
export default {
    name: "LoginView",
    data() {
        return {
            loginForm:{
                username: 'admin',
                password: '123456'
            },
            loading: false
        };
    },
    computed: {
        loginRules() {
            const validateUsername = (rule, value, callback) => {
                if (value.length < 1 ) {
                    callback(new Error('请输入用户名'))
                } else {
                    callback()
                }
            }
            const validatePassword = (rule, value, callback) => {
                if (value.length < 6) {
                    callback(new Error('密码必须在6位以上'))
                } else {
                    callback()
                }
            }
            return {
                'username': [{ 'validator': validateUsername, 'trigger': 'blur' }],
                'password': [{ 'validator': validatePassword, 'trigger': 'blur' }]
            }
        }
    },
    methods:{
        handleLogin(){
            this.$refs.loginForm.validate((valid)=>{
                // console.log(valid)
                if (valid){
                    login(this.loginForm).then((res)=>{
                        console.log(typeof res.data)
                        if (res.data!=''){
                            //将token数据存入localStorage，还包含了明文信息，仅用学习
                            localStorage.setItem("jwt-token",JSON.stringify(res.data))
                            this.$router.push("/home")
                        }else {
                            this.$message.error("用户名或密码不正确! 请重新登陆!")
                        }
                    })
                }
            })
        }
    }
}
</script>

<style scoped>
.form-card{
    width: 500px;
    margin: 0 auto;
}
</style>
