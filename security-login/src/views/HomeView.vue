<template>
    <div>
        <el-row type="flex" class="row-bg" justify="end">
            <el-col :span="1.5">
                <el-button type="success" round @click="login"> {{userinfo.username!='' ? userinfo.username :"登陆" }}</el-button>
            </el-col>
            <el-col :span="2">
                <el-button type="danger" round @click="logout">注销</el-button>
            </el-col>
        </el-row>
        <el-row :gutter="20">
            <el-col :span="8">
                <el-card shadow="hover">
                    <el-link type="primary" href="/vip/1">普通用户</el-link>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card shadow="hover">
                    <el-link type="success" href="/vip/2">VIP用户</el-link>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card shadow="hover">
                   <el-link type="primary" href="/vip/3">管理员</el-link>
                </el-card>
            </el-col>
        </el-row>
    </div>

</template>

<script>


export default {
    name: 'HomeView',
    data() {
        return {
            userinfo: {
                username: '',
                password: ''
            }
        }
    },
    mounted() {
        this.infoSave()
    },
    methods: {
        infoSave(){
            const token=localStorage.getItem('jwt-token')
            if (token){
                this.userinfo.username=JSON.parse(token).username
            }
            // console.log(this.userinfo)
        },
        login(){
            if (this.userinfo.username == ''){
                localStorage.removeItem('verify')
                this.$router.push('/login')
            }
        },
        logout(){
            localStorage.removeItem('verify')
            this.$router.push('/api/auth/logout')
        }
    }
}
</script>

<style scoped>

</style>
