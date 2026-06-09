//管理员主页
<template>
  <div class="admin-page">
    <div class="admin-header">
      <div>
        <h1>管理员控制台</h1>
        <p>管理商品审核、用户状态和订单流程</p>
      </div>

      <button class="back-btn" @click="goProducts">
        返回商品列表
      </button>
    </div>

    <div class="admin-grid">
      <div class="admin-card" @click="goAdminProducts">
        <h2>商品列表</h2>
        <p>查看全平台商品，支持搜索和状态查看</p>
        <span>进入管理</span>
      </div>

        <div class="admin-card" @click="goAdminReview">
          <h2>审核管理</h2>
          <p>查看待审核商品，处理通过与拒绝</p>
          <span>进入管理</span>
        </div>

      <div class="admin-card" @click="goAdminUser">
        <h2>用户管理</h2>
        <p>查看用户列表，处理用户禁用与启用。</p>
        <span>进入管理</span>
      </div>

      <div class="admin-card" @click="goAdminOrder">
        <h2>订单管理</h2>
        <p>查看订单状态，跟踪交易流程。</p>
        <span>进入管理</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import {useRouter} from "vue-router";

const router = useRouter()

function goProducts(){
  router.push('/products')
}

function goAdminProducts(){
  router.push('/admin/products')
}

function goAdminReview(){
  router.push('/admin/products/review')
}

function goAdminUser(){
  router.push('/admin/users')
}

function goAdminOrder(){
  router.push('/admin/orders')
}
</script>

