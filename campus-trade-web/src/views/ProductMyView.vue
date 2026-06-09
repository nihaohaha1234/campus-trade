//个人发布的商品页面
<template>

  <div class="products-page">
    <button @click="goBack" class="back-btn">返回</button>
    <header>
      <h1>我的商品</h1>
      <button @click="searchMyProducts(null)">全部</button>
      <button @click="searchMyProducts(0)">待审核</button>
      <button @click="searchMyProducts(1)">已上架</button>
      <button @click="searchMyProducts(2)">已下架</button>
      <button @click="searchMyProducts(3)">已售出</button>
      <button @click="searchMyProducts(4)">已锁定</button>
    </header>

    <div class="grid" v-if="products.length!==0">

      <div class="card" v-for="product in products" :key="product.id" @click = searchMyProductDetails(product.id)>
        <img
            v-if="product.imageUrl"
            :src="'http://localhost:8080'+product.imageUrl"
        />
        <div class="image-placeholder" v-else>暂无图片</div>

        <h3>{{product.title}}</h3>
        <p>{{product.description}}</p>
        <p>{{getStatusName(product.status)}}</p>
        <strong>{{product.price}}</strong>
      </div>
    </div>
    <div v-else class="loading">暂无商品</div>
    <p class="error" v-if="errorMessage">{{ errorMessage }}</p>
  </div>
  <div class="pagination">
    <button
        class="page-btn"
        :disabled="page<=1"
        @click="prevPage()">
      上一页
    </button>

    <span class="page-info">
          第{{page}}页，共{{pages}}页
        </span>

    <button
        class="page-btn"
        :disabled="page>=pages"
        @click="nextPage()">
      下一页
    </button>

    <span class="total-info">共{{total}}条</span>
  </div>


</template>

<script setup>
import {ref,onMounted} from "vue";
import {useRouter} from "vue-router";
import request from "../api/request.js";

const router = useRouter()
const products= ref([])
const errorMessage = ref('')
const status = ref(null)
const page = ref(1)
const pages = ref(1)
const pageSize = ref(20)
const total = ref(0)


async function getMyProducts(){
  let res
  errorMessage.value = ''
  try {
    if(status.value === null){
      res = await request.get('/products/my?page='+page.value+'&pageSize='+pageSize.value)
    }else{
      res = await request.get('/products/my?status='+status.value+'&page='+page.value+'&pageSize='+pageSize.value)
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

async function searchMyProducts(currentStatus){
  status.value = currentStatus
  page.value = 1
  await getMyProducts()
}

async function prevPage(){
  if (page.value<=1){
    return
  }
  page.value = page.value - 1
  await getMyProducts()
}

async function nextPage(){
  if (page.value>=pages.value){
    return
  }
  page.value = page.value + 1
  await getMyProducts()
}

function searchMyProductDetails(id){
  router.push('/products/my/'+id)
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

function goBack(){
  router.push('/products')
}

onMounted(()=>{
  getMyProducts()
})
</script>

