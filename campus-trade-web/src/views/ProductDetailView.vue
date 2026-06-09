//商品详情页面
<template>
  <div class="detail-page">
    <button class="back-btn" @click="goBack">返回</button>

    <ToastMessage
      :message="messageText"
      :type="messageType"
      />

    <div class="detail-card" v-if="product">

      <p class="error" v-if="errorMessage">{{errorMessage}}</p>

      <img
        v-if="product.imageUrl"
        class="detail-image"
        :src="'http://localhost:8080'+product.imageUrl"/>

      <div class="detail-image-placeholder" v-else>
        暂无图片
      </div>

      <h1>{{product.title}}</h1>

      <p class="price">{{product.price}}元</p>

      <p class="description">{{product.description}}</p>

      <div class="detail-actions">
        <button
          :class="favorited ? 'favorite-active-btn':'secondary-btn'"
          @click="toggleFavorite">
          {{favorited?'已收藏':'收藏'}}
        </button>
        <button class="primary-btn" @click="addOrder(id)">发起交易</button>
      </div>

    </div>

    <p v-else class="loading">
      加载中...
    </p>

  </div>
</template>

<script setup>

import {onMounted,ref} from "vue";
import  {useRouter,useRoute} from "vue-router";
import request from "../api/request.js";
import ToastMessage from '../components/ToastMessage.vue'


const router = useRouter()
const route = useRoute()
const id = route.params.id
const product = ref(null)
const errorMessage = ref('')
const favorited = ref(false)
const messageText = ref('')
const messageType = ref('')

async function getProductDetail(id){
  errorMessage.value = ''
  try {
    const res = await request.get("/products/"+id)
    if(res.data.code === 200) {
      product.value = res.data.data
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '查询商品详情失败'
  }
}

async function addOrder(id){
  errorMessage.value = ''
  try {
    const res = await request.post('/orders/'+id)
    if (res.data.code === 200){
      showMessage('发起交易成功，等待卖家确认','success')
      setTimeout(()=>{
        router.push('/orders')
      },2000)
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '发起交易失败'
  }
}

async function loadFavoriteStatus(){
  errorMessage.value = ''
  try {
    const res = await request.get('/favorites/'+id+"/isFavorite")
    if (res.data.code === 200){
      favorited.value = res.data.data
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '查看收藏状态失败，请稍后再试'
  }
}

async function toggleFavorite(){
  errorMessage.value = ''
  let res
  try {
    if (favorited.value == false){
      res = await request.post('/favorites/'+id)
    }else {
      res = await request.delete('/favorites/'+id)
    }
    if (res.data.code === 200){
      favorited.value = !favorited.value
      const text =  favorited.value?'收藏成功':'取消收藏成功'
      showMessage(text,'success')
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = favorited.value?'取消收藏失败':'收藏失败'
  }
}

function goBack(){
  router.push("/products")
}

function showMessage(text,type){
  messageText.value = text
  messageType.value = type
  setTimeout(()=>{
    messageText.value = ''
  },2000)
}

onMounted(()=>{
  getProductDetail(id)
  loadFavoriteStatus()
})


</script>

