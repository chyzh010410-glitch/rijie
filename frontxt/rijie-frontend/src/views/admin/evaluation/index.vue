<template>
  <div class="admin-evaluation-container" style="padding: 20px;">
    <h2 style="font-size: 20px; font-weight: 600; margin-bottom: 20px;">评价管理</h2>

    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="6" animated />
    </div>

    <el-table 
      v-else 
      :data="evaluationList || []" 
      border 
      style="width: 100%;"
      :empty-text="'暂无评价数据'"
    >
      <el-table-column prop="id" label="评价ID" width="100" />
      <el-table-column prop="jobId" label="岗位ID" width="100" />
      <el-table-column prop="seekerId" label="求职者ID" width="120" />
      <el-table-column label="评分" width="120">
        <template #default="scope">
          <el-rate :value="scope.row.score" disabled :show-score="false" />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" min-width="350" />
      <el-table-column prop="createTime" label="评价时间" width="180" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDelete(scope.row.id)"
            :loading="deleteLoading[scope.row.id]"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty 
      v-if="evaluationList.length === 0 && !loading" 
      description="暂无评价数据" 
      style="margin-top: 50px;"
    />

    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除评价"
      width="400px"
      :close-on-click-modal="false"
    >
      <p>确定要删除这条评价吗？删除后无法恢复！</p>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElSkeleton, ElTable, ElTableColumn, ElRate, ElEmpty, ElDialog, ElButton } from 'element-plus'
// ✅ 核心修复：导入你实际存在的接口名
import { getAdminEvaluationList, deleteEvaluation } from '@/api/modules/evaluation.js'

const evaluationList = ref([])
const loading = ref(false)
const deleteDialogVisible = ref(false)
const deleteLoading = ref({})
const currentDeleteId = ref(null)

// 加载全部评价（管理员专属）
const loadAllEvaluations = async () => {
  loading.value = true
  try {
    // ✅ 调用你实际的接口方法
    const res = await getAdminEvaluationList()
    console.log('【管理员评价】接口返回：', res)

    // 兼容后端直接返回数组/包装对象
    const list = Array.isArray(res) ? res : (res.data || res.list || [])
    evaluationList.value = Array.isArray(list) ? list : []
    console.log('【管理员评价】最终列表：', evaluationList.value)
  } catch (err) {
    ElMessage.error('加载评价列表失败')
    console.error('加载失败：', err)
    evaluationList.value = []
  } finally {
    loading.value = false
  }
}

// 打开删除弹窗
const handleDelete = (id) => {
  currentDeleteId.value = id
  deleteDialogVisible.value = true
}

// 确认删除
const confirmDelete = async () => {
  if (!currentDeleteId.value) return

  deleteLoading.value[currentDeleteId.value] = true
  try {
    await deleteEvaluation(currentDeleteId.value)
    ElMessage.success('评价删除成功')
    deleteDialogVisible.value = false
    await loadAllEvaluations()
  } catch (err) {
    ElMessage.error('删除失败，请稍后重试')
    console.error('删除失败：', err)
  } finally {
    deleteLoading.value[currentDeleteId.value] = false
    currentDeleteId.value = null
  }
}

onMounted(async () => {
  await loadAllEvaluations()
})
</script>

<style scoped>
.admin-evaluation-container {
  background: #fff;
  min-height: calc(100vh - 84px);
}
</style>
