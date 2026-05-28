<template>
  <div class="recruit-container">
    <!-- 内容区顶部 -->
    <div class="main-header">
      <h2>岗位报名审核</h2>
    </div>

    <!-- 仅保留“岗位报名审核”Tab（因为后端暂未实现其他接口） -->
    <div class="apply-content" style="padding: 20px; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); background-color: #fff;">
      <!-- 岗位报名者列表 -->
      <div class="apply-list">
        <el-table 
          :data="applyList" 
          border 
          style="width: 100%;"
          v-loading="loading"
          element-loading-text="加载申请记录中..."
        >
          <!-- 只显示数据库存在的字段 -->
          <el-table-column prop="seekerName" label="求职者姓名" width="120" />
          <el-table-column prop="seekerPhone" label="求职者手机号" width="150" />
          <el-table-column prop="jobName" label="岗位名称" min-width="180" />
          <el-table-column 
            prop="applyTime" 
            label="报名时间" 
            width="200"
            :formatter="formatApplyTime" 
          />
          <el-table-column 
            prop="applyStatus" 
            label="申请状态" 
            width="120"
            :formatter="formatApplyStatus" 
          />
          <el-table-column label="审核操作" width="180">
            <template #default="scope">
              <!-- 仅未审核的申请显示操作按钮 -->
              <template v-if="Number(scope.row.applyStatus) === 0">
                <el-button 
                  type="primary" 
                  text 
                  @click="handlePass(scope.row)"
                  :loading="operateLoading[scope.row.id]"
                >通过</el-button>
                <el-button 
                  type="danger" 
                  text 
                  @click="handleReject(scope.row)"
                  :loading="operateLoading[scope.row.id]"
                >拒绝</el-button>
              </template>
              <template v-else>
                <span>已处理</span>
              </template>
            </template>
          </el-table-column>
        </el-table>
        <!-- 新增：空数据提示 -->
        <div v-if="!loading && applyList.length === 0" style="text-align: center; padding: 50px 0; color: #999;">
          暂无岗位申请记录
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage , ElMessageBox} from 'element-plus'
// 导入简化后的API
import { getEmployerApplications, auditApplication } from '@/api/modules/employer/recruit.js'

// ========== 基础变量 ==========
// 申请记录列表
const applyList = ref([])
// 加载状态
const loading = ref(false)
// 操作按钮加载状态（按申请ID）
const operateLoading = ref({})

// ========== 工具函数：格式化申请时间（适配后端LocalDateTime格式） ==========
const formatApplyTime = (row) => {
  if (!row.applyTime) return '未知时间'
  // 把后端的 2026-01-07T10:00:00 转为 2026-01-07 10:00:00
  return row.applyTime.replace('T', ' ')
}

// ========== 工具函数：格式化申请状态 ==========
const formatApplyStatus = (row) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝',
    3: '已入职'
  }
  // 兼容后端返回的数字/字符串类型
  return statusMap[Number(row.applyStatus)] || '未知状态'
}

// ========== 页面挂载：加载申请记录 ==========
onMounted(async () => {
  await fetchApplyList()
})

// ========== API调用：加载雇主的申请记录 ==========
const fetchApplyList = async () => {
  loading.value = true
  try {
    const res = await getEmployerApplications()
    console.log('后端原始响应：', res) // 打印完整响应，方便排查
    console.log('申请记录数组：', res) // 打印实际的申请记录数组
    
    // 直接使用res.data（后端返回的申请记录数组），无需额外嵌套
    applyList.value = res || []
    console.log('前端最终渲染数据：', applyList.value)

    // 空数据提示
    if (applyList.value.length === 0) {
      ElMessage.info('当前暂无岗位申请记录')
    }
  } catch (err) {
    console.error('加载申请记录失败：', err) // 打印完整错误信息
    ElMessage.error('加载申请记录失败：' + (err.response?.data?.message || err.message || '网络异常'))
    applyList.value = []
  } finally {
    loading.value = false
  }
}

// ========== 审核操作：通过申请 ==========
const handlePass = async (row) => {
  // 校验行数据
  if (!row || !row.id) {
    ElMessage.warning('请选择有效的申请记录')
    return
  }

  operateLoading.value[row.id] = true
  try {
    const res = await auditApplication({
      id: row.id,
      applyStatus: 1 // 1=通过
    })
    ElMessage.success(res.message || '审核通过成功')
    // 实时更新列表状态（兼容类型）
    row.applyStatus = 1
  } catch (err) {
    ElMessage.error('审核失败：' + (err.response?.data?.message || err.message))
  } finally {
    operateLoading.value[row.id] = false
  }
}

// ========== 审核操作：拒绝申请（新增弹窗输入拒绝原因） ==========
const handleReject = async (row) => {
  // 校验行数据
  if (!row || !row.id) {
    ElMessage.warning('请选择有效的申请记录')
    return
  }

  try {
    // 弹窗输入拒绝原因（必填）
    const { value: rejectReason } = await ElMessageBox.prompt(
      '请输入拒绝原因',
      '拒绝申请',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        inputPlaceholder: '例如：不符合岗位要求、人数已满...',
        // 校验拒绝原因不能为空
        inputValidator: (value) => {
          if (!value.trim()) {
            return '拒绝原因不能为空！'
          }
        }
      }
    )

    operateLoading.value[row.id] = true
    // 调用审核接口
    const res = await auditApplication({
      id: row.id,
      applyStatus: 2, // 2=拒绝
      rejectReason: rejectReason.trim() // 拒绝原因（去空格）
    })
    ElMessage.success(res.message || '已拒绝该申请')
    // 实时更新列表状态
    row.applyStatus = 2
  } catch (err) {
    // 取消弹窗不报错
    if (err !== 'cancel') {
      ElMessage.error('拒绝失败：' + (err.response?.data?.message || err.message))
    }
  } finally {
    operateLoading.value[row.id] = false
  }
}
</script>

<style scoped>
.recruit-container { width: 100%; }
.main-header { margin-bottom: 20px; }
.main-header h2 { font-size: 20px; font-weight: 600; color: #333; margin: 0; }
.main-tabs { background-color: #f5f7fa; padding: 10px; border-radius: 8px; }
.candidate-detail { padding: 10px 0; }
/* 优化空数据提示样式 */
.apply-list :deep(.el-table) { --el-table-text-color: #333; }
</style>