<template>
  <!-- 顶部导航栏容器 -->
  <div class="navbar">
    <!-- 左侧：Logo + 城市切换 -->
    <div class="navbar-left">
      <!-- Logo -->
      <div class="navbar-logo">日结兼职系统</div>
      <!-- 城市切换 -->
      <div class="city-switch">
        <el-icon class="city-icon"><Location /></el-icon>
        <span class="city-name">深圳</span>
        <span class="switch-btn">切换</span>
      </div>
    </div>

    <!-- 中间：导航菜单 -->
    <div class="navbar-middle">
      <el-menu mode="horizontal" background-color="transparent" text-color="#fff" active-text-color="#fff">
        <el-menu-item index="1">首页</el-menu-item>
        <el-menu-item index="2">职位</el-menu-item>
        <el-menu-item index="3">公司</el-menu-item>
        <el-menu-item index="4">校园·海归</el-menu-item>
        <el-menu-item index="5">APP</el-menu-item>
        <el-menu-item index="6">有了</el-menu-item>
        <el-menu-item index="7">海外</el-menu-item>
        <el-menu-item index="8">无障碍专区</el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧：功能按钮 + 登录/注册 -->
    <div class="navbar-right">
      <el-button link class="func-btn" @click="toLogin">我要招聘</el-button>
      <el-button link class="func-btn" @click="toLogin">我要找工作</el-button>
      <el-button type="default" class="login-btn" @click="toLogin">登录/注册</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Location } from '@element-plus/icons-vue' // 引入定位图标

const router = useRouter()

// 跳转登录页
const toLogin = () => {
  router.push({ name: 'Login' })
}
</script>

<style scoped>
/* 导航栏整体样式 */
.navbar {
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

/* 左侧：Logo + 城市切换 */
.navbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}
.navbar-logo {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
}
.city-switch {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}
.city-icon {
  font-size: 16px;
}
.switch-btn {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

/* 中间：导航菜单 */
.navbar-middle {
  flex: 1;
  margin: 0 40px;
}
.el-menu--horizontal {
  border-bottom: none; /* 去掉Element菜单默认下划线 */
}
.el-menu-item {
  font-size: 14px;
  margin: 0 8px;
}
.el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* 右侧：功能按钮 + 登录/注册 */
.navbar-right {
  display: flex;
  align-items: center;
  gap: 15px;
}
.func-btn {
  color: #fff;
  font-size: 14px;
}
.func-btn:hover {
  color: rgba(255, 255, 255, 0.8);
}
.login-btn {
  border-color: #1e88e5;
  color: #1e88e5;
  border-radius: 2px;
  padding: 5px 15px;
  font-size: 14px;
}
.login-btn:hover {
  /* 1. 背景色：白色 + 10%透明度（rgba最后一位是透明度） */
  background-color: rgba(255, 255, 255, 0.1);
  /* 2. 边框颜色：强制保持白色 */
  border-color: #fff;
  /* 3. 文字颜色：强制保持白色 */
  color: #fff;
}
</style>