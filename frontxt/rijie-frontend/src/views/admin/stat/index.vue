<template>
  <div class="stat-management">
    <h2>数据统计管理</h2>
    <!-- 概览卡片 -->
    <el-row :gutter="20">
      <el-col :span="6"><el-card>总用户：{{ overview.totalUser }}</el-card></el-col>
      <el-col :span="6"><el-card>总岗位：{{ overview.totalJob }}</el-card></el-col>
      <el-col :span="6"><el-card>总薪资：{{ overview.totalSalary }}元</el-card></el-col>
      <el-col :span="6"><el-card>今日新增：{{ overview.todayNewUser }}</el-card></el-col>
    </el-row>
    <!-- 图表区域（近7天用户增长折线图） -->
    <el-card style="margin-top:20px;height:400px;">
      <div ref="userGrowthChart" style="width:100%;height:100%"></div>
    </el-card>
    <!-- 其他图表同理 -->
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getAdminOverview, getUserGrowth7d } from '@/api/modules/admin/stat.js'

const router = useRouter()
const overview = ref({})
const userGrowthChart = ref(null)

// 加载概览数据（拦截器已解包data，res即后端data字段）
const loadOverview = async () => {
  const res = await getAdminOverview()
  overview.value = res
}

// 初始化用户增长图表
const initUserGrowthChart = async () => {
  const res = await getUserGrowth7d()
  const chart = echarts.init(userGrowthChart.value)
  chart.setOption({
    xAxis: { type: 'category', data: res.map(i => i.date) },
    yAxis: { type: 'value' },
    series: [{ name: '新增用户', type: 'line', data: res.map(i => i.num), smooth: true }]
  })
}

onMounted(async () => {
  await loadOverview()
  nextTick(() => initUserGrowthChart())
})
</script>