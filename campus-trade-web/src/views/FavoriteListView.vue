<template>
  <div class="products-page">
    <header>
      <h1>收藏列表</h1>
      <button @click="goBack">返回</button>
    </header>

    <p class="error" v-if="errorMessage">{{errorMessage}}</p>
    <ToastMessage
      :message="messageText"
      :type="messageType"
      />

    <div class="grid" v-if="products.length !== 0">

      <div class="card" v-for="product in products" :key="product.id" @click="searchProductDetails(product.id)">
        <img
            v-if="product.imageUrl"
            :src="API_BASE_URL+product.imageUrl"
        />
        <div class="image-placeholder" v-else>暂无图片</div>

        <h3>{{product.title}}</h3>
        <p>{{product.description}}</p>


        <div class="card-footer">
          <strong>{{product.price}}元</strong>
          <button class="secondary-btn" @click.stop="removeFavorite(product.id)">取消收藏</button>
        </div>
      </div>
    </div>

    <div class="empty" v-else>暂无收藏商品</div>

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
import {onMounted,ref} from "vue";
import {useRouter} from "vue-router";
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

async function loadFavorites(){
  errorMessage.value = ''
  try {
    const res = await request.get('/favorites?page='+page.value+'&pageSize='+pageSize.value)
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
    errorMessage.value = '查询收藏列表失败'
  }
}

async function reloadAfterRemove(){
  await loadFavorites()
  if (page.value > 1 && products.value.length === 0){
    page.value = page.value - 1
    await loadFavorites()
  }
}

async function removeFavorite(id){
  errorMessage.value = ''
  try {
    const res = await request.delete('/favorites/'+id)
    if (res.data.code === 200){
      showMessage('移除收藏成功','success')
      await reloadAfterRemove()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '移除收藏失败，请稍后再试'
  }
}

async function prevPage(){
  if (page.value<=1){
    return
  }
  page.value = page.value - 1
  await loadFavorites()
}

async function nextPage(){
  if (page.value>=pages.value){
    return
  }
  page.value = page.value + 1
  await loadFavorites()
}

function searchProductDetails(id) {
  router.push("/products/"+ id)
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

onMounted(()=>{
  loadFavorites()
})
</script>

