<template>
<div>
    <el-card shadow="hover">
        <h1>Error</h1>
        <a href="/login"> 重新登陆</a>
    </el-card>
</div>
</template>

<script>
export default {
    name: "ErrorView",
    mounted() {
      this.open1()
    },
    methods:{
        open1() {
            this.$notify.error({
                title: '错误',
                message: '操作失败！！',
                duration: 0
            });
        }
    }
}
</script>

<style scoped>

</style>
