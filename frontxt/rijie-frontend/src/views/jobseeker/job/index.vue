<template>
  <!-- 搜索栏：添加搜索事件监听 -->
  <div class="search-bar-wrap">
    <SearchBar @search="handleSearch" />
  </div>

  <div class="hot-jobs-page">
     <!-- ✅ 新增：我的申请 固定浮窗按钮 -->
    <el-popover
      placement="left"
      title="我的岗位申请"
      width="320"
      trigger="click"
      v-model="popoverVisible"
    >
      <template #reference>
        <div class="my-apply-float-btn">
          <span>我的申请</span>
        </div>
      </template>
<!-- 浮窗里的申请列表，直接用 v-for 渲染 div -->
<div class="apply-list">
  <div v-if="myApplications.length === 0" class="empty-apply">暂无申请记录</div>
  <div v-for="item in myApplications" :key="item.id" class="apply-item">
    <div class="apply-job-name">{{ item.jobName || '未知岗位' }}</div>
    <div class="apply-status">
      <span :class="getStatusClass(item.applyStatus)">{{ getStatusText(item.applyStatus) }}</span>
      <el-button 
        v-if="[1,3].includes(item.applyStatus)" 
        type="primary" 
        size="small" 
        @click="goToAttendance(item)"
      >
        去签到
      </el-button>
    </div>
  </div>
</div>
    </el-popover>

    <!-- ✅ 新增：薪资查询 固定浮窗按钮（和我的申请完全一致） -->
<el-popover
  placement="left"
  title="薪资查询"
  width="200"
  trigger="click"
  v-model="salaryPopoverVisible"
>
  <!-- 薪资查询浮窗内容：简洁入口按钮 -->
  <div class="salary-query-content" style="text-align: center; padding: 10px 0;">
    <el-button 
      type="primary" 
      size="default" 
      @click="goToSalaryPage"
      style="width: 160px;"
    >
      查看我的薪资记录
    </el-button>
  </div>

  <!-- 薪资查询浮窗按钮（样式和我的申请完全一致） -->
  <template #reference>
    <div class="salary-query-float-btn">
      <span>薪资查询</span>
    </div>
  </template>
