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

      <div class="form-item">
        <label>商品价格</label>
        <input v-model="product.price" type="number" placeholder="请输入商品价格">
      </div>

      <div class="form-item">
        <label>图片地址</label>
        <input type="file" @change="uploadImage"/>
        <img v-if="product.imageUrl" class="preview-image" :src="'http://localhost:8080'+product.imageUrl"/>
      </div>

      <button class="submit-btn" @click="updateProduct(id)">
        提交修改
      </button>

      <p class="error" v-if="errorMessage">{{errorMessage}}</p>
    </div>
  </div>
</template>

<script setup>
import {onMounted,ref} from "vue";
import {useRouter,useRoute} from "vue-router";
import request from "../api/request.js";
import ToastMessage from '../components/ToastMessage.vue'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const errorMessage = ref('')
const id = route.params.id
const maxSize = 10*1024*1024
const messageText = ref('')
const messageType = ref('')

async function getMyProductDetail(id){
  errorMessage.value = ''
  try {
    const res = await request.get('/products/my/'+id)
    if (res.data.code === 200){
      product.value = res.data.data
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '查询商品详情失败'
  }
}

async function updateProduct(id){
  errorMessage.value = ''
  try {
    const res = await request.put('/products/'+id,product.value)
    if(res.data.code === 200){
      showMessage('更新成功 请等待审核','success')
      setTimeout(()=>{
        router.push('/products/my/'+id)
      },1000)
    }else{
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '更新失败'
  }
}

async function uploadImage(event){
  errorMessage.value = ''
  const file = event.target.files[0]

  if (!file){
    return
  }
  if (!file.type.startsWith('image/')){
    errorMessage.value = '只能上传图片'
    return
  }
  if(file.size > maxSize){
    errorMessage.value = '超过最大内存限制'
    return
  }

  const formData = new FormData()
  formData.append('file',file)
  try {
    const res = await request.post('/files/upload',formData)
    if (res.data.code === 200){
      product.value.imageUrl = res.data.data
    }else{
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '更新图片失败'
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

