<template>
  <!-- 组件化：登录页作为独立视图组件 -->
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <h2 class="login-title">系统登录</h2>
      <!-- Element Plus表单组件 + 表单验证 -->
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="80px" class="login-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item class="login-btn-item">
          <el-button type="primary" @click="handleLogin" class="login-btn">登录</el-button>
          <!-- 新增注册按钮 -->
          <el-button link @click="toRegister" class="register-btn">还没有账号？立即注册</el-button>
          <!-- 新增：返回首页（退出）按钮 -->
          <el-button link @click="toHome" class="home-btn">返回首页</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
// 组件化：独立的逻辑层
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { loginApi } from '@/api/modules/login'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

// 表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const loginFormRef = ref(null)
const router = useRouter()

// 新增：跳转注册页方法
const toRegister = () => {
  router.push({ name: 'Register' })
}

// 新增：跳转首页（HomeView.vue）
const toHome = () => {
  router.push({ name: 'Home' }) // 匹配路由中name: 'Home'的路径（/）
}

// 角色类型 → 路由首页映射
const roleToHomeMap = {
  0: '/admin/index',   // 管理员 → 管理员首页
  1: '/employer/index', // 雇主 → 雇主首页
  2: '/jobseeker/index' // 求职者 → 求职者首页
}

// 登录核心逻辑
const handleLogin = async () => {
  // 1. 表单验证（捕获验证失败，避免直接报错）
  let isValid = false
  try {
    await loginFormRef.value.validate()
    isValid = true
  } catch (error) {
    ElMessage.warning('请完善用户名和密码！')
    return
  }

  if (!isValid) return

  try {
    // 2. 调用登录接口（传表单对象，而非分开传参！）
    // 注意：响应拦截器已处理Result，返回的是res.data（LoginInfo）
    // const loginInfo = await loginApi(loginForm) 
    // 2. 调用登录接口（分开传username/password，匹配loginApi的参数要求）
    const loginInfo = await loginApi(loginForm.username, loginForm.password)

    const auth = useAuthStore()
    auth.login(loginInfo)

    // 4. 根据角色跳转对应首页
    const homePath = roleToHomeMap[loginInfo.roleType] || '/admin/index'
    router.push(homePath)

    ElMessage.success(`登录成功！欢迎您，${loginInfo.realName || loginInfo.username}`)
  } catch (error) {
    // 捕获接口错误（如用户名密码错误、后端返回code≠200）
    console.error('登录失败：', error)
    ElMessage.error('登录失败！用户名或密码错误，请重试')
  }
}
</script>

<style scoped>
/* 组件化：独立的样式层 */
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  margin-bottom: 10px;
  /* 新增：给注册按钮留空间 */
}

/* 新增：注册按钮样式 */
.register-btn {
  width: 48%;
  /* 并排显示：占48%宽度 */
  color: #409eff;
  margin-right: 4%;
  /* 间距 */
}

/* 首页按钮样式 */
.home-btn {
  width: 48%;
  /* 并排显示：占48%宽度 */
  color: #67c23a;
}
</style>