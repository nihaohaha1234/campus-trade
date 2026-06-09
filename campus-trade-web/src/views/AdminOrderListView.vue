<template>
  <div class="orders-page">
    <header class="orders-header">
      <div>
        <h1>订单列表</h1>
      </div>
      <button class="back-btn" @click="goBack">返回控制台</button>
    </header>

    <div class="status-tabs">
      <button :class="{active: status === null}" @click="changeStatus(null)">全部</button>
      <button :class="{active: status === 0}" @click="changeStatus(0)">待确认</button>
      <button :class="{active: status === 1}" @click="changeStatus(1)">已确认</button>
      <button :class="{active: status === 2}" @click="changeStatus(2)">已完成</button>
      <button :class="{active: status === 3}" @click="changeStatus(3)">已取消</button>
    </div>

    <p class="error" v-if="errorMessage">{{errorMessage}}</p>

    <table class="admin-table" v-if="orders.length !== 0">
      <thead>
      <tr>
        <th>订单号</th>
        <th>商品</th>
        <th>买家ID</th>
        <th>卖家ID</th>
        <th>价格</th>
        <th>状态</th>
        <th>创建时间</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="order in orders" :key="order.id">
        <td>{{order.orderNo}}</td>

        <td>
          <div class="table-product">
            <img v-if="order.imageUrl"
                 :src="'http://localhost:8080'+order.imageUrl"/>
            <div class="table-image-placeholder" v-else>无图</div>
            <span>{{order.productTitle}}</span>
          </div>
        </td>

        <td>{{ order.buyerId }}</td>
        <td>{{ order.sellerId }}</td>
        <td>{{ order.price }}元</td>
        <td>{{ getOrderStatusName(order.status) }}</td>
        <td>{{ formatTime(order.createTime) }}</td>
      </tr>
      </tbody>
    </table>

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
import {useRouter} from "vue-router";
import request from "../api/request.js";

const orders = ref([])
const errorMessage = ref('')
const router = useRouter()
const status = ref(null)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const pages = ref(0)

async function loadOrders(){
  errorMessage.value = ''
  let res
  try {
    if (status.value === null){
      res = await request.get('/admin/orders?page='+page.value+'&pageSize='+pageSize.value)
    }else{
      res =  await request.get('/admin/orders?status='+status.value+'&page='+page.value+'&pageSize='+pageSize.value)
    }
    if(res.data.code === 200){
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
    errorMessage.value = '查询订单列表失败，请稍后再试'
  }
}

async function changeStatus(statusNum){
  page.value = 1
  status.value = statusNum
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

function goBack(){
  router.push('/admin')
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
    return
  }
  return time.replace('T',' ')
}

onMounted(()=>{
  loadOrders()
})

</script>

