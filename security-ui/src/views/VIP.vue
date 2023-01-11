<template>
    <el-card shadow="hover">
        <a > {{ msg }}</a>
        <a href="/"> 返回</a>
    </el-card>
</template>

<script>
import {getData} from "@/api";

export default {
    name: "VIP",
    props:['n'],
    data(){
        return{
            msg:null
        }
    },
    mounted() {
        this.getMsg()
    },
    methods:{
        async getMsg(){
            const {data:msg}=await getData(this.n)
            this.msg=msg
        }
    }
}
</script>

<style scoped>

</style>
