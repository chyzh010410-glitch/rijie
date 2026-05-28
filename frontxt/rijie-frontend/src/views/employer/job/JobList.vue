<template>
  <div class="job-list-container">
    <!-- 搜索筛选 -->
    <div class="list-filter">
      <el-input
        v-model="searchKey"
        placeholder="搜索岗位名称"
        style="width: 300px; margin-right: 10px"
        @keyup.enter="handleSearchJob"
      />
      <el-button type="primary" @click="handleSearchJob">搜索</el-button>
      <el-button @click="handleResetSearch">重置</el-button>
    </div>

    <!-- 岗位表格 -->
    <el-table :data="jobList" border style="width: 100%; margin-top: 20px">
      <el-table-column prop="jobName" label="岗位名称" min-width="150" />
      <el-table-column prop="jobType" label="岗位类型" width="100" />
      <el-table-column prop="dailySalary" label="薪资（元/天）" width="120" />
      <el-table-column prop="workAddress" label="工作地点" min-width="200" />
      <el-table-column prop="workStartTime" label="开始时间" width="120" />
      <el-table-column prop="workEndTime" label="结束时间" width="120" />
      <el-table-column prop="recruitNum" label="招聘人数" width="100" />
      <el-table-column
        prop="publishStatus"
        label="发布状态"
        width="100"
        :formatter="formatStatus"
      />
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <!-- 编辑按钮：触发父组件的编辑事件，传递岗位数据 -->
          <el-button link @click="handleEdit(scope.row)">编辑</el-button>
          <!-- 结束按钮：仅已审核状态可点击 -->
          <el-button
            link
            danger
            @click="handleEndJob(scope.row)"
            :disabled="scope.row.publishStatus !== 1"
          >
            结束岗位
          </el-button>
          <!-- 删除按钮 -->
          <el-button link danger @click="handleDeleteJob(scope.row.id)">删除</el-button>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
// 导入岗位API
import { getEmployerJobs, updateJobStatus, deleteJob } from '@/api/modules/employer/job.js'
import { useAuthStore } from '@/stores/auth'

// ========== 接收父组件参数 ==========
const emit = defineEmits(['onEdit']) // 触发编辑事件，通知父组件回显数据

// ========== 基础变量 ==========
const searchKey = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const jobList = ref([])
// 原始岗位列表（用于搜索重置）
const originJobList = ref([])

// ========== 页面挂载时获取岗位列表 ==========
onMounted(() => {
  getMyJobList()
})

// ========== 对接后端：获取当前雇主的岗位列表 ==========
const getMyJobList = async () => {
  try {
    const auth = useAuthStore()
    const userInfo = auth.userInfo
    // 调用后端接口（支持分页：传currentPage和pageSize）
    const res = await getEmployerJobs({
      employerId: userInfo.id,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    // 假设后端返回格式：{ list: [], total: 0 }（需和后端协商）
    jobList.value = res.list
    originJobList.value = res.list
    total.value = res.total
  } catch (error) {
    // 响应拦截器已处理错误提示
  }
}

// ========== 搜索岗位 ==========
const handleSearchJob = () => {
  const keyword = searchKey.value.trim()
  if (!keyword) {
    jobList.value = originJobList.value
    return
  }
  // 前端筛选（或调用后端搜索接口）
  jobList.value = originJobList.value.filter(job => job.jobName.includes(keyword))
}

// 重置搜索
const handleResetSearch = () => {
  searchKey.value = ''
  jobList.value = originJobList.value
}

// ========== 操作逻辑 ==========
// 格式化发布状态
const formatStatus = (row) => {
  return row.publishStatus === 0 ? '未审核' : row.publishStatus === 1 ? '已审核' : '已结束'
}

// 触发编辑：通知父组件（JobAdd）回显数据
const handleEdit = (row) => {
  emit('onEdit', row) // 把当前岗位数据传给父组件
}

// 结束岗位：修改状态为2
const handleEndJob = async (row) => {
  try {
    await updateJobStatus(row.id, 2)
    // 重新获取列表
    getMyJobList()
    ElMessage.success('岗位已结束！')
  } catch (error) {
    // 响应拦截器已处理错误提示
  }
}

// 删除岗位
const handleDeleteJob = async (id) => {
  try {
    await deleteJob(id)
    // 重新获取列表
    getMyJobList()
    ElMessage.success('岗位删除成功！')
  } catch (error) {
    // 响应拦截器已处理错误提示
  }
}

// ========== 分页事件 ==========
const handleSizeChange = (val) => {
  pageSize.value = val
  getMyJobList() // 切换每页条数后重新请求
}
const handleCurrentChange = (val) => {
  currentPage.value = val
  getMyJobList() // 切换页码后重新请求
}

// ========== 监听分页参数变化（可选） ==========
watch([currentPage, pageSize], () => {
  getMyJobList()
})
</script>

<style scoped>
.job-list-container {
  width: 100%;
}
.list-filter {
  padding: 10px 0;
}
</style>