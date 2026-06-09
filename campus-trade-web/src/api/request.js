import axios from 'axios'
let isRedirecting = false

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000
})

request.interceptors.request.use(config =>{
    const token = localStorage.getItem('token')
    if (token){
        config.headers.Authorization = 'Bearer ' + token
    }
    return config
})

request.interceptors.response.use(res =>{
    const message = res.data.message
    if (message === '请先登录' || message === '登陆状态无效，请重新登录'){
        if(!isRedirecting){
            isRedirecting = true
            alert(message)
            localStorage.removeItem('user')
            localStorage.removeItem('token')
            window.location.href='/login'
        }
    }
    return res
})

export default request