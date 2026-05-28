<template>
  <!-- Element Plus布局容器：垂直方向的布局 -->
  <el-container style="height: 100vh;">
    <!-- 头部：显示系统名称、用户信息等 -->
    <el-header  class="header" style="background: linear-gradient(90deg, #8bb5e4 0%, #5289c8 20%, #1e88e5 80%, #1565c0 100%); color: white;">
      <div class="header-content">
        <h2>后台管理系统</h2>
        <!-- type="text"改成了link -->
        <el-button link @click="handleLogout" style="color: white;">退出登录</el-button>
      </div>
    </el-header>

    <!-- 主体：侧边栏 + 主内容区 -->
    <el-container>
      <!-- 侧边栏：导航菜单 -->
      <el-aside width="200px" style="background-color: #f5f7fa;">
        <el-scrollbar>
          <el-menu router>

            <el-menu-item index="/admin/index">
              <el-icon>
                <Promotion />
              </el-icon>首页
            </el-menu-item>


            <el-sub-menu index="1">
              <template #title>
                <el-icon>
                  <tools />
                </el-icon>系统管理
              </template>
              <el-menu-item index="/admin/joblist">岗位管理</el-menu-item>
              <el-menu-item index="/admin/jobaudit">岗位审核管理</el-menu-item>
              <el-menu-item index="/admin/user-management">用户管理</el-menu-item>
              <el-menu-item index="/admin/evaluation">评价管理</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="4">
              <template #title>
                <el-icon>
                 <histogram />
                </el-icon>数据统计管理
              </template>
              <el-menu-item index="/admin/stat">数据查验</el-menu-item>
            </el-sub-menu>

          </el-menu>
        </el-scrollbar>
      </el-aside>

      <!-- 主内容区：后续子路由的渲染出口 -->
      <el-main style="background-color: #ffffff;padding:0px">
        <router-view /> <!-- 布局页面的子路由出口（可选，用于后续功能扩展） -->
        <!-- <div class="welcome">欢迎登录后台管理系统！</div> -->
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// 引入Element Plus图标
import { Histogram, House, Promotion, Tools, User } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()

// 退出登录方法
const handleLogout = () => {
  // 清除登录状态
  useAuthStore().logout()
  // 跳转到登录页
  router.push('/login')
  ElMessage.success('退出登录成功')
}
</script>

<style scoped>

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.welcome {
  font-size: 18px;
  color: #666;
  text-align: center;
  margin-top: 100px;
}
</style>