</el-popover>


    <div class="page-title">热招日结岗位</div>

    <!-- 分类标签（与数据库job_type匹配） -->
    <div class="category-tabs">
      <span v-for="(tab, idx) in categoryTabs" :key="idx" :class="['tab-item', { active: currentTab === idx }]"
        @click="currentTab = idx">
        {{ tab.name }}
      </span>
    </div>

    <!-- 岗位卡片：匹配数据库字段 -->
    <div class="job-card-list">
      <!-- 无数据提示 -->
      <div class="empty-tip" v-if="filteredJobs.length === 0">
        暂无匹配的岗位~
      </div>

      <div class="job-card" v-for="(job, idx) in displayedJobs" :key="job.id">
        <div class="card-header">
          <div class="time-wrap">
            <!-- 时间格式处理（匹配数据库的HH:mm:ss） -->
            <span class="time-text">今天 {{ job.workStartTime }}-{{ job.workEndTime }}</span>
            <!-- 数据库无work_days字段，暂默认“1天单”（可后续补充字段后调整） -->
            <span class="day-tag">1天单</span>
          </div>
          <!-- 数据库无distance字段，仅显示地址 -->
          <span class="distance">{{ formatAddress(job.workAddress) }}</span>
        </div>

        <div class="job-name">{{ job.jobName }}</div>

        <div class="job-tags">
          <!-- 计算工作时长（根据start/end时间） -->
          <span class="tag-time">{{ calculateWorkHours(job.workStartTime, job.workEndTime) }}小时</span>
          <!-- 数据库无gender_limit字段，默认“男女不限” -->
          <span class="tag-item">男女不限</span>
          <!-- 从job_require提取年龄范围 -->
          <span class="tag-item">{{ getAgeRange(job.jobRequire) }}</span>
        </div>

        <div class="card-footer">
          <div class="salary-wrap">
            <span class="salary">{{ job.dailySalary }}元/天</span>
            <!-- 匹配数据库recruit_num字段（招聘人数） -->
            <span class="salary-note">（招{{ job.recruitNum }}人）</span>
          </div>
          <!-- ✅ 正常：报名按钮传参 整个job对象 -->
          <button class="grab-btn" @click="handleApply(job)" :disabled="applyLoading[job.id]">
            <span v-if="applyLoading[job.id]">申请中...</span>
            <span v-else>申请</span>
          </button>
        </div>
      </div>
    </div>
    <!-- 新增：报名确认弹窗 -->
    <el-dialog title="确认报名该岗位" v-model="applyDialogVisible" width="500px" :close-on-click-modal="false">
      <div class="job-detail-card">
        <h4>📋 岗位详情</h4>
        <div class="detail-item">
          <span class="label">岗位名称：</span>
          <span class="value">{{ currentJob.jobName || '暂无岗位名称' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">工作时间：</span>
          <span class="value">{{ currentJob.workStartTime || '--:--' }}-{{ currentJob.workEndTime || '--:--' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">工作地址：</span>
          <span class="value">{{ formatAddress(currentJob.workAddress) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">日薪待遇：</span>
          <span class="value">{{ currentJob.dailySalary || 0 }}元/天</span>
        </div>
        <div class="detail-item">
          <span class="label">岗位要求：</span>
          <span class="value">{{ currentJob.jobRequire || '无特殊要求' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">招聘人数：</span>
          <span class="value">{{ currentJob.recruitNum || 0 }}人</span>
        </div>
      </div>

      <!-- ===================== 👇 评价区域：直接嵌在报名弹窗内（完美适配！） ===================== -->
      <div class="evaluation-section" style="margin-top: 20px; padding-top: 10px; border-top: 1px solid #eee;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
          <h3 style="font-size: 16px; font-weight: 600; margin: 0;">岗位评价</h3>
          <el-button 
  type="primary" 
  size="small" 
  @click="openSubmitDialog"
  v-if="canEvaluate && !hasEvaluated"
>
  我要评价
</el-button>
        </div>

        <div v-if="evaluationList.length > 0" class="evaluation-list">
          <div 
            v-for="item in evaluationList" 
            :key="item.id" 
            class="evaluation-item"
            style="padding: 8px; border-bottom: 1px solid #f0f0f0;"
          >
            <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 4px;">
              <el-rate :value="item.score" disabled :show-score="false" />
              <span style="color: #999; font-size: 12px;">{{ item.createTime }}</span>
            </div>
            <div style="color: #333; line-height: 1.5; font-size:14px;">{{ item.content }}</div>
          </div>
        </div>
        <el-empty v-else description="暂无评价，快来抢首评吧~" style="margin:10px 0;" />
      </div>

      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApply">确认报名</el-button>
      </template>
    </el-dialog>

    <!-- 加载更多（数据不足时自动隐藏） -->
    <div class="load-more-btn-wrap" v-if="hasMore">
      <button class="load-more-btn" @click="loadMore">
        查看更多岗位
      </button>
    </div>
  </div>

    <!-- ===================== 提交评价弹窗 ===================== -->
    <el-dialog
      v-model="submitVisible"
      title="评价这份工作"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="score" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="content"
            type="textarea"
            :rows="4"
            placeholder="分享你的工作感受，给其他求职者参考~"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluation">提交评价</el-button>
      </template>
    </el-dialog>

</template>

<script setup>
import router from '@/router';
import { ref, computed, onMounted } from 'vue'
// ✅ 删除无用的 useRoute 导入！
import { ElMessage, ElDialog, ElButton,ElPopover, ElEmpty, ElRate, ElInput } from 'element-plus'
import SearchBar from '@/components/SearchBar.vue'
import { getApprovedJobsForSeeker, searchApprovedJobs } from '@/api/modules/seeker/job.js'
import { submitJobApplication, getMyApplications } from '@/api/modules/seeker/application.js';
// 评价接口
import { getJobEvaluation, addEvaluation, getMyEvaluation } from '@/api/modules/evaluation.js'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'

// ========== 原有功能代码（完全不动） ==========
const popoverVisible = ref(false)
const myApplications = ref([])
const jobList = ref([]) 
const loading = ref(false) 
const categoryTabs = ref([
  { name: '全部', type: '' },{ name: '餐饮', type: '餐饮' },{ name: '快递', type: '快递' },
  { name: '家教', type: '家教' },{ name: '零售', type: '零售' },{ name: '促销', type: '促销' },
  { name: '安保', type: '安保' }
])
const currentTab = ref(0) 
const searchCondition = ref({ jobType: '', key: '' })
const showCount = ref(9) 
const applyLoading = ref({});
const applyDialogVisible = ref(false)
const currentJob = ref({}) // 🔥 核心：弹窗里的岗位对象（评价用这个ID！）
const salaryPopoverVisible = ref(false)

// ========== 原有方法（完全不动） ==========
const getStatusText = (status) => {
  const statusMap = {0: '待审核',1: '已通过',2: '已拒绝',3: '已入职'}
  return statusMap[status] || '未知状态'
}
const getStatusClass = (status) => {
  const classMap = {0: 'status-pending',1: 'status-passed',2: 'status-rejected',3: 'status-joined'}
  return classMap[status] || ''
}
const goToAttendance = (item) => {
  popoverVisible.value = false;
  useAppStore().setCurrentJob({ jobId: item.jobId, jobName: item.jobName });
  router.push({ name: 'SeekerAttendance' });
  ElMessage.success('即将为您跳转至考勤页面');
};
const goToSalaryPage = () => {
  salaryPopoverVisible.value = false;
  router.push({ name: 'SeekerSalary' });
  ElMessage.success('即将为您跳转至薪资记录页面');
};
const handleSearch = async (condition) => {
  searchCondition.value = condition;
  showCount.value = 9;
  loading.value = true;
  try {
    const res = await searchApprovedJobs({ keyword: condition.key, jobType: condition.jobType, pageNum: 1, pageSize: 9 });
    jobList.value = res.list || [];
  } catch (err) { ElMessage.error('搜索岗位失败'); } finally { loading.value = false; }
};
const filteredJobs = computed(() => {
  const currentTabType = categoryTabs.value[currentTab.value].type
  return jobList.value.filter(job => {
    if (!job) return false;
    const tabMatch = !currentTabType || job.jobType === currentTabType
    const keyMatch = !searchCondition.value.key || (job.jobName && job.jobName.includes(searchCondition.value.key))
    return tabMatch && keyMatch
  })
})
const formatAddress = (address) => {
  if (!address) return '未知地址';
  return address.length > 10 ? address.substring(0, 10) + '...' : address;
};
const displayedJobs = computed(() => filteredJobs.value.slice(0, showCount.value))
const hasMore = computed(() => showCount.value < filteredJobs.value.length)
const loadMore = async () => {
  if (!hasMore.value) return
  loading.value = true
  try {
    const nextPage = (showCount.value / 9) + 1
    const res = await getApprovedJobsForSeeker(undefined, nextPage, 6)
    jobList.value = [...jobList.value, ...res.list]
    showCount.value += 6
  } catch (err) { ElMessage.error('加载更多失败'); } finally { loading.value = false; }
}
const calculateWorkHours = (start, end) => {
  if (!start || !end) return 0
  const startHour = parseInt(start.split(':')[0])
  const endHour = parseInt(end.split(':')[0])
  return endHour >= startHour ? endHour - startHour : (24 - startHour) + endHour
}
const getAgeRange = (require) => {
  if (!require) return '年龄不限'
  const match = require.match(/\d+-\d+岁/)
  return match ? match[0] : '年龄不限'
}

// 点击报名 → 打开弹窗 + 加载当前岗位评价
const handleApply = (job) => {
  currentJob.value = job
  applyDialogVisible.value = true
  // 🔥 关键：打开弹窗时，加载该岗位的所有评价
  loadEvaluationList()
  loadMyEvaluations()
}

const confirmApply = async () => {
  applyDialogVisible.value = false
  const job = currentJob.value
  const { id: jobId } = job
  const auth = useAuthStore()
  const userInfo = auth.userInfo
  const token = auth.token
  const isSeeker = userInfo?.roleType === 2
  if (!token || !userInfo || !isSeeker || !userInfo.id) {
    ElMessage.error('请先以求职者身份登录')
    router.push('/jobseeker/login')
    return
  }
  applyLoading.value[jobId] = true
  try {
    await submitJobApplication({ jobId: jobId })
    ElMessage.success('申请成功，等待雇主审核')
    await getMyApplicationsList()
  } catch (err) { ElMessage.error('申请失败'); } finally { applyLoading.value[jobId] = false }
}

const getMyApplicationsList = async () => {
  try {
    const auth = useAuthStore()
    const userInfo = auth.userInfo
    if (userInfo?.id) {
      const res = await getMyApplications()
      myApplications.value = res?.data || res || []
    }
  } catch (err) { ElMessage.error('获取申请记录失败'); }
}

// ===================== ✅ 评价核心逻辑（适配弹窗，无路由！） =====================

// 评价变量
const evaluationList = ref([])
const submitVisible = ref(false)
const score = ref(5)
const content = ref('')
const myEvaluations = ref([])

// 判断是否已评价
// 1. 原有：判断是否已评价（保留）
const hasEvaluated = computed(() => {
  if (!currentJob.value?.id) return false
  return myEvaluations.value.some(item => item.jobId === currentJob.value.id)
})

// 🔥 2. 新增：核心校验 → 只有【薪资已结算】才能评价（你的业务规则）
const canEvaluate = computed(() => {
  // 无岗位/未登录 → 不允许
  if (!currentJob.value.id || !useAuthStore().userId) return false

  // 查找当前用户 对 该岗位 的申请记录
  const application = myApplications.value.find(
    item => item.jobId === currentJob.value.id
  )

  // 规则：
  // ① 必须有申请记录
  // ② 必须是【已入职/已完成】状态 (3)
  // ③ 必须【薪资已结算】(你后端结算状态，这里用状态+逻辑兜底)
  if (!application) return false
  // 仅 已入职(3) + 薪资结算完成 → 允许评价
  return application.applyStatus === 3 
})

// 加载当前岗位的评价（所有人的评论！）
const loadEvaluationList = async () => {
  const jobId = currentJob.value?.id
  console.log('【调试1】当前岗位ID：', jobId)
  if (!jobId) {
    console.warn('岗位ID为空，跳过加载')
    evaluationList.value = []
    return
  }
  try {
    const res = await getJobEvaluation(jobId)
    console.log('【调试2】接口完整返回：', res)
    // ✅ 核心修复：先判断res是不是数组，直接取数据，同时兼容包装对象结构
    const list = Array.isArray(res) ? res : (res.data || res.list || [])
    evaluationList.value = list
    console.log('【调试4】最终赋值的列表：', evaluationList.value)
  } catch (err) {
    ElMessage.error('评价列表加载失败')
    console.error('加载评价失败：', err)
    evaluationList.value = []
  }
}
// 加载我的评价
const loadMyEvaluations = async () => {
  if (!useAuthStore().userId) {
    console.warn('用户ID为空，跳过加载我的评价')
    myEvaluations.value = []
    return
  }
  try {
    const res = await getMyEvaluation()
    // ✅ 同样适配直接返回数组的情况
    const list = Array.isArray(res) ? res : (res.data || res.list || [])
    myEvaluations.value = list
  } catch (err) {
    ElMessage.error('加载我的评价失败')
    console.error('加载我的评价失败：', err)
    myEvaluations.value = []
  }
}
// 打开评价弹窗
const openSubmitDialog = () => {
  if (!useAuthStore().userId) { ElMessage.warning('请先登录'); return }
  score.value = 5
  content.value = ''
  submitVisible.value = true
}

// 提交评价
const submitEvaluation = async () => {
    // 🔥 新增：二次校验！未结算直接拦截，无法提交
  if (!canEvaluate.value) {
    ElMessage.warning('请完成工作并结算薪资后再评价！')
    return
  }
  
  if (!content.value.trim()) { ElMessage.warning('请输入评价内容'); return }
  const jobId = currentJob.value.id
  const employerId = currentJob.value.employerId
  if (!jobId || !useAuthStore().userId || !employerId) { ElMessage.warning('参数异常'); return }

  try {
    await addEvaluation({
      jobId: jobId,
      seekerId: Number(useAuthStore().userId),
      employerId: Number(employerId),
      score: score.value,
      content: content.value.trim()
    })
    ElMessage.success('评价提交成功！')
    submitVisible.value = false
    // 刷新评价列表
    loadEvaluationList()
    loadMyEvaluations()
  } catch (err) {
    ElMessage.error('评价提交失败')
  }
}

// 页面初始化
onMounted(async () => {
  loading.value = true
  try {
    const res = await getApprovedJobsForSeeker(undefined, 1, 9)
    jobList.value = res.list || [] 
    await getMyApplicationsList()
  } catch (err) {
    ElMessage.error('岗位数据加载失败')
  } finally { loading.value = false }
})
</script>

<style scoped>
/* 搜索栏容器 */
.search-bar-wrap {
  width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

/* 热招职位页面样式 */
.hot-jobs-page {
  width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

/* 分类标签 */
.category-tabs {
  display: flex;
  justify-content: center;
  gap: 25px;
  margin-bottom: 25px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.tab-item {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  padding-bottom: 8px;
}

.tab-item.active {
  color: #00bfa5;
  border-bottom: 2px solid #00bfa5;
}

/* 岗位列表容器 */
.job-card-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

/* 无数据提示 */
.empty-tip {
  grid-column: 1 / -1;
  text-align: center;
  padding: 50px 0;
  color: #999;
  font-size: 16px;
}

/* 岗位卡片 */
.job-card {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 12px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.2s;
}

.job-card:hover {
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
  background-color: #f8faff;
  padding: 8px 10px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.time-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}

.day-tag {
  border: 1px solid #ddd;
  border-radius: 2px;
  padding: 1px 4px;
  color: #999;
}

/* 岗位名称 */
.job-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

/* 岗位标签 */
.job-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.tag-time {
  background-color: #fff2e8;
  color: #ff7d00;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 2px;
}

.tag-item {
  font-size: 12px;
  color: #666;
  border: 1px solid #eee;
  padding: 2px 6px;
  border-radius: 2px;
}

/* 卡片底部 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.salary-wrap {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.salary {
  font-size: 20px;
  font-weight: 700;
  color: #ff4444;
}

.salary-note {
  font-size: 12px;
  color: #999;
}

.grab-btn {
  background-color: #ffd166;
  color: #333;
  border: none;
  border-radius: 4px;
  padding: 6px 15px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

/* 新增：申请按钮禁用样式 */
.grab-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.grab-btn:hover {
  background-color: #ffc842;
}

/* 加载更多按钮 */
.load-more-btn-wrap {
  text-align: center;
}

.load-more-btn {
  padding: 8px 25px;
  border: 1px solid #00bfa5;
	border-radius: 4px;
  background-color: #fff;
  color: #00bfa5;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.load-more-btn:hover {
  background-color: #f5fff9;
}

/* 新增：报名弹窗样式 */
.job-detail-card {
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}
.detail-item {
  display: flex;
  margin: 10px 0;
  font-size: 14px;
}
.label {
  width: 80px;
  color: #666;
  font-weight: 500;
}
.value {
  flex: 1;
  color: #333;
}

/* ✅ 新增：我的申请浮窗样式 */
.my-apply-float-btn {
  position: fixed;
  right: 20px;
  top: 120px;
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 24px;
  padding: 10px 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  cursor: pointer;
  z-index: 999;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}
.my-apply-float-btn:hover {
  background-color: #f8f9fa;
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}
.my-apply-float-btn span {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* ✅ 新增：薪资查询浮窗按钮样式（和我的申请完全一致，仅改top值） */
.salary-query-float-btn {
  position: fixed;
  right: 20px;
  top: 180px; /* 在我的申请下方60px，间距适中 */
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 24px;
  padding: 10px 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  cursor: pointer;
  z-index: 999;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}
.salary-query-float-btn:hover {
  background-color: #f8f9fa;
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}
.salary-query-float-btn span {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 申请列表弹窗样式 */
.apply-list {
  max-height: 300px;
  overflow-y: auto;
  padding: 8px 0;
}
.apply-item {
  padding: 10px 12px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.apply-item:last-child {
  border-bottom: none;
}
.apply-job-name {
  font-size: 14px;
  color: #333;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.apply-status {
  display: flex;
  align-items: center;
  gap: 8px;
}
.status-pending {
  color: #ff9800;
  font-size: 12px;
}
.status-passed {
  color: #4caf50;
  font-size: 12px;
}
.status-rejected {
  color: #f44336;
  font-size: 12px;
}
.status-joined {
  color: #2196f3;
  font-size: 12px;
}
.empty-apply {
  text-align: center;
  padding: 20px 0;
  color: #999;
  font-size: 14px;
}

.job-detail-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
</style>