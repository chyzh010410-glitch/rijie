// src/main.js
import { createApp } from 'vue'
import App from './App.vue'
// 引入路由实例（模块化：路由逻辑独立管理）
import router from './router'
// 引入Element Plus组件库及样式（规范化：全局UI库集中注册）
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 可选：引入Element Plus中文语言包（优化体验）
import zhCn from 'element-plus/es/locale/lang/zh-cn'

// 创建Vue实例并挂载全局插件
const app = createApp(App)

// 注册路由
app.use(router)
// 注册Element Plus（配置中文语言）
app.use(ElementPlus, {
  locale: zhCn
})

// 挂载到DOM节点
app.mount('#app')