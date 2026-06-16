<template>
  <div class="admin-log-page">
    <header class="page-header">
      <div>
        <h1>AI审核日志</h1>
        <p>查看用户发布商品时的 AI 审核结果</p>
      </div>

      <button class="back-btn" @click="goBack()">
        返回控制台
      </button>
    </header>

    <p class="error" v-if="errorMessage">{{ errorMessage }}</p>

    <div class="table-card">
      <table>
        <thead>
        <tr>
          <th>日志ID</th>
          <th>用户ID</th>
          <th>商品标题</th>
          <th>商品描述</th>
          <th>价格</th>
          <th>AI建议</th>
          <th>审核原因</th>
          <th>创建时间</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="log in logs" :key="log.id">
          <td>{{ log.id }}</td>
          <td>{{ log.userId }}</td>
          <td class="title-cell">{{ log.productTitle }}</td>
          <td class="desc-cell">{{ log.productDescription }}</td>
          <td>{{ log.productPrice }}元</td>
          <td>
            <span :class="log.suggestion === 'PASS' ? 'pass-tag' : 'reject-tag'">
              {{ log.suggestion === 'PASS' ? '通过' : '拒绝' }}
            </span>
          </td>
          <td class="reason-cell">{{ log.reason }}</td>
          <td>{{formatTime(log.createTime)}}</td>
        </tr>
        </tbody>
      </table>

      <div class="empty" v-if="logs.length === 0">
        暂无AI审核日志
      </div>
    </div>

    <div class="pagination">
      <button
          class="page-btn"
          :disabled="page <= 1"
          @click="prevPage">
        上一页
      </button>

      <span class="page-info">
        第{{ page }}页，共{{ pages }}页
      </span>

      <button
          class="page-btn"
          :disabled="page >= pages"
          @click="nextPage">
        下一页
      </button>

      <span class="total-info">共{{ total }}条</span>
    </div>
  </div>
</template>

<script setup>
import {ref,onMounted} from "vue";
import {useRouter} from "vue-router";
import request from "../api/request.js";
const router = useRouter()
const errorMessage = ref('')
const logs = ref([])
const page = ref(1)
const pageSize = ref(10)
const pages = ref(0)
const total = ref(0)


async function loadAIReviewLogs(){
  errorMessage.value = ''
  try {
    const res =await request.get('/admin/ai-review-logs?page='+page.value+"&pageSize="+pageSize.value)
    if (res.data.code === 200){
      logs.value = res.data.data.records
      page.value = res.data.data.current
      pages.value = res.data.data.pages
      total.value = res.data.data.total
      pageSize.value = res.data.data.size
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e){
    console.log(e)
    errorMessage.value = '查询AI审核日志失败，请稍后再试'
  }
}

async function prevPage(){
  if (page.value<=1){
    return
  }
  page.value = page.value - 1
  await loadAIReviewLogs()
}

async function nextPage(){
  if (page.value>=pages.value){
    return
  }
  page.value = page.value + 1
  await loadAIReviewLogs()
}

function goBack(){
  router.push('/admin')
}

function formatTime(time){
  if (!time){
    return
  }
  return time.replace('T',' ')
}

onMounted(()=>{
  loadAIReviewLogs()
})

</script>

