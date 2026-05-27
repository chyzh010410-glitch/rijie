<template>
  <!-- 工作台页面容器 -->
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <h2 class="dashboard-title">工作台仪表盘</h2>

    <!-- 统计卡片区域：4个数据卡片 -->
    <el-row :gutter="20" class="stats-card-row">
      <!-- 今日岗位数卡片 -->
      <el-col :span="6">
        <el-card class="stats-card stats-card-active" @click="goToPage('/joblist')">
          <div class="stats-card-content">
            <el-icon class="stats-icon">
              <Document />
            </el-icon>
            <div class="stats-info">
              <div class="stats-label">今日岗位数</div>
              <div class="stats-value">{{ stats.todayJobCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 待审核岗位卡片 -->
      <el-col :span="6">
        <el-card class="stats-card stats-card-active" @click="goToPage('/joblist')">
          <div class="stats-card-content">
            <el-icon class="stats-icon">
              <Clock />
            </el-icon>
            <div class="stats-info">
              <div class="stats-label">待审核岗位</div>
              <div class="stats-value">{{ stats.pendingAuditJobCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 今日考勤人数卡片 -->
      <el-col :span="6">
        <el-card class="stats-card stats-card-active" @click="goToPage('/joblist')">
          <div class="stats-card-content">
            <el-icon class="stats-icon">
              <UserFilled />
            </el-icon>
            <div class="stats-info">
              <div class="stats-label">今日考勤人数</div>
              <div class="stats-value">{{ stats.todayAttendanceCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 待发放薪资卡片 -->
      <el-col :span="6">
        <el-card class="stats-card stats-card-active" @click="goToPage('/joblist')">
          <div class="stats-card-content">
            <el-icon class="stats-icon">
              <Money />
            </el-icon>
            <div class="stats-info">
              <div class="stats-label">待发放薪资</div>
              <div class="stats-value">{{ stats.pendingSalaryCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据模块区域：左侧趋势图 + 右侧待处理事项 -->
    <el-row :gutter="20" class="data-module-row">
      <!-- 近期岗位发布趋势模块 -->
      <el-col :span="12">
        <el-card class="data-module-card">
          <div class="module-header">
            <span class="module-title">近期岗位发布趋势</span>
            <el-icon class="module-collapse-icon"></el-icon>
          </div>
          <!-- 图表容器（后续可接入ECharts） -->
          <div class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 待处理事项模块 -->
      <el-col :span="12">
        <el-card class="data-module-card">
          <div class="module-header">
            <span class="module-title">待处理事项</span>
            <el-icon class="module-collapse-icon"></el-icon>
          </div>
          <!-- 待处理列表头部 -->
          <div class="todo-header">
            <span>列表</span>
            <a class="todo-link">请态列表 ></a>
          </div>
          <!-- 待处理列表容器 -->
          <div class="todo-list-container">
            <!-- 列表项占位 -->
            <div class="todo-item"></div>
            <div class="todo-item"></div>
            <div class="todo-item"></div>
            <div class="todo-item"></div>
            <div class="todo-item"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ElRow, ElCol, ElCard, ElIcon } from 'element-plus'
import { Document, Clock, UserFilled, Money } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
// 引入仪表盘接口
import { getDashboardStats } from '@/api/modules/admin/dashboard.js'

const router = useRouter()

// 实时统计数据（从数据库读取）
const stats = ref({
  todayJobCount: 0,
  pendingAuditJobCount: 0,
  todayAttendanceCount: 0,
  pendingSalaryCount: 0
})

// 获取实时数据
const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    stats.value = res
  } catch (err) {
    ElMessage.error('获取统计数据失败')
    console.error(err)
  }
}

// 页面一进来就加载
onMounted(() => {
  loadStats()
})

const goToPage = (path) => {
  router.push(path)
}
</script>

<style scoped>
/* 页面基础布局 */
.dashboard-container {
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
}

.dashboard-title {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: 600;
}

/* 统计卡片行 */
.stats-card-row {
  margin-bottom: 60px;
}

.stats-card {
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.stats-card-active:hover {
  background-color: #8bb5e4;
}

.stats-card-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stats-icon {
  font-size: 24px;
  color: #409EFF;
}

.stats-card-active .stats-icon {
  color: #fff;
}

.stats-card .stats-icon {
  color: #409EFF;
}

.stats-label {
  font-size: 25px;
  color: #666;
}

.stats-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.data-module-row {
  margin-top: 20px;
}

.data-module-card {
  padding: 15px;
}

.module-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}

.chart-container {
  width: 100%;
  height: 400px;
  background-color: #f5f7fa;
}

.todo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
}

.todo-link {
  color: #409EFF;
  text-decoration: none;
}

.todo-list-container {
  width: 100%;
  height: 370px;
  background-color: #f5f7fa;
}

.todo-item {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}
</style>