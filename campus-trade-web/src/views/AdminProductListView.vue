//管理员商品列表页面
<template>
  <div class="products-page">
    <header>
      <h1>商品列表</h1>
      <button @click="logout">退出登录</button>
      <button @click="goBack">返回控制台</button>
    </header>

    <div class="search-bar">
      <input
          v-model="keyWord"
          placeholder="搜索商品标题或描述"
          @keyup.enter="searchProducts"
      />
      <button @click="searchProducts">搜索</button>
      <button @click="resetSearch">重置</button>
    </div>

    <p class="error" v-if="errorMessage">{{ errorMessage }}</p>

    <div class="grid">

      <div class="card" v-for="product in products" :key="product.id" @click="searchProductDetailsForAdmin(product.id)">
        <img
            v-if="product.imageUrl"
            :src="API_BASE_URL+product.imageUrl"
        />
        <div class="image-placeholder" v-else>暂无图片</div>

        <h3>{{product.title}}</h3>
        <p>{{product.description}}</p>
        <p>{{getStatusName(product.status)}}</p>
        <strong>{{product.price}}</strong>
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
import {onMounted, ref} from "vue";
import request from "../api/request.js";
import { API_BASE_URL } from '../api/config.js'

const router = useRouter()
const products = ref([])
const keyWord = ref('')
const errorMessage = ref('')
const page = ref(1)
const pages = ref(1)
const pageSize = ref(20)
const total = ref(0)

async function loadProducts(){
  errorMessage.value = ''
  const word = keyWord.value.trim()
  let res
  try {
    if (!word){
      res = await request.get('/admin/products?page='+page.value+'&pageSize='+pageSize.value)
    }else {
      res = await request.get('/admin/products/search?keyWord='+encodeURIComponent(word)+'&page='+page.value+'&pageSize='+pageSize.value)
    }
    if (res.data.code === 200){
      products.value = res.data.data.records
      page.value = res.data.data.current
      pages.value = res.data.data.pages
      total.value = res.data.data.total
      pageSize.value = res.data.data.size
    }else{
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '查看商品列表失败'
  }
}

async function searchProducts(){
  page.value = 1
  await loadProducts()
}

async function prevPage(){
  if (page.value<=1){
    return
  }
  page.value = page.value - 1
  await loadProducts()
}

async function nextPage(){
  if (page.value>=pages.value){
    return
  }
  page.value = page.value + 1
  await loadProducts()
}

function searchProductDetailsForAdmin(id){
  router.push('/admin/products/'+id)
}

function getStatusName(status){
  switch (status){
    case 0:return '待审核'
    case 1:return '已上架'
    case 2:return '已下架'
    case 3:return '已售出'
    case 4:return '已锁定'
    default:return '未知状态'
  }
}

function logout(){
  localStorage.removeItem("token")
  localStorage.removeItem("user")
  router.push("/login")
}

function goBack(){
  router.push("/admin")
}

function resetSearch(){
  keyWord.value = ''
  page.value = 1
  loadProducts()
}

onMounted(()=>{
  loadProducts()
})
</script>

