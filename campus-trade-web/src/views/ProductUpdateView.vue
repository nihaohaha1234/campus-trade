//更新商品页面
<template>
  <div class="publish-page">
    <div class="publish-card" v-if="product">
      <button class="back-btn" @click="goBack(id)">返回</button>

      <ToastMessage
          :message="messageText"
          :type="messageType"
      />

      <h1>更新商品</h1>

      <div class="form-item">
        <label>商品标题</label>
        <input v-model="product.title" placeholder="请输入商品标题"/>
      </div>

      <div class="form-item">
        <label>商品描述</label>
        <textarea v-model="product.description" placeholder="请输入商品描述"></textarea>
      </div>

      <div class="ai-actions">
        <button class="ai-btn" type="button" :disabled="optimizing" @click="optimizeProduct">
          {{optimizing?"生成中":"AI优化描述"}}
        </button>
      </div>

      <div class="form-item">
        <label>商品价格</label>
        <input v-model="product.price" type="number" placeholder="请输入商品价格">
      </div>

      <div class="form-item">
        <label>图片地址</label>
        <input type="file" @change="uploadImage"/>
        <img v-if="product.imageUrl" class="preview-image" :src="API_BASE_URL+product.imageUrl"/>
      </div>

      <button class="submit-btn" :disabled="submitting" @click="updateProduct(id)">
        {{submitting?"提交中":"提交修改"}}
      </button>

    </div>
  </div>
</template>

<script setup>
import {onMounted,ref} from "vue";
import {useRouter,useRoute} from "vue-router";
import request from "../api/request.js";
import ToastMessage from '../components/ToastMessage.vue'
import { API_BASE_URL } from '../api/config.js'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const id = route.params.id
const maxSize = 10*1024*1024
const messageText = ref('')
const messageType = ref('')
const submitting = ref(false)
const optimizing = ref(false)

async function getMyProductDetail(id){

  try {
    const res = await request.get('/products/my/'+id)
    if (res.data.code === 200){
      product.value = res.data.data
    }else {
      showMessage(res.data.message,"error")
    }
  }catch (e) {
    console.log(e)
    showMessage("查询商品详情失败","error")
  }
}

async function updateProduct(id){
  if (submitting.value === true){
    return
  }
  submitting.value=true
  try {
    const res = await request.put('/products/'+id,product.value)
    if(res.data.code === 200){
      showMessage('更新成功 请等待审核','success')
      setTimeout(()=>{
        router.push('/products/my/'+id)
      },1000)
    }else{
      showMessage(res.data.message,"error")
    }
  }catch (e) {
    console.log(e)
    showMessage("更新失败","error")
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
    showMessage("只能上传图片","error")
    return
  }
  if(file.size > maxSize){
    showMessage("超过最大内存限制","error")
    return
  }

  const formData = new FormData()
  formData.append('file',file)
  try {
    const res = await request.post('/files/upload',formData)
    if (res.data.code === 200){
      product.value.imageUrl = res.data.data
    }else{
      showMessage(res.data.message,"error")
    }
  }catch (e) {
    console.log(e)
    showMessage("更新图片失败","error")
  }
}

async function optimizeProduct(){
  if (optimizing.value === true){
    return
  }
  if(!product.value.description || !product.value.title || !product.value.price){
    showMessage("标题，描述，价格不能为空","error")
    return
  }
  optimizing.value=true
  try {
    const res = await request.post("/ai/products/optimize",{
      title:product.value.title,
      description:product.value.description,
      price:product.value.price
    },{
      timeout:20000
    })
    if(res.data.code === 200){
      product.value.title = res.data.data.title
      product.value.description = res.data.data.description
    }else{
      showMessage(res.data.message,"error")
    }
  }catch (e) {
    console.log(e)
    showMessage("调用AI生成失败","error")
  }finally {
    optimizing.value=false
  }
}

function goBack(id){
  router.push('/products/my/'+id)
}

function showMessage(text,type){
  messageText.value = text
  messageType.value = type
  setTimeout(()=>{
    messageText.value = ''
  },1000)
}

onMounted(()=>{
  getMyProductDetail(id)
})

</script>

