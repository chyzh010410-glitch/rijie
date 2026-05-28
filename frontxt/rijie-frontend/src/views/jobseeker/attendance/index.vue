<template>
  <div class="seeker-attendance-page">
    <div class="container">
      <!-- 1. 岗位信息卡片 -->
      <el-card shadow="hover" class="job-card">
        <div class="job-info">
            <!-- ✅ 新增：返回首页按钮 -->
  <div class="back-btn-wrap">
    <el-button 
      link 
      icon="el-icon-arrow-left" 
      @click="goBackToHome"
      size="small"
    >
      返回首页
    </el-button>
  </div>

          <h3>当前考勤岗位：{{ jobInfo.jobName || '暂无岗位信息' }}</h3>
          <p>上班时间：{{ jobInfo.workStartTime }} | 下班时间：{{ jobInfo.workEndTime }}</p>
        </div>
      </el-card>

      <!-- 2. 签到签退按钮区 -->
      <div class="sign-btn-box">
        <el-button 
          type="primary" 
          size="large" 
          @click="handleSignIn"
          :disabled="isSignedIn"
          v-if="!isSignedIn"
        >
          <el-icon><Clock /></el-icon> 立即签到
        </el-button>
        <el-button 
          type="success" 
          size="large" 
          @click="handleSignOut"
          :disabled="isSignedOut"
          v-else
        >
          <el-icon><Clock /></el-icon> 立即签退
        </el-button>
        <p class="sign-tips">{{ signTips }}</p>
      </div>

      <!-- 3. 今日考勤状态 -->
      <el-card shadow="hover" class="today-status-card" v-if="todayAttendance">
        <div class="status-info">
          <p>今日考勤状态：<span :style="{color: getStatusColor(todayAttendance.attendanceStatus)}">{{ getStatusText(todayAttendance.attendanceStatus) }}</span></p>
          <p>签到时间：{{ todayAttendance.signInTime || '未签到' }}</p>
          <p>签退时间：{{ todayAttendance.signOutTime || '未签退' }}</p>
        </div>
      </el-card>

      <!-- 4. 个人考勤记录列表 -->
      <el-card shadow="hover" class="attendance-list-card">
        <div slot="header">我的考勤记录</div>
        <el-table :data="attendanceList" border stripe>
          <el-table-column prop="workDate" label="考勤日期" width="120" />
          <el-table-column prop="signInTime" label="签到时间" width="200" />
          <el-table-column prop="signOutTime" label="签退时间" width="200" />
          <el-table-column prop="attendanceStatus" label="考勤状态">
            <template #default="scope">
              <span :style="{color: getStatusColor(scope.row.attendanceStatus)}">
                {{ getStatusText(scope.row.attendanceStatus) }}
              </span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElButton, ElCard, ElTable, ElTableColumn } from 'element-plus'
