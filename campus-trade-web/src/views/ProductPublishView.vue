//发布商品页面
<template>
  <div class="publish-page">
    <div class="publish-card">
      <button class="back-btn" @click="goBack">返回</button>

      <h1>发布商品</h1>

      <ToastMessage
        :message="messageText"
        :type="messageType"
        />

      <div class="form-item">
        <label>商品标题</label>
        <input v-model="form.title" placeholder="请输入商品标题"/>
      </div>

      <div class="form-item">
        <label>商品描述</label>
        <textarea v-model="form.description" placeholder="请输入商品描述"></textarea>
      </div>

      <div class="ai-actions">
        <button class="ai-btn" type="button" :disabled="optimizing" @click="optimizeProduct">
          {{optimizing?"生成中":"AI优化描述"}}
        </button>
      </div>

      <div class="form-item">
        <label>商品价格</label>
        <input v-model="form.price" type="number" placeholder="请输入商品价格">
      </div>

      <div class="form-item">
        <label>图片地址</label>
        <input type="file" @change="uploadImage"/>
        <img v-if="form.imageUrl" class="preview-image" :src="API_BASE_URL+form.imageUrl"/>
      </div>

      <button class="submit-btn" :disabled="submitting" @click="publishProduct">
        {{submitting?"提交中":"提交发布"}}
      </button>


    </div>
  </div>
</template>

<script setup>
import {reactive,ref} from "vue";
import {useRouter} from "vue-router";
import request from "../api/request.js";
import ToastMessage from '../components/ToastMessage.vue'
import { API_BASE_URL } from '../api/config.js'

const maxSize = 10*1024*1024
const router = useRouter()
const submitting = ref(false)
const optimizing = ref(false)
const messageText = ref('')
const messageType = ref('')
const form = reactive({
  title:'',
  description:'',
  price:'',
  imageUrl:''
})

async function publishProduct(){
  if(submitting.value === true){
    return
  }
  submitting.value=true
  try {
    const res = await request.post('/products',form)
    if (res.data.code === 200){
      showMessage('发布成功，请等待管理员审核',"success")
      setTimeout(()=>{
        router.push('/products')
      },3000)
    }else{
      showMessage(res.data.message,'error')
    }
  }catch (e) {
    console.log(e)
    showMessage('发布失败，请稍后再试','error')
  }finally {
    submitting.value=false
  }
}

async function uploadImage(event){
  const file = event.target.files[0]
  if (!file){
    return
  }
  if (!file.type.startsWith('image/')){
    showMessage('只能上传图片','error')
    return
  }
  if(file.size > maxSize){
    showMessage('图片最大大小为10MB','error')
    return
  }
  const formData = new FormData();
  formData.append("file",file)
  try{
    const res = await request.post("/files/upload",formData)
    if (res.data.code === 200){
      form.imageUrl = res.data.data
      showMessage("图片上传成功","success")
    }else {
      showMessage(res.data.message,'error')
    }
  }catch (e) {
    console.log(e)
    showMessage('上传照片失败','error')
  }
}

async function optimizeProduct(){
  if (optimizing.value === true){
    return
  }
  if(!form.description || !form.title || !form.price){
    showMessage('标题，描述，价格不能为空','error')
    return
  }
  optimizing.value=true
  try {
    const res = await request.post("/ai/products/optimize",{
      title:form.title,
      description:form.description,
      price:form.price
    },{
      timeout:20000
    })
    if(res.data.code === 200){
      form.title = res.data.data.title
      form.description = res.data.data.description
    }else{
      showMessage(res.data.message,'error')
    }
  }catch (e) {
    console.log(e)
    showMessage('调用AI生成失败','error')
  }finally {
    optimizing.value=false
  }
}

function goBack(){
  router.push('/products')
}

function showMessage(text,type){
  messageText.value = text
  messageType.value = type
  setTimeout(()=>{
    messageText.value = ''
  },1000)
}

</script>

