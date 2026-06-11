<template>
  <div class="orders-page">
    <header class="orders-header">
      <div>
        <h1>我的订单</h1>
      </div>
      <button class="back-btn" @click="goBack">返回</button>
    </header>

    <div class="order-tabs">
      <button
        :class="{active: orderType === 'buyer'}"
        @click="changeOrderType('buyer')">
        我买到的
      </button>

      <button
          :class="{active: orderType === 'seller'}"
          @click="changeOrderType('seller')">
        我卖出的
      </button>
    </div>

    <div class="status-tabs">
      <button :class="{active: status === null}" @click="changeStatus(null)">全部</button>
      <button :class="{active: status === 0}" @click="changeStatus(0)">待确认</button>
      <button :class="{active: status === 1}" @click="changeStatus(1)">已确认</button>
      <button :class="{active: status === 2}" @click="changeStatus(2)">已完成</button>
      <button :class="{active: status === 3}" @click="changeStatus(3)">已取消</button>
    </div>

    <p class="error" v-if="errorMessage">{{errorMessage}}</p>

    <ToastMessage
        :message="messageText"
        :type="messageType"
    />

    <div class="order-list" v-if="orders.length !== 0">
      <div class="order-card" v-for="order in orders" :key="order.id" @click="goOrderDetails(order.id)">
          <img v-if="order.imageUrl"
               :src="API_BASE_URL+order.imageUrl"/>
          <div class="image-placeholder" v-else>暂无图片</div>

          <div class="order-info">
            <h3>{{order.productTitle}}</h3>
            <p>订单号：{{order.orderNo}}</p>
            <p>价格：{{order.price}}元</p>
            <p>状态：{{getOrderStatusName(order.status)}}</p>
          </div>


        <div class="order-actions">
          <button
            v-if="orderType === 'seller' && order.status === 0"
            class="primary-btn"
            @click.stop="confirmOrder(order.id)">
            确认订单
          </button>

          <button
            v-if="order.status === 0 || order.status === 1"
            class="danger-btn"
            @click.stop="cancelOrder(order.id)">
            取消订单
          </button>

          <button
            v-if="order.status === 1"
            class="primary-btn"
            @click.stop="finishOrder(order.id)">
            完成订单
          </button>
        </div>
      </div>
    </div>

    <div class="empty" v-else>暂无订单</div>

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
import {ref,onMounted} from "vue";
import {useRouter,useRoute} from "vue-router";
import request from "../api/request.js";
import ToastMessage from '../components/ToastMessage.vue'
import { API_BASE_URL } from '../api/config.js'

const router = useRouter()
const route = useRoute()
const orders = ref([])
const orderType = ref(route.query.type || 'buyer')
const status = ref(null)
const errorMessage = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(1)
const pages = ref(0)
const messageText = ref('')
const messageType = ref('')

async function loadOrders(){
  errorMessage.value = ''
  let res
  try {
    if(orderType.value === 'buyer'){
      if (status.value === null){
        res = await request.get('/orders/buyer?page='+page.value+"&pageSize="+pageSize.value)
      }else {
        res = await request.get('/orders/buyer?status='+status.value+'&page='+page.value+'&pageSize='+pageSize.value)
      }
    }else{
      if (status.value === null){
        res = await request.get('/orders/seller?page='+page.value+"&pageSize="+pageSize.value)
      }else {
        res = await request.get('/orders/seller?status='+status.value+'&page='+page.value+'&pageSize='+pageSize.value)
      }
    }
    if (res.data.code === 200){
      orders.value = res.data.data.records
      page.value = res.data.data.current
      pages.value = res.data.data.pages
      total.value = res.data.data.total
      pageSize.value = res.data.data.size
    }else{
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '查询订单列表失败'
  }
}

async function reloadAfterActions(){
  await loadOrders()
  if (page.value > 1 && orders.value.length === 0){
    page.value = page.value - 1
    await loadOrders()
  }
}

async function cancelOrder(id){
  errorMessage.value = ''
  try {
    const res = await request.put('/orders/'+id+'/cancel')
    if(res.data.code === 200){
      showMessage('取消订单成功','success')
      await reloadAfterActions()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '取消订单失败，请稍后再试'
  }
}

async function confirmOrder(id){
  errorMessage.value = ''
  try {
    const res = await request.put('/orders/'+id+'/confirm')
    if(res.data.code === 200){
      showMessage('确认订单成功','success')
      await reloadAfterActions()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '确认订单失败，请稍后再试'
  }
}

async function finishOrder(id){
  errorMessage.value = ''
  try {
    const res = await request.put('/orders/'+id+'/finish')
    if(res.data.code === 200){
      showMessage('完成订单成功','success')
      await reloadAfterActions()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '完成订单失败，请稍后再试'
  }
}


async function changeStatus(statusNum){
  status.value = statusNum
  page.value = 1
  await loadOrders()
}

async function changeOrderType(type){
  orderType.value = type
  page.value = 1
  await loadOrders()
}

async function prevPage(){
  if (page.value<=1){
    return
  }
  page.value = page.value - 1
  await loadOrders()
}

async function nextPage(){
  if (page.value>=pages.value){
    return
  }
  page.value = page.value + 1
  await loadOrders()

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

function showMessage(text,type){
  messageText.value = text
  messageType.value = type
  setTimeout(()=>{
    messageText.value = ''
  },1000)
}

function goBack(){
  router.push('/products')
}

function goOrderDetails(id){
  router.push("/orders/"+id+"?type="+orderType.value)
}

onMounted(()=>{
  loadOrders()
})
</script>

