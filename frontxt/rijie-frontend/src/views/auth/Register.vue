<template>
  <div class="register-container">
    <el-card class="register-card" shadow="hover">
      <h2 class="register-title">用户注册</h2>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="100px"
        class="register-form"
      >
        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入登录用户名" />
        </el-form-item>
        <!-- 密码 -->
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <!-- 确认密码 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" />
        </el-form-item>
        <!-- 真实姓名 -->
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <!-- 手机号（选填） -->
        <el-form-item label="手机号">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号（选填）" />
        </el-form-item>
        <!-- 邮箱（选填） -->
        <el-form-item label="邮箱">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱（选填）" />
        </el-form-item>
        <!-- 角色选择（必填） -->
        <el-form-item label="注册角色" prop="roleType">
          <el-radio-group v-model="registerForm.roleType">
            <el-radio label="1">雇主</el-radio>
            <el-radio label="2">求职者</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 提交按钮 -->
        <el-form-item class="register-btn-item">
          <el-button type="primary" @click="handleRegister" class="register-btn">确认注册</el-button>
          <el-button link @click="toLogin" class="back-login-btn">已有账号？返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerApi } from '@/api/modules/register'
import { ElMessage } from 'element-plus'

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: '',
  roleType: '' // 1=雇主，2=求职者
})

// 表单验证规则
const registerRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  roleType: [{ required: true, message: '请选择注册角色', trigger: 'change' }]
})

const registerFormRef = ref(null)
const router = useRouter()

// 跳转登录页
const toLogin = () => {
  router.push({ name: 'Login' })
}

// 注册核心逻辑
const handleRegister = async () => {
  // 1. 表单验证
  let isValid = false
  try {
    await registerFormRef.value.validate()
    isValid = true
  } catch (error) {
    ElMessage.warning('请完善必填信息！')
    return
  }
  if (!isValid) return

  try {
    // 2. 转换角色类型为数字（前端是字符串，后端需要Integer）
    const submitData = {
      ...registerForm,
      roleType: Number(registerForm.roleType),
      status: 1 // 账号默认启用（后端sys_user表status字段）
    }
    // 3. 调用注册接口
    const res = await registerApi(submitData)
    // 4. 注册成功：提示+跳登录页
    ElMessage.success('注册成功！请返回登录')
    setTimeout(() => {
      router.push({ name: 'Login' })
    }, 1500)
  } catch (error) {
    // 5. 注册失败：提示错误
    console.error('注册失败：', error)
    ElMessage.error(error.message || '注册失败！用户名已存在或参数错误')
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}
.register-card {
  width: 500px;
}
.register-title {
  text-align: center;
  margin-bottom: 20px;
}
.register-form {
  margin-top: 10px;
}
.register-btn {
  width: 60%;
  margin-right: 10px;
}
.back-login-btn {
  width: 35%;
  color: #409eff;
}
</style>