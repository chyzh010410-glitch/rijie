<template>
  <div class="salary-page">
    <div class="page-title">我的薪资记录（日结）</div>

    <div class="salary-card-list">
      <div class="empty-tip" v-if="salaryList.length === 0">
        暂无薪资记录，打卡完成后将自动生成~
      </div>

      <div class="salary-card" v-for="item in salaryList" :key="item.id">
        <div class="card-header">
          <!-- 兼容后端不同字段名：jobName / job_name -->
          <span class="job-name">{{ item.jobName || item.job_name || '未知岗位' }}</span>
          <span class="work-date">{{ formatDate(item.workDate) }}</span>
        </div>

        <div class="salary-content">
          <div class="salary-amount">
            薪资金额：<span class="amount">¥{{ formatMoney(item.salaryAmount) }}</span>
          </div>
          <div class="pay-status" :class="item.payStatus === 1 ? 'status-paid' : 'status-unpaid'">
            {{ item.payStatus === 1 ? '已发放' : '未发放' }}
          </div>
        </div>

        <div class="card-footer" v-if="item.payStatus === 1 && item.payTime">
          <span class="pay-time">发放时间：{{ formatDateTime(item.payTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMySalaries } from '@/api/modules/seeker/salary'

const salaryList = ref([])

onMounted(() => {
  fetchMySalaries()
})

const fetchMySalaries = async () => {
  try {
    const res = await getMySalaries()
    console.log('【薪资接口返回】', res) // 调试用，确认后端字段

    // 2. 修复：兼容后端直接返回数组 / 包装对象两种结构
    const list = Array.isArray(res) ? res : (res.data || res.list || [])
    salaryList.value = list

    if (salaryList.value.length === 0) {
      ElMessage.info('暂无薪资记录，完成签退后将自动生成~')
    }
  } catch (err) {
    console.error('查询薪资记录异常：', err)
    ElMessage.error('查询薪资记录失败：' + (err.response?.data?.message || err.message || '网络异常'))
  }
}

// 3. 修复：金额格式化，加非空判断，避免NaN
const formatMoney = (num) => {
  if (num === null || num === undefined || isNaN(num)) return '0.00'
  return Number(num).toFixed(2)
}

// 4. 修复：本地实现日期格式化，不依赖外部工具
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return ''
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return ''
  return `${formatDate(dateStr)} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
/* 5. 补充：和岗位列表完全一致的样式 */
.salary-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.salary-card-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.salary-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  padding: 16px;
  transition: all 0.3s;
}

.salary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.job-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.work-date {
  font-size: 14px;
  color: #999;
}

.salary-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.salary-amount {
  font-size: 16px;
  color: #333;
}

.amount {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
  margin-left: 4px;
}

.pay-status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
}

.status-paid {
  background: #f0f9ff;
  color: #009e5f;
}

.status-unpaid {
  background: #fef6cc;
  color: #e6a23c;
}

.card-footer {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #999;
}

.empty-tip {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 0;
  font-size: 16px;
  color: #999;
}
</style>