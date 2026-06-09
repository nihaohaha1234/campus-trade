//管理员商品详情页面
<template>
  <div class="detail-page">
    <button class="back-btn" @click="goBack">返回</button>
    <div class="detail-card" v-if="product">
      <img
          v-if="product.imageUrl"
          class="detail-image"
          :src="'http://localhost:8080'+product.imageUrl"/>

      <div class="detail-image-placeholder" v-else>
        暂无图片
      </div>

      <h1>{{product.title}}</h1>

      <p class="price">{{product.price}}元</p>

      <p class="status">{{getStatusName(product.status)}}</p>

      <p class="description">{{product.description}}</p>
    </div>

    <p v-else class="loading">
      加载中...
    </p>
    <p class="error" v-if="errorMessage">{{errorMessage}}</p>
  </div>
</template>

<script setup>
import {onMounted, ref} from "vue";
import {useRouter,useRoute} from "vue-router";
import request from "../api/request.js";

const route = useRoute()
const router = useRouter()
const product = ref(null)
const id = route.params.id
const errorMessage = ref('')

async function getProductDetailForAdmin(id){
  errorMessage.value = ''
  try {
    const res = await request.get('/admin/products/'+id)
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

onMounted(()=>{
  getProductDetailForAdmin(id)
})

function goBack(){
  router.push('/admin/products')
}

</script>

