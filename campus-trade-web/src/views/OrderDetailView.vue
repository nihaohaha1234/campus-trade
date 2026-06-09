<template>
  <div class="order-detail-page">
    <button class="back-btn" @click="goBack">返回订单列表</button>
    <p class="error" v-if="errorMessage">{{errorMessage}}</p>
    <ToastMessage
        :message="messageText"
        :type="messageType"
    />

    <div class="order-detail-card" v-if="order">

      <div class="order-detail-image-box">
        <img
          v-if="order.imageUrl"
          :src="'http://localhost:8080'+order.imageUrl"
          class="order-detail-image"/>
        <div class="order-detail-placeholder" v-else>暂无图片</div>
      </div>

      <div class="order-detail-info">
        <h1>{{order.productTitle}}</h1>
        <div class="order-info-row">
          <span>订单号</span>
          <strong>{{order.orderNo}}</strong>
        </div>
        <div class="order-info-row">
          <span>商品ID</span>
          <strong>{{order.productId}}</strong>
        </div>
        <div class="order-info-row">
          <span>价格</span>
          <strong class="order-price">{{order.price}}元</strong>
        </div>
        <div class="order-info-row">
          <span>状态</span>
          <strong>{{getOrderStatusName(order.status)}}</strong>
        </div>
        <div class="order-info-row">
          <span>创建时间</span>
          <strong>{{formatTime(order.createTime)}}</strong>
        </div>

        <div class="order-detail-actions">
          <button
            v-if="orderType === 'seller' && order.status === 0"
            class="primary-btn"
            @click="confirmOrder">确认订单
          </button>
          <button
            v-if="order.status === 1"
            class="primary-btn"
            @click="finishOrder">完成订单
          </button>
          <button
            v-if="order.status === 0 || order.status === 1"
            class="danger-btn"
            @click="cancelOrder">取消订单
          </button>
        </div>
      </div>
    </div>
    <p class="loading" v-else>加载中...</p>
  </div>
</template>

<script setup>
import {ref,onMounted} from "vue";
import {useRouter,useRoute} from "vue-router";
import request from "../api/request.js";
import ToastMessage from '../components/ToastMessage.vue'

const route = useRoute()
const router = useRouter()
const errorMessage = ref('')
const order = ref(null)
const id = route.params.id
const orderType = route.query.type || 'buyer'
const messageText = ref('')
const messageType = ref('')

async function loadOrder(){
  errorMessage.value = ''
  try {
    const res = await request.get('/orders/'+id)
    if (res.data.code === 200){
      order.value = res.data.data
    }else{
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '查询该订单失败'
  }
}

async function cancelOrder(){
  errorMessage.value = ''
  try {
    const res = await request.put('/orders/'+id+'/cancel')
    if (res.data.code === 200){
      showMessage('取消订单成功','success')
      await loadOrder()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '取消订单失败，请稍后再试'
  }
}

async function confirmOrder(){
  errorMessage.value = ''
  try {
    const res = await request.put('/orders/'+id+'/confirm')
    if (res.data.code === 200){
      showMessage('确认订单成功','success')
      await loadOrder()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '确认订单失败，请稍后再试'
  }
}

async function finishOrder(){
  errorMessage.value = ''
  try {
    const res = await request.put('/orders/'+id+'/finish')
    if (res.data.code === 200){
      showMessage('完成订单成功','success')
      await loadOrder()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '完成订单失败，请稍后再试'
  }
}

function goBack(){
  router.push('/orders?type='+orderType)
}

function getOrderStatusName(status){
  switch (status){
    case 0:return '待确认'
    case 1:return '已确认'
    case 2:return '已完成'
    case 3:return '已取消'
    default:return '未知状态'
  }
}

function formatTime(time){
  if (!time){
    return ''
  }
  return time.replace('T', ' ')
}

function showMessage(text,type){
  messageText.value = text
  messageType.value = type
  setTimeout(()=>{
    messageText.value = ''
  },1000)
}


onMounted(()=>{
  loadOrder()
})
</script>

