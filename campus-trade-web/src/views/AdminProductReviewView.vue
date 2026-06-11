//管理员审核商品页面
<template>
  <div class="products-page">
    <header>
      <h1>待审核列表</h1>
      <button @click="logout">退出登录</button>
      <button @click="goBack">返回控制台</button>
    </header>

    <p class="error" v-if="errorMessage">{{ errorMessage }}</p>

    <ToastMessage
      :message="messageText"
      :type="messageType"
      />

    <div class="grid">

      <div class="card" v-for="product in products" :key="product.id" @click="searchProductDetailsForAdmin(product.id)">
        <img
            v-if="product.imageUrl"
            :src="API_BASE_URL+product.imageUrl"
        />
        <div class="image-placeholder" v-else>暂无图片</div>

        <h3>{{product.title}}</h3>
        <p>{{product.description}}</p>
        <strong>{{product.price}}</strong>

         <div class="review-actions">
           <button  class="approve-btn" @click.stop = 'approveProduct(product.id)'>通过</button>
           <button class="reject-btn" @click.stop = 'rejectProduct(product.id)'>拒绝</button>
         </div>
      </div>
    </div>

    <div class="pagination">
      <button
          class="page-btn"
          :disabled="page<=1"
          @click="prevPage">
        上一页
      </button>

      <span class="page-info">
          第{{page}}页，共{{pages}}页
        </span>

      <button
          class="page-btn"
          :disabled="page>=pages"
          @click="nextPage">
        下一页
      </button>

      <span class="total-info">共{{total}}条</span>
    </div>

  </div>
</template>

<script setup>
import {useRouter} from "vue-router";
import {ref,onMounted} from "vue";
import request from "../api/request.js";
import ToastMessage from '../components/ToastMessage.vue'
import { API_BASE_URL } from '../api/config.js'

const router = useRouter()
const errorMessage = ref('')
const products = ref([])
const page = ref(1)
const pages = ref(1)
const pageSize = ref(20)
const total = ref(0)
const messageText = ref('')
const messageType = ref('')

async function getAllPendingProducts(){
  errorMessage.value = ''
  try {
    const res = await request.get('/admin/products/pending?page='+page.value+'&pageSize='+pageSize.value)
    if (res.data.code === 200){
      products.value = res.data.data.records
      page.value = res.data.data.current
      pages.value = res.data.data.pages
      total.value = res.data.data.total
      pageSize.value = res.data.data.size
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '获取待审核商品列表失败'
  }
}

async function approveProduct(id){
  errorMessage.value = ''
  const confirmed = confirm("确认审核通过该商品吗")
  if (!confirmed){
    return
  }
  try {
    const res = await request.put('/admin/products/'+id+'/approve')
    if (res.data.code === 200){
      showMessage('审核通过','success')
      await reloadAfterReview()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '审核通过失败，请稍后再试'
  }
}

async function rejectProduct(id){
  errorMessage.value = ''
  const confirmed = confirm("确认拒绝通过该商品吗")
  if (!confirmed){
    return
  }
  try {
    const res = await request.put('/admin/products/'+id+'/reject')
    if (res.data.code === 200){
      showMessage('已拒绝上架该商品', 'success')
      await reloadAfterReview()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '拒绝上架失败，请稍后再试'
  }
}

async function reloadAfterReview(){
  await getAllPendingProducts()
  if (products.value.length === 0 && page.value > 1){
    page.value = page.value - 1
    await getAllPendingProducts()
  }
}

async function prevPage(){
  if (page.value<=1){
    return
  }
  page.value = page.value - 1
  await getAllPendingProducts()
}

async function nextPage(){
  if (page.value>=pages.value){
    return
  }
  page.value = page.value + 1
  await getAllPendingProducts()
}

onMounted(()=>{
  getAllPendingProducts()
})

function searchProductDetailsForAdmin(id){
  router.push('/admin/products/'+id)
}

function logout(){
  localStorage.removeItem("token")
  localStorage.removeItem("user")
  router.push("/login")
}

function goBack(){
  router.push("/admin")
}

function showMessage(text,type){
  messageText.value = text
  messageType.value = type
  setTimeout(()=>{
    messageText.value = ''
  },1000)
}
</script>

