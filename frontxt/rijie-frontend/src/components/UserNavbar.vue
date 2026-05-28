<template>
  <!-- 导航栏部分不变，只调整表单和逻辑 -->
  <div class="user-navbar">
    <!-- 左侧Logo、右侧用户区、下拉菜单 不变 -->
        <!-- 左侧：首页入口 -->
    <div class="navbar-left">
      <router-link to="/" class="logo">日结兼职平台</router-link>
    </div>

    <!-- 右侧：用户信息+操作区 -->
    <div class="navbar-right">
      <!-- 用户名+下拉菜单 -->
      <el-dropdown @command="handleDropdownCmd">
        <div class="user-info-trigger">
          <span>你好，{{ userInfo.realName ||'用户' }}</span>
          <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
        </div>
        <!-- 下拉菜单列表 -->
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="setting">设置个人信息</el-dropdown-item>
            <el-dropdown-item command="other">其他</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <!-- 退出登录按钮 -->
      <el-button link @click="handleLogout" class="logout-btn">
        退出登录
      </el-button>
    </div>

    <!-- 个人信息修改弹窗（保持原样式） -->
    <el-dialog
      title="设置个人信息"
      v-model="dialogVisible"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="infoFormRef"
        :model="editForm"
        :rules="infoRules"
        label-width="120px"
        class="info-form"
      >
        <!-- 用户名（只读） -->
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <!-- 真实姓名（必填） -->
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <!-- 手机号（可选，填则验证格式） -->
        <!-- 手机号输入框 加 @blur="handleCheckPhone" -->
<el-form-item label="手机号" prop="phone">
  <el-input 
    v-model="editForm.phone" 
    placeholder="选填，如1380013xxxx" 
    @blur="handleCheckPhone"
  />
</el-form-item>
        <!-- 邮箱（可选，填则验证格式） -->
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="选填，如xxx@xxx.com" @blur="handleCheckEmail"/>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateInfo">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ElDropdown, ElDropdownMenu, ElDropdownItem, 
  ElButton, ElDialog, ElForm, ElFormItem, ElInput, ElIcon 
} from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { updateUserInfo, checkPhoneUnique, checkEmailUnique } from '@/api/modules/seeker/user.js'
const router = useRouter()

// 1. 初始化用户信息（兼容phone/email为空）
const userInfo = ref({

  id: '',          // ✅ 必须有！后端修改需要用户ID
  username: '',
  realName: '',
  phone: '',
  email: '',
  roleType: ''
})
// 2. 编辑表单（初始同步userInfo）
const editForm = ref({

  id: '',          // ✅ 必须有！传给后端的核心字段
  username: '',
  realName: '',
  phone: '',
  email: ''
})
const infoFormRef = ref(null)
// 3. 调整验证规则：phone/email为“选填但填则验证”
const infoRules = ref({
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur', required: false }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱', trigger: 'blur', required: false }
  ]
})

const dialogVisible = ref(false)


onMounted(() => {
  const auth = useAuthStore()
  if (auth.isLoggedIn) {
    userInfo.value = { ...auth.userInfo }
    editForm.value = {
      id: auth.userId,
      username: auth.userInfo.username || '',
      realName: auth.userInfo.realName || '',
      phone: auth.userInfo.phone || '',
      email: auth.userInfo.email || ''
    }
  } else {
    ElMessage.warning('请先登录！')
    router.push('/login')
  }
})


// 处理下拉菜单点击（不变）
const handleDropdownCmd = (command) => {
  if (command === 'setting') dialogVisible.value = true
  else if (command === 'other') ElMessage.info('其他功能待开发~')
}


// 提交修改：把所有字段（包括新填的phone/email）传给后端
// const handleUpdateInfo = async () => {
//   try {
//     await infoFormRef.value.validate()

//     // 【调用后端“修改个人信息”接口】
//     // 示例：假设接口是 updateUserInfoApi，传editForm所有字段
//     // await updateUserInfoApi(editForm.value)

