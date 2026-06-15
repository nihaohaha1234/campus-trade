//商品列表页面
<template>
  <div class="products-page">
    <header>
      <h1>商品列表</h1>
      <button @click="logout">退出登录</button>
      <button @click="goPublish">发布商品</button>
      <button @click="goMyProduct">我的商品</button>
      <button @click="goMyOrder">我的订单</button>
      <button @click="goMyFavorite">我的收藏</button>
    </header>

    <p class="error" v-if="errorMessage">{{errorMessage}}</p>

    <div class="search-bar">
      <input
          v-model="keyWord"
          placeholder="搜索商品标题或描述"
          @keyup.enter="searchProducts()"
      />
      <button @click="searchProducts()">搜索</button>
      <button @click="resetSearch">重置</button>
    </div>

   <div class="product-layout">
     <div class="product-main">
       <div class="grid">

         <div class="card" v-for="product in products" :key="product.id" @click="searchProductDetails(product.id)">
           <img
               v-if="product.imageUrl"
               :src="API_BASE_URL+product.imageUrl"
           />
           <div class="image-placeholder" v-else>暂无图片</div>

           <h3>{{product.title}}</h3>
           <p>{{product.description}}</p>
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
     <aside class="hot-panel">
       <h2>热门商品</h2>
       <div class="hot-item" v-for="product in hotProducts" :key="product.id" @click="searchProductDetails(product.id)">
         <img
             v-if="product.imageUrl"
             :src="API_BASE_URL+product.imageUrl"
         />
         <div class="hot-image-placeholder" v-else>无图</div>
         <div class="hot-info">
           <strong>{{product.title}}</strong>
           <span>{{product.price}}元</span>
         </div>
       </div>
     </aside>
   </div>


  </div>
</template>

<script setup>
import {onMounted,ref} from "vue";
import {useRouter} from "vue-router";
import request from "../api/request.js";
import { API_BASE_URL } from '../api/config.js'

const router = useRouter()
const errorMessage = ref('')
const products = ref([])
const hotProducts = ref([])
const keyWord = ref('')
const page = ref(1)
const pages = ref(1)
const pageSize = ref(20)
const total = ref(0)


async function loadProducts(){
  let res
  errorMessage.value = ''
  const word = keyWord.value.trim()
  try {
    if (!word){
      const token = localStorage.getItem('token')
      if (!token){
        res = await request.get('/products?page='+page.value+'&pageSize='+pageSize.value)
      }else {
        res = await request.get('/products/recommend?page='+page.value+'&pageSize='+pageSize.value)
      }
    }else{
      res = await request.get('/products/search?keyWord='+encodeURIComponent(word)+'&page='+page.value+'&pageSize='+pageSize.value)
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
    errorMessage.value = '查询商品列表失败'
  }
}

async function loadHotProducts(){
  errorMessage.value = ''
  try {
    const res = await request.get('/products/hot')
    if (res.data.code === 200){
      hotProducts.value = res.data.data
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '查找热门商品失败，请稍后再试'
  }
}

async function searchProducts(){
  page.value = 1
  await loadProducts()
}
function goPublish(){
  router.push("/products/publish")
}

function resetSearch(){
  keyWord.value=''
  page.value = 1
  loadProducts()
}

function searchProductDetails(id) {
  router.push("/products/"+ id)
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
function logout(){
  localStorage.removeItem("token")
  localStorage.removeItem("user")
  router.push("/login")
}

function goMyProduct(){
  router.push("/products/my")
}

function goMyOrder(){
  router.push('/orders')
}

function goMyFavorite(){
  router.push('/favorites')
}

onMounted(()=>{
  loadProducts()
  loadHotProducts()
})

</script>
