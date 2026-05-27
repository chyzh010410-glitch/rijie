<template>
  <div class="common-layout">
    <div>
      <el-container>
        <el-header>
          <UserNavbar />
        </el-header>
        <el-main>
          <router-view />
        </el-main>
        <el-footer><Footer /></el-footer>
      </el-container>
    </div>
  </div>
</template>

<script>
// 1. 引入Header组件
import UserNavbar from '@/components/UserNavbar.vue';
import Footer from '@/components/Footer.vue';
// 2. 导出组件，并注册Header
export default {
  components: {
    UserNavbar // 关键：把引入的Header注册到当前组件中
    , Footer
  }
}
</script>