//     // 模拟接口请求（实际项目替换为真实接口）
//     await new Promise(resolve => setTimeout(resolve, 500))

//     // 修改成功：更新localStorage的userInfo
//     localStorage.setItem('userInfo', JSON.stringify(editForm.value))
//     // 更新页面显示的用户信息
//     userInfo.value = { ...editForm.value }

//     dialogVisible.value = false
//     ElMessage.success('个人信息修改成功！')
//   } catch (error) {
//     ElMessage.error('请完善表单信息后提交~')
//   }
// }
// ✅ 核心修改：对接真实后端修改接口 - 完美适配你的PUT接口
const handleUpdateInfo = async () => {
  try {
    // 1. 前端表单校验
    await infoFormRef.value.validate()

    // 2. 调用后端真实的修改个人信息接口 ✅ 核心核心！！
    const res = await updateUserInfo(editForm.value)

    // 3. 修改成功 - 同步数据+提示+关闭弹窗
    ElMessage.success(res || '个人信息修改成功！')
    userInfo.value.realName = editForm.value.realName
    userInfo.value.phone = editForm.value.phone
    userInfo.value.email = editForm.value.email
    const auth = useAuthStore()
    auth.updateProfile({
      realName: editForm.value.realName,
      phone: editForm.value.phone,
      email: editForm.value.email
    })
    dialogVisible.value = false
  } catch (error) {
    // 表单验证失败提示，接口错误由你的axios拦截器自动提示，无需重复写
    if (error.name === 'ValidationError') {
      ElMessage.error('请完善表单信息后提交~')
    }
  }
}

const handleLogout = () => {
  const auth = useAuthStore()
  auth.logout()
  router.push('/login')
  ElMessage.info('已退出登录~')
}

// ========== 新增：手机号失焦校验 ==========
// 修复后的手机号失焦校验方法
const handleCheckPhone = async () => {
  // 1. 拿ref对象的value里的phone（必须加.value！）
  const phone = editForm.value.phone?.trim()
  // 手机号为空，不校验
  if (!phone) return

  try {
    // 2. 调用校验接口（传trim后的手机号）
    await checkPhoneUnique(phone)
  } catch (error) {
    // 3. 校验失败，弹出提示
    ElMessage.warning(error.message || "该手机号已被注册")
  }
}

// ========== 新增：邮箱失焦校验 ==========
// 修复后的邮箱失焦校验方法
const handleCheckEmail = async () => {
  // 必须加.value！
  const email = editForm.value.email?.trim()
  if (!email) return

  try {
    await checkEmailUnique(email)
  } catch (error) {
    ElMessage.warning(error.message || "该邮箱已被注册")
  }
}
</script>


<style scoped>
/* 导航栏整体样式 */
.user-navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
  /* 风格渐变背景 */
  background: linear-gradient(90deg, #8bb5e4 0%, #5289c8 20%, #1e88e5 80%, #1565c0 100%);
  color: #fff;
    position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 20;
}

/* 左侧Logo */
.navbar-left .logo {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  text-decoration: none;
}

/* 右侧用户区域 */
.navbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 用户名+下拉触发区 */
.user-info-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
}
.dropdown-icon {
  font-size: 14px;
  color: #999;
}

/* 退出按钮 */
.logout-btn {
  border-color: #333;
  color: #333;
  border-radius: 2px;
  padding: 5px 15px;
  font-size: 14px;
}
.logout-btn:hover {
  /* 1. 背景色：白色 + 10%透明度（rgba最后一位是透明度） */
  background-color: rgba(255, 255, 255, 0.1);
  /* 2. 边框颜色：强制保持白色 */
  border-color: #fff;
   /* 3. 文字颜色：强制保持白色 */
  color: #fff;
}

/* 个人信息表单 */
.info-form {
  margin-top: 10px;
}
</style>