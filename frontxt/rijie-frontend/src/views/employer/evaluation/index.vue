<template>
  <div class="employer-evaluation-container" style="padding: 20px;">
    <h2 style="font-size: 18px; font-weight: 600; margin-bottom: 20px;">收到的评价</h2>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>

    <el-table v-else :data="evaluationList || []" border style="width: 100%;">
      <el-table-column prop="jobId" label="岗位ID" width="100" />
      <el-table-column label="评分" width="120">
        <template #default="scope">
          <el-rate :value="scope.row.score" disabled :show-score="false" />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" min-width="300" />
      <el-table-column prop="createTime" label="评价时间" width="180" />
    </el-table>

    <el-empty v-if="evaluationList.length === 0 && !loading" description="暂无收到的评价" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElSkeleton, ElTable, ElTableColumn, ElRate, ElEmpty } from 'element-plus'
// 确保接口名和evaluation.js导出完全一致
import { getEmployerEvaluation } from '@/api/modules/evaluation.js'
import { useAuthStore } from '@/stores/auth'

const evaluationList = ref([])
const loading = ref(false)

const getEmployerId = () => {
  return Number(useAuthStore().userId) || null
}

const loadEmployerEvaluations = async () => {
  loading.value = true
  try {
    const employerId = getEmployerId()
    console.log('【调试】雇主ID：', employerId)

    if (!employerId) {
      ElMessage.error('获取雇主信息失败，请重新登录')
      return
    }

    const res = await getEmployerEvaluation(employerId)
    console.log('【调试】接口返回：', res)

    const list = Array.isArray(res) ? res : (res.data || res.list || [])
    evaluationList.value = list
  } catch (err) {
    ElMessage.error('加载评价失败')
    console.error('加载失败：', err)
    evaluationList.value = []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadEmployerEvaluations()
})
</script>

<style scoped>
.employer-evaluation-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
</style>