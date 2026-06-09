//登录页面
<template>
  <div class="page">
    <div class="panel">
      <h1>校园二手交易平台</h1>
      <h2>登录</h2>

      <input v-model="form.username" placeholder="用户名"/>
      <input v-model="form.password" type="password" placeholder="密码"/>

      <button @click="login">登录</button>

      <button class="text-btn" @click="goRegister">还没有账号？立即注册</button>

      <p class="error" v-if="errorMessage">{{errorMessage}}</p>
    </div>
  </div>
</template>

<script setup>
import {reactive,ref} from "vue";
import {useRouter} from "vue-router";
import request from "../api/request.js";

const router = useRouter()

const form = reactive({
  username: '',
  password: ''
})

const errorMessage = ref('')

async function login(){
  errorMessage.value = ''
  try {
    const res = await request.post('/auth/login', form)
    if (res.data.code === 200) {
      localStorage.setItem('token', res.data.data.token)
      localStorage.setItem('user', JSON.stringify(res.data.data.user))
      router.push('/products')
    } else {
      errorMessage.value = res.data.message
    }
  }
  catch (e){
    console.log(e)
    errorMessage.value = '登录失败，请稍后再试'
  }
}

function goRegister(){
  router.push('/register')
}
</script>
