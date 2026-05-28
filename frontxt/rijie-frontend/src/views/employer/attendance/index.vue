<template>
  <div class="employer-attendance-page">
    <div class="container">
      <el-card shadow="hover" class="attendance-list-card">
        <div slot="header">
          <h3>旗下岗位考勤记录管理</h3>
          <p>可手动修改员工考勤状态</p>
        </div>
        <el-table :data="attendanceList" border stripe>
          <el-table-column prop="id" label="考勤ID" width="80" />
          <el-table-column prop="seekerId" label="求职者ID" width="100" />
          <el-table-column prop="jobId" label="岗位ID" width="100" />
          <el-table-column prop="workDate" label="考勤日期" width="120" />
          <el-table-column prop="signInTime" label="签到时间" width="200" />
          <el-table-column prop="signOutTime" label="签退时间" width="200" />
          <el-table-column prop="attendanceStatus" label="考勤状态" width="120">
            <template #default="scope">
              <span :style="{color: getStatusColor(scope.row.attendanceStatus)}">
                {{ getStatusText(scope.row.attendanceStatus) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-select 
                v-model="scope.row.attendanceStatus" 
                size="small"
                @change="handleUpdateStatus(scope.row)"
                style="width: 120px"
              >
                <el-option label="正常" value="0" />
                <el-option label="迟到" value="1" />
                <el-option label="早退" value="2" />
                <el-option label="旷工" value="3" />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElCard, ElTable, ElTableColumn, ElSelect, ElOption } from 'element-plus'
// 导入考勤接口
import { getEmployerAttendance, updateAttendanceStatus } from '@/api/modules/attendance.js'
// 导入状态映射
import { getStatusText, getStatusColor } from '@/api/modules/constant/attendance.js'

// 考勤列表
const attendanceList = ref([])

// 页面初始化：查询考勤记录
onMounted(() => {
  getAttendanceData()
})

// 查询雇主名下所有考勤记录
const getAttendanceData = async () => {
  try {
    const res = await getEmployerAttendance()
    attendanceList.value = res || []
  } catch (error) {
    ElMessage.error('查询考勤记录失败：' + error.message)
  }
}

// 手动更新考勤状态
const handleUpdateStatus = async (row) => {
  try {
    await updateAttendanceStatus({
      id: row.id,
      attendanceStatus: row.attendanceStatus
    })
    ElMessage.success('考勤状态更新成功！')
    // 刷新列表
    getAttendanceData()
  } catch (error) {
    ElMessage.error(error.message || '更新考勤状态失败')
    // 状态回滚，避免页面显示错误
    getAttendanceData()
  }
}
</script>