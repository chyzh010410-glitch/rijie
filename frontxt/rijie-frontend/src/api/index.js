// src/api/index.js
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '@/router'

// 1. 创建Axios实例（全局配置）
const request = axios.create({
  baseURL: '/api', // 对应Vite代理的前缀（后续配置Vite代理）
  timeout: 5000,   // 请求超时时间
  headers: {       // 默认请求头
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 2. 请求拦截器：自动加Token、统一请求参数
request.interceptors.request.use(
  (config) => {
    // 从localStorage取登录后的Token（登录成功后要存Token）
    const token = localStorage.getItem('token')
    if (token) {
      // 与后端约定的Token格式（Bearer + Token）
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    ElMessage.error('请求参数格式错误')
    return Promise.reject(error)
  }
)

// 3. 响应拦截器：统一处理后端Result、错误提示、Token过期
request.interceptors.response.use(
  (response) => {
    // 后端返回格式是统一的Result（{code, message, data}）
    const res = response.data

    // 成功码：后端约定code=200为成功
    if (res.code !== 200) {
      // 提示后端返回的错误信息
      // ElMessage.error(res.message || '操作失败')

      // 处理Token过期（后端约定code=401为Token无效/过期）
      if (res.code === 401) {
        ElMessageBox.confirm(
          '登录状态已过期，请重新登录',
          '提示',
          { type: 'warning' }
        ).then(() => {
          // 清除过期Token
          localStorage.removeItem('token')
          // 跳转到登录页
          router.push('/login')
        })
      }

      // 抛错让业务代码catch
      return Promise.reject(new Error(res.message || '操作失败'))
    }

    // 成功：只返回data（剥离外层code/message）
    return res.data
  },
  (error) => {
    // 处理网络/后端状态码错误
    let errorMsg = '请求失败'
    if (error.response) {
      // 后端返回的状态码错误（404/500等）
      const status = error.response.status
      switch (status) {
        case 404: errorMsg = '接口不存在'; break
        case 500: errorMsg = '后端服务异常'; break
        case 403: errorMsg = '没有操作权限'; break
        default: errorMsg = error.response.data?.message || '请求异常'
      }
    } else if (error.message.includes('Network Error')) {
      errorMsg = '网络错误：请检查后端是否启动'
    } else if (error.message.includes('timeout')) {
      errorMsg = '请求超时'
    }

    // 统一提示错误
    ElMessage.error(errorMsg)
    return Promise.reject(error)
  }
)

export default request // 导出全局Axios实例