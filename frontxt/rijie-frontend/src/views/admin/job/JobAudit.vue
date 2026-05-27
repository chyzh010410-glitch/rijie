<template>
  <div class="job-audit-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>岗位审核管理</h2>
    </div>

    <!-- 筛选区（可选） -->
    <div class="filter-section">
      <el-input
        v-model="searchKey"
        placeholder="搜索岗位名称/雇主ID"
        style="width: 300px; margin-right: 10px"
        @keyup.enter="getUnreviewedJobsList"
      />
      <el-button type="primary" @click="getUnreviewedJobsList">搜索</el-button>
    </div>

    <!-- 未审核岗位列表 -->
    <el-table :data="jobList" border style="width: 100%; margin-top: 20px">
      <el-table-column prop="id" label="岗位ID" width="80" />
      <el-table-column prop="jobName" label="岗位名称" min-width="180" />
      <el-table-column prop="employerId" label="雇主ID" width="100" />
      <el-table-column prop="dailySalary" label="日薪（元）" width="120" />
      <el-table-column prop="workAddress" label="工作地点" min-width="150" />
      <el-table-column prop="jobDesc" label="工作内容" min-width="200" />
      <!-- 系统初次筛选提示 -->
      <el-table-column label="系统筛选提示" width="180">
        <template #default="scope">
          <el-tag :type="getFilterTagType(scope.row)">{{ getFilterTips(scope.row) }}</el-tag>
        </template>
      </el-table-column>
      <!-- 审核操作 -->
      <el-table-column label="审核操作" width="150">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="handleAuditPass(scope.row.id)"
            :disabled="getFilterTips(scope.row) === '包含违禁词'"
          >
            审核通过
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[5, 10, 20]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      style="margin-top: 20px; text-align: right"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
// 导入管理员岗位API
import { getUnreviewedJobs, adminUpdateJobStatus } from '@/api/modules/admin/job.js'

// ========== 基础变量 ==========
const searchKey = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const jobList = ref([])
// 系统筛选规则（可配置）
const FILTER_RULES = reactive({
  minSalary: 50, // 日薪最低阈值
  forbiddenWords: ['违法', '色情', '赌博', '暴力'] // 违禁词列表
})

// ========== 页面挂载时加载未审核岗位 ==========
onMounted(() => {
  getUnreviewedJobsList()
})

// ========== 获取未审核岗位列表 ==========
const getUnreviewedJobsList = async () => {
  try {
    const res = await getUnreviewedJobs({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKey.value.trim()
    })
    // 假设后端返回格式：{ list: [], total: 0 }
    jobList.value = res.list
    total.value = res.total
  } catch (error) {
    ElMessage.error('获取未审核岗位失败')
  }
}

// ========== 系统初次筛选逻辑 ==========
// 获取筛选提示文案
const getFilterTips = (row) => {
  // 1. 检测违禁词
  const hasForbiddenWord = FILTER_RULES.forbiddenWords.some(word => 
    row.jobDesc.includes(word) || row.jobRequire.includes(word)
  )
  if (hasForbiddenWord) return '包含违禁词（禁止通过）'

  // 2. 检测日薪过低
  if (row.dailySalary < FILTER_RULES.minSalary) return '日结金额过低（建议核查）'

  // 3. 筛选通过
  return '初次筛选通过'
}

// 获取筛选提示标签类型
const getFilterTagType = (row) => {
  const tips = getFilterTips(row)
  if (tips.includes('违禁词')) return 'danger'
  if (tips.includes('过低')) return 'warning'
  return 'success'
}

// ========== 审核操作逻辑 ==========
const handleAuditPass = async (id) => {
  try {
    await adminUpdateJobStatus(id, 1) // 1=已审核
    ElMessage.success('审核通过成功')
    // 重新加载列表
    getUnreviewedJobsList()
  } catch (error) {
    ElMessage.error('审核操作失败')
  }
}

// ========== 分页事件 ==========
const handleSizeChange = (val) => {
  pageSize.value = val
  getUnreviewedJobsList()
}
const handleCurrentChange = (val) => {
  currentPage.value = val
  getUnreviewedJobsList()
}
</script>

<style scoped>
.job-audit-container {
  width: 100%;
  padding: 20px;
}
.page-header {
  margin-bottom: 20px;
}
.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}
.filter-section {
  margin-bottom: 10px;
}
</style>