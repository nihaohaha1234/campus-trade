//注册页面
<template>
  <div class="page">
    <div class="panel register-panel">
      <h1>创建账号</h1>
      <p class="form-tip">注册校园二手交易平台账号</p>

      <ToastMessage
          :message="messageText"
          :type="messageType"
      />

      <div class="form-item">
        <label>用户名</label>
        <input v-model="form.username" placeholder="请输入用户名"/>
      </div>

      <div class="form-item">
        <label>密码</label>
        <input v-model="form.password" type="password" placeholder="请输入6至20位密码"/>
      </div>

      <div class="form-item">
        <label>确认密码</label>
        <input v-model="confirmPassword" type="password" placeholder="请再次输入密码"/>
      </div>

      <button class="submit-btn" @click="register">注册</button>

      <button class="text-btn" @click="goLogin">已有帐号？返回登陆</button>

      <p class="error" v-if="errorMessage">{{errorMessage}}</p>
    </div>
  </div>
</template>

<script setup>
import {reactive,ref} from "vue";
import {useRouter} from "vue-router";
import request from "../api/request.js";
import ToastMessage from '../components/ToastMessage.vue'

const router = useRouter()
const errorMessage = ref('')
const confirmPassword = ref('')
const form = reactive({
  username: '',
  password: ''
})
const messageText = ref('')
const messageType = ref('')

async function register(){
  errorMessage.value = ''
  if(form.password !== confirmPassword.value){
    errorMessage.value = '两次输入密码不一致'
    return
  }
  try {
    const res = await request.post('/auth/register',form)
    if (res.data.code === 200){
      showMessage('注册成功','success')
      setTimeout(()=>{
        router.push('/login')
      },1500)
    }else {
      errorMessage.value = res.data.message
      form.username=''
      form.password=''
      confirmPassword.value=''
    }
  }catch (e){
    console.log(e)
    errorMessage.value = '注册失败，请稍后再试'
  }
}

function goLogin(){
  router.push('/login')
}

function showMessage(text,type){
  messageText.value = text
  messageType.value = type
  setTimeout(()=>{
    messageText.value = ''
  },1000)
}

</script>

