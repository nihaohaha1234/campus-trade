//发布商品页面
<template>
  <div class="publish-page">
    <div class="publish-card">
      <button class="back-btn" @click="goBack">返回</button>

      <h1>发布商品</h1>

      <div class="form-item">
        <label>商品标题</label>
        <input v-model="form.title" placeholder="请输入商品标题"/>
      </div>

      <div class="form-item">
        <label>商品描述</label>
        <textarea v-model="form.description" placeholder="请输入商品描述"></textarea>
      </div>

      <div class="form-item">
        <label>商品价格</label>
        <input v-model="form.price" type="number" placeholder="请输入商品价格">
      </div>

      <div class="form-item">
        <label>图片地址</label>
        <input type="file" @change="uploadImage"/>
        <img v-if="form.imageUrl" class="preview-image" :src="'http://localhost:8080'+form.imageUrl"/>
      </div>

      <button class="submit-btn" @click="publishProduct">
        提交发布
      </button>

      <p class="error" v-if="errorMessage">{{errorMessage}}</p>
      <p class="success" v-if="successMessage">{{successMessage}}</p>
    </div>
  </div>
</template>

<script setup>
import {reactive,ref} from "vue";
import {useRouter} from "vue-router";
import request from "../api/request.js";

const maxSize = 10*1024*1024
const router = useRouter()
const errorMessage = ref('')
const successMessage = ref('')
const form = reactive({
  title:'',
  description:'',
  price:'',
  imageUrl:''
})

async function publishProduct(){
  errorMessage.value=''
  successMessage.value=''
  try {
    const res = await request.post('/products',form)
    if (res.data.code === 200){
      successMessage.value='发布成功，请等待管理员审核'
      setTimeout(()=>{
        router.push('/products')
      },3000)
    }else{
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value='发布失败，请稍后再试'
  }
}

async function uploadImage(event){
  errorMessage.value=''
  successMessage.value=''
  const file = event.target.files[0]
  if (!file){
    return
  }
  if (!file.type.startsWith('image/')){
    errorMessage.value='只能上传图片'
    return
  }
  if(file.size > maxSize){
    errorMessage.value='图片最大大小为10MB'
    return
  }
  const formData = new FormData();
  formData.append("file",file)
  try{
    const res = await request.post("/files/upload",formData)
    if (res.data.code === 200){
      form.imageUrl = res.data.data
      successMessage.value='图片上传成功'
    }else {
      errorMessage.value = res.data.message
    }
  }catch (e) {
    console.log(e)
    errorMessage.value='上传照片失败'
  }

}

function goBack(){
  router.push('/products')
}

</script>

