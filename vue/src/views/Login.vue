<template>
  <div class="bg">
    <div
        style="width: 350px; background-color: white;OPACITY: 0.5; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); padding: 40px 20px">
      <el-form ref="formRef" :model="data.form" :rules="data.rules">
        <div style="margin-bottom: 40px; text-align: center; font-weight: bold; font-size: 24px">欢 迎 登 录</div>
        <el-form-item prop="username">
          <el-input size="large" v-model="data.form.username" autocomplete="off" prefix-icon="User"
                    placeholder="请输入账号"/>
        </el-form-item>
        <el-form-item prop="password">
          <el-input size="large" show-password v-model="data.form.password" autocomplete="off" prefix-icon="Lock"
                    placeholder="请输入密码"/>
        </el-form-item>
        <div style="margin-bottom: 20px">
          <el-button style="width: 100%" size="large" type="primary" @click="login">登录</el-button>
        </div>
        <div style="text-align: right">
          还没有账号？请<a style="color: #2c82ff" href="/register">注册</a>
        </div>

      </el-form>

    </div>
  </div>
</template>

<script setup>
import {reactive,ref} from 'vue'
import {ElMessage} from "element-plus";
import request from "@/utils/request.js";
import router from "@/router/index.js";

const formRef = ref(null)
const data = reactive({
  form: {},
  rules: {
    username: [
      {required: true, message: '请输入账号', trigger: 'blur'},
    ],
    password: [
      {required: true, message: '请输入密码', trigger: 'blur'},
    ],
  },
})
const login = () => {
  console.log(data.form)
  request.post('/login', data.form).then(res => {
    if (res.code === '200') {
      // 存储用户消息
      localStorage.setItem('user', JSON.stringify(res.data ))
      ElMessage.success("登录成功")
      router.push('/')}
    else {
      ElMessage.error(res.msg)
    }
  })
}

let userStr = localStorage.getItem('user')
if (userStr) {
  let user = JSON.parse(userStr)
  if (!user.id) {
    router.push('/')
  }
} else {
  router.push('/login')
}

</script>
<style>
.bg {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background-image: url("");
  background-size: cover;
}

</style>