import { Clock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
// 导入考勤接口
import { signIn, signOut, getMyAttendance } from '@/api/modules/attendance.js'
// 导入状态映射
import { getStatusText, getStatusColor } from '@/api/modules/constant/attendance.js'
import { getJobDetailById  } from '@/api/modules/seeker/job.js'
// 在考勤页script setup顶部导入
import { calculateSalary } from '@/api/modules/seeker/salary'
const router = useRouter()
// ========== ✅ 修复错误3：安全获取用户信息+求职者ID ==========
const userInfoStr = localStorage.getItem('userInfo')
const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null
const seekerId = userInfo?.id || null; // 取不到就是null，后续做校验

// ========== ✅ 修复错误4：安全初始化岗位信息 ==========
const jobInfo = reactive({
  jobId: localStorage.getItem('currentJobId') || '',
  jobName: localStorage.getItem('currentJobName') || '',
  workStartTime: localStorage.getItem('workStartTime') || '',
  workEndTime: localStorage.getItem('workEndTime') || ''
})

// 状态变量
const attendanceList = ref([]) // 个人考勤列表
const todayAttendance = ref(null) // 今日考勤信息
const isSignedIn = ref(false) // 是否已签到
const isSignedOut = ref(false) // 是否已签退
const signTips = ref('请在上班时间内完成签到，下班时间内完成签退')

// ========== ✅ 修复错误1+所有逻辑：完整修复 申请状态校验方法 ==========
// const checkApplyStatus = async () => {
//   // 前置校验：用户未登录/无求职者ID
//   if (!seekerId) {
//     ElMessage.warning('请先登录后再进行考勤操作')
//     router.push({ name: 'JobseekerWorkbench' })
//     return false
//   }
//   // 前置校验：无岗位ID
//   if (!jobInfo.jobId) {
//     ElMessage.warning('请先报名岗位后再进行考勤操作')
//     router.push({ name: 'JobseekerWorkbench' })
//     return false
//   }

//   try {
//     const res = await getApplyStatus({
//       seekerId: seekerId, // 当前求职者ID
//       jobId: jobInfo.jobId // 当前岗位ID
//     })
//     // ✅ 核心修复：从res.data里取申请状态！！！
//     const applyStatus = res.data
//     console.log("✅ 申请状态查询结果：", applyStatus); // 控制台打印状态，方便你排查
    
//     // ✅ 你的规则：1=已通过、3=已入职 都允许考勤
//     if ([1, 3].includes(applyStatus)) {
//       return true; // 校验通过，放行
//     } else {
//       ElMessage.warning('请等待雇主审核通过后再进行考勤操作')
//       router.push({ name: 'JobseekerWorkbench' })
//       return false
//     }
//   } catch (error) {
//     console.error("❌ 查询申请状态失败：", error); // 控制台打印错误，方便排查
//     ElMessage.error('查询审核状态失败，请稍后重试')
//     router.push({ name: 'JobseekerWorkbench' })
//     return false
//   }
// }

// 页面初始化：校验权限+查询考勤数据
// ========== 修改：页面初始化逻辑（新增申请状态校验） ==========
onMounted(async () => {
  // 原有校验：未报名岗位
    // 新增：打印jobId，确认是否有效
  console.log("当前岗位ID：", jobInfo.jobId); 
  if (!jobInfo.jobId) {
    
    ElMessage.warning('请先报名岗位，再进行考勤操作！')
    router.push({ name: 'JobseekerWorkbench' })
    return
  }

  // // ✅ 新增校验：申请状态是否通过
  // const isApplyPassed = await checkApplyStatus()
  // if (!isApplyPassed) return


  // ✅ 关键：调用后端现成接口，根据岗位ID获取完整岗位信息
  try {
    const res = await getJobDetailById(jobInfo.jobId)
    // ✅ 把接口返回的 岗位名称、上班时间、下班时间 赋值给jobInfo
    jobInfo.jobName = res.jobName
    jobInfo.workStartTime = res.workStartTime
    jobInfo.workEndTime = res.workEndTime
  } catch (err) {
    ElMessage.error('获取岗位时间信息失败，不影响签到签退')
  }
  // 原有逻辑：查询考勤记录
  getAttendanceData()
})

// 查询个人考勤记录
const getAttendanceData = async () => {
  try {
    const res = await getMyAttendance(seekerId)
    attendanceList.value = res || []
    // 筛选今日考勤信息
    const today = new Date().toLocaleDateString()
    todayAttendance.value = attendanceList.value.find(item => {
      return new Date(item.workDate).toLocaleDateString() === today
    }) || null
    // 更新签到签退状态
    if (todayAttendance.value) {
      isSignedIn.value = !!todayAttendance.value.signInTime
      isSignedOut.value = !!todayAttendance.value.signOutTime
      signTips.value = isSignedOut.value ? '今日考勤已完成' : '已签到，请在下班时间签退'
    }
  } catch (error) {
    ElMessage.error('查询考勤记录失败：' + error.message)
  }
}

// 签到操作
const handleSignIn = async () => {
  try {
    await signIn({
      seekerId: seekerId,
      jobId: jobInfo.jobId,
      workDate: new Date().toISOString().split('T')[0] // 今日日期，格式匹配后端LocalDate
    })
    ElMessage.success('签到成功！')
    isSignedIn.value = true
    signTips.value = '已签到，请在下班时间签退'
    // 刷新考勤数据
    getAttendanceData()
  } catch (error) {
    ElMessage.error(error.message || '签到失败，可能已重复签到')
  }
}

// 签退操作
// 签退操作（修改后）
const handleSignOut = async () => {
  try {
    // 1. 原有签退接口调用（变量完全复用，无需修改）
    await signOut({
      seekerId: seekerId,       // 复用现有求职者ID变量
      jobId: jobInfo.jobId,     // 复用现有岗位ID变量
      workDate: new Date().toISOString().split('T')[0] // 复用现有日期变量
    })
    
    // 2. 签退成功提示（原有逻辑不变）
    ElMessage.success('签退成功！')
    isSignedOut.value = true
    signTips.value = '今日考勤已完成'

    // ✅ 新增：自动调用算薪接口（核心改造，复用所有现有变量）
    try {
      const salaryRes = await calculateSalary({
        seekerId: seekerId,       // 直接复用签退的求职者ID
        jobId: jobInfo.jobId,     // 直接复用签退的岗位ID
        workDate: new Date().toISOString().split('T')[0] // 直接复用签退的工作日期
      })
        // ✅ salaryRes是后端的data（无code），直接判断是否有返回即可
  ElMessage.success('薪资已自动计算，可前往「薪资查询」查看！')
} catch (salaryError) {
  // 算薪失败（比如已算薪）的提示
  ElMessage.info(salaryError.message || '签退成功，薪资计算稍后为您处理~')
}
    //   // 算薪成功提示（友好引导到薪资查询）
    //   if (salaryRes.code === 200) {
    //     ElMessage.success('薪资已自动计算，可前往「薪资查询」查看！')
    //   } else {
    //     // 后端返回“已算薪”时，仅轻提示，不报错
    //     ElMessage.info(salaryRes.msg || '该日期薪资已计算，无需重复操作')
    //   }
    // } catch (salaryError) {
    //   // 算薪失败不影响签退结果，仅控制台打印+轻提示
    //   console.error('自动算薪异常：', salaryError)
    //   ElMessage.info('签退成功，薪资计算稍后为您处理~')
    // }

    // 3. 刷新考勤数据（原有逻辑不变）
    getAttendanceData()
  } catch (error) {
    ElMessage.error(error.message || '签退失败，可能未签到或已签退')
  }
}

// ✅ 新增：返回首页方法
const goBackToHome = () => {
  router.push({ name: 'JobseekerWorkbench' }); // 跳转到你的求职者首页路由
  ElMessage.success('已返回岗位列表页');
};
</script>

<style scoped>
/* ✅ 考勤页完整样式 */
.seeker-attendance-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px 0;
}
.container {
  width: 1200px;
  margin: 0 auto;
}

/* 岗位信息卡片 */
.job-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.job-info {
  padding: 16px;
}
.job-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}
.job-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 签到签退按钮区 */
.sign-btn-box {
  text-align: center;
  margin: 30px 0;
}
.sign-tips {
  margin-top: 12px;
  font-size: 14px;
  color: #999;
}

/* 今日考勤状态卡片 */
.today-status-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.status-info {
  padding: 16px;
}
.status-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #333;
}
.status-info span {
  font-weight: 500;
}

/* 考勤记录列表卡片 */
.attendance-list-card {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.attendance-list-card .el-card__header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #eee;
  font-weight: 500;
  color: #333;
  padding: 12px 16px;
}
.el-table {
  --el-table-header-text-color: #666;
  --el-table-row-hover-bg-color: #f8f9fa;
  --el-table-border-color: #eee;
}

/* 考勤页样式：返回按钮美化 */
.back-btn-wrap {
  position: absolute;
  top: 10px;
  right: 10px;
}
.job-info {
  position: relative; /* 让按钮定位生效 */
  padding: 20px;
}
</style>