//管理员的用户列表
<template>
  <div class="admin-user-page">
    <header class="admin-user-header">
      <h1>用户管理</h1>
      <button class="back-btn" @click="goBack">返回控制台</button>
    </header>

    <p class="error" v-if="errorMessage">{{errorMessage}}</p>

    <ToastMessage
      :message="messageText"
      :type="messageType"
      />

    <div class="user-table-wrapper">
      <table class="user-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>用户名</th>
          <th>昵称</th>
          <th>角色</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{user.id}}</td>
          <td>{{user.username}}</td>
          <td>{{user.nickname}}</td>
          <td>{{getRoleName(user.role)}}</td>
          <td>{{getStatusName(user.status)}}</td>
          <td>
            <button
                v-if="user.status===1 &&user.role!==1"
                class="disable-btn"
                @click="disableUser(user.id)">禁用
            </button>

            <button v-if="user.status===0"
                    class="enable-btn"
                    @click="enableUser(user.id)">解禁
            </button>

            <span v-if="user.role === 1">管理员</span>
          </td>
        </tr>
        </tbody>
      </table>

      <p class="empty-tip" v-if="users.length ===0">暂无用户</p>

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
  </div>
</template>

<script setup>
import {ref,onMounted} from "vue";
import {useRouter} from "vue-router";
import request from "../api/request.js";
import ToastMessage from '../components/ToastMessage.vue'

const router = useRouter()
const users = ref([])
const errorMessage = ref('')
const page = ref(1)
const pages = ref(1)
const pageSize = ref(10)
const total = ref(0)
const messageText = ref('')
const messageType = ref('')


async function loadUsers(){
  errorMessage.value = ''
  try {
    const res = await request.get('/admin/users?page='+page.value+"&pageSize="+pageSize.value)
    if (res.data.code === 200){
      users.value = res.data.data.records
      page.value = res.data.data.current
      pages.value = res.data.data.pages
      total.value = res.data.data.total
      pageSize.value = res.data.data.size
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '查询用户列表失败'
  }
}

async function disableUser(id){
  errorMessage.value = ''
  try {
    const res = await request.put('/admin/users/'+id+'/disable')
    if (res.data.code === 200){
      showMessage('禁用成功','success')
      await loadUsers()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '禁用该用户失败，请稍后再试'
  }
}

async function enableUser(id){
  errorMessage.value = ''
  try {
    const res = await request.put('/admin/users/'+id+'/enable')
    if (res.data.code === 200){
      showMessage('解禁成功','success')
      await loadUsers()
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value = '解禁该用户失败，请稍后再试'
  }
}

async function prevPage(){
  if (page.value<=1){
    return
  }
  page.value = page.value - 1
  await loadUsers()
}

async function nextPage(){
  if (page.value>=pages.value){
    return
  }
  page.value = page.value + 1
  await loadUsers()
}

function getRoleName(role){
  switch (role){
    case 0:return '普通用户'
    case 1:return '管理员'
    default:return '未知角色'
  }
}

function getStatusName(status){
  switch (status){
    case 0:return '禁用'
    case 1:return '正常'
    default:return '未知状态'
  }
}

function goBack(){
  router.push("/admin")
}

function showMessage(text,type){
  messageText.value = text
  messageType.value = type
  setTimeout(()=>{
    messageText.value = ''
  },1000)
}

onMounted(()=>{
  loadUsers()
})
</script>

