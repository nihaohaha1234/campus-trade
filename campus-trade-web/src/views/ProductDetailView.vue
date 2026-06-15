//商品详情页面
<template>
  <div class="detail-page">
    <button class="back-btn" @click="goBack">返回</button>

    <ToastMessage
      :message="messageText"
      :type="messageType"
      />

    <div class="detail-card" v-if="product">

      <img
        v-if="product.imageUrl"
        class="detail-image"
        :src="API_BASE_URL+product.imageUrl"/>

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
import { API_BASE_URL } from '../api/config.js'


const router = useRouter()
const route = useRoute()
const id = route.params.id
const product = ref(null)
const favorited = ref(false)
const messageText = ref('')
const messageType = ref('')

async function getProductDetail(id){
  try {
    const res = await request.get("/products/"+id)
    if(res.data.code === 200) {
      product.value = res.data.data
    }else {
      showMessage(res.data.message,'error')
    }
  }catch (e) {
    console.log(e)
    showMessage('查询商品详情失败','error')
  }
}

async function addOrder(id){
  const token = localStorage.getItem('token')
  if (!token){
    showMessage("请先登录再发起交易",'error')
    return
  }
  try {
    const res = await request.post('/orders/'+id)
    if (res.data.code === 200){
      showMessage('发起交易成功，等待卖家确认','success')
      setTimeout(()=>{
        router.push('/orders')
      },2000)
    }else {
      showMessage(res.data.message,'error')
    }
  }catch (e) {
    console.log(e)
    showMessage('发起交易失败','error')
  }
}

async function loadFavoriteStatus(){
  try {
    const res = await request.get('/favorites/'+id+"/isFavorite")
    if (res.data.code === 200){
      favorited.value = res.data.data
    }else {
      showMessage(res.data.message,'error')
    }
  }catch (e) {
    console.log(e)
    showMessage('查看收藏状态失败，请稍后再试','error')
  }
}

async function toggleFavorite(){
  const token = localStorage.getItem('token')
  if (!token){
    showMessage("请先登录再收藏",'error')
    return
  }
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
      showMessage(res.data.message,'error')
    }
  }catch (e) {
    console.log(e)
    showMessage(favorited.value?'取消收藏失败':'收藏失败','error')
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
  const token = localStorage.getItem('token')
  getProductDetail(id)
  if (token){
    loadFavoriteStatus()
  }
})


</script>

