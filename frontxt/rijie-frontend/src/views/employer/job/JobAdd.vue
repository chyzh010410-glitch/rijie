<template>
  <div class="job-add-container">
    <!-- 内容区顶部 -->
    <div class="main-header">
      <h2>{{ isEditMode ? '编辑岗位' : '发布新岗位' }}</h2>
    </div>

    <!-- Tab切换（发布/编辑表单 + 岗位列表） -->
    <el-tabs v-model="activeTab" class="main-tabs">
      <!-- 1. 发布/编辑表单Tab -->
      <el-tab-pane :label="isEditMode ? '编辑岗位' : '发布新岗位'" name="form">
        <div class="publish-job-form">
          <el-form ref="publishFormRef" :model="publishForm" :rules="publishRules" label-width="120px" size="default" label-suffix="*" require-asterisk-position="left">
  <el-form-item label="岗位名称" prop="jobName">
    <el-input v-model="publishForm.jobName" placeholder="请输入岗位名称（如：餐饮服务员、外卖骑手）" autofocus />
  </el-form-item>

            <!-- 2. 岗位类型 -->
            <el-form-item label="岗位类型" prop="jobType">
              <el-input v-model="publishForm.jobType" placeholder="请输入岗位类型（如：餐饮、快递）" />
            </el-form-item>

            <!-- 3. 工作地点 -->
            <el-form-item label="工作地点" prop="workAddress">
              <el-cascader v-model="addressArr" :options="cityOptions" placeholder="选择省/市/区"
                @change="handleAddressChange" clearable />
              <el-input v-model="publishForm.workAddress" placeholder="若选择器无匹配项，可手动输入地址" style="margin-top: 8px;" />
            </el-form-item>

            <!-- 4. 工作开始时间 -->
            <el-form-item label="工作开始时间" prop="workStartTime">
              <el-time-picker v-model="publishForm.workStartTime" format="HH:mm:ss" value-format="HH:mm:ss"
                placeholder="选择开始时间" clearable />
            </el-form-item>

            <!-- 5. 工作结束时间 -->
            <el-form-item label="工作结束时间" prop="workEndTime">
              <el-time-picker v-model="publishForm.workEndTime" format="HH:mm:ss" value-format="HH:mm:ss"
                placeholder="选择结束时间" clearable />
            </el-form-item>

<!-- 6. 薪资输入框（独立事件，无联动） -->
  <el-form-item label="薪资（元/天）" prop="dailySalary">
    <el-input
      v-model="publishForm.dailySalary"
      type="number"
      placeholder="请输入日薪，例如：150、180.5"
      min="0.01"
      step="0.01"
      precision="2"
      @input="handleSalaryInput"
      clearable
    />
</el-form-item>

<!-- 7. 招聘人数输入框（独立事件，无联动） -->
<el-form-item label="招聘人数" prop="recruitNum">
  <el-input
    v-model.number="publishForm.recruitNum"
    type="number"
    placeholder="请输入招聘人数（如：5）"
    min="1"
    clearable
    @input="handleRecruitNumInput"  
  />
</el-form-item>

            <!-- 8. 岗位描述 -->
            <el-form-item label="岗位描述" prop="jobDesc">
              <el-input v-model="publishForm.jobDesc" type="textarea" :rows="2" placeholder="请输入岗位描述（如：负责餐厅的点餐和收银）" />
            </el-form-item>

            <!-- 9. 岗位要求 -->
            <el-form-item label="岗位要求" prop="jobRequire">
              <el-input v-model="publishForm.jobRequire" type="textarea" :rows="4"
                placeholder="请输入岗位要求（如：18-35岁，有餐饮经验）" />
            </el-form-item>

            <!-- 提交按钮 -->
            <el-form-item>
              <!-- 强制绑定发布函数，不再判断isEditMode -->
<!-- 发布按钮加防抖loading -->
<el-button type="primary" @click="handlePublishJob" :loading="publishLoading">
  发布岗位
</el-button>
              <el-button @click="resetPublishForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 2. 岗位列表Tab -->
      <el-tab-pane label="我的岗位列表" name="list">
        <JobList @onEdit="handleEditJobEmit" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import JobList from './JobList.vue'
import { publishJob, updateJob } from '@/api/modules/employer/job.js'
import { useAuthStore } from '@/stores/auth'
import { cityOptions } from '@/utils/cityData.js'

// ========== 基础变量 ==========
const activeTab = ref('form')
const isEditMode = ref(false)
const publishFormRef = ref(null)
const auth = useAuthStore()
const userInfo = auth.userInfo
const employerId = ref(userInfo.id || '')

// ========== 表单数据 ==========
const publishForm = reactive({
  id: null,
  employerId: employerId.value,
  jobName: '',
  jobType: '',
  workAddress: '',
  workStartTime: '',
  workEndTime: '',
  dailySalary: null, // 初始值为null，避免类型异常
  recruitNum: null,
  jobDesc: '',
  jobRequire: '',
  publishStatus: 0
})

// ========== 省市区处理 ==========
const addressArr = ref([])
const handleAddressChange = (val) => {
  if (val && val.length) {
    publishForm.workAddress = val.join('/')
  }
}

// ========== 薪资输入处理（仅处理薪资，不涉及招聘人数） ==========
const handleSalaryInput = () => {
  if (publishForm.dailySalary === '' || publishForm.dailySalary === null || isNaN(publishForm.dailySalary)) {
    publishForm.dailySalary = null
  } else {
    // 仅处理薪资的数字格式，不触碰招聘人数
    const num = Number(publishForm.dailySalary)
    publishForm.dailySalary = Number(num.toFixed(2))
  }
}

// ========== 招聘人数输入处理（仅处理招聘人数，不涉及薪资） ==========
const handleRecruitNumInput = () => {
  if (publishForm.recruitNum === '' || publishForm.recruitNum === null || isNaN(publishForm.recruitNum)) {
    publishForm.recruitNum = null
  } else {
    // 仅处理招聘人数的整数格式，不触碰薪资
    publishForm.recruitNum = Math.floor(Number(publishForm.recruitNum))
    // 绝对不要在这里修改 dailySalary 字段！
  }
}

// ========== 表单验证规则 ==========
const publishRules = reactive({
  jobName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  jobType: [{ required: true, message: '请输入岗位类型', trigger: 'blur' }],
  workAddress: [{ required: true, message: '请选择/输入工作地点', trigger: 'blur' }],
  workStartTime: [{ required: true, message: '请选择工作开始时间', trigger: 'change' }],
  workEndTime: [{ required: true, message: '请选择工作结束时间', trigger: 'change' }],
  dailySalary: [
    { required: true, message: '请输入日薪', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        // 手动校验：确保值是数字且大于0
        if (value === null || value === '' || isNaN(value)) {
          callback(new Error('请输入有效的数字'))
        } else if (Number(value) <= 0) {
          callback(new Error('日薪必须大于0'))
        } else {
          callback() // 校验通过
        }
      },
      trigger: ['blur', 'change']
    }
  ],
  recruitNum: [
    { required: true, message: '请输入招聘人数', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value === null || value === '' || isNaN(value)) {
          callback(new Error('请输入有效的数字'))
        } else if (!Number.isInteger(Number(value))) {
          callback(new Error('招聘人数必须为整数'))
        } else if (Number(value) < 1) {
          callback(new Error('招聘人数必须大于0'))
        } else {
          callback()
        }
      },
      trigger: ['blur', 'change']
    }
  ],

  jobDesc: [{ required: true, message: '请输入岗位描述', trigger: 'blur' }],
  jobRequire: [{ required: true, message: '请输入岗位要求', trigger: 'blur' }]
})

// script里新增一个加载状态
const publishLoading = ref(false);
// ========== 发布岗位 ==========
// ========== 发布岗位（适配后端返回+Axios拦截器） ==========
const handlePublishJob = async () => {
  console.log('===== 点击发布按钮，进入发布逻辑 ====='); // 日志1：确认按钮点击触发
  publishLoading.value = true; // 开始请求时加loading
  // 强制发布时，清空编辑态的岗位ID（避免污染新岗位发布）
  publishForm.id = null; 

  if (!employerId.value) {
    console.log('雇主ID为空：', employerId.value); // 日志2：检查employerId
    ElMessage.error('未获取到雇主信息，请重新登录！');
    return;
  }

  try {
    console.log('开始表单验证'); // 日志3：确认进入验证阶段
    // 表单验证失败会直接抛错进入catch
    await publishFormRef.value.validate();
    console.log('表单验证通过'); // 日志4：确认验证通过
    
    // 赋值雇主ID（确保请求参数正确）
    publishForm.employerId = employerId.value;
    console.log('调用发布接口，参数：', publishForm); // 日志5：确认接口参数

    // 【关键】Axios拦截器已处理响应：
    // - 后端code=200时，res是后端的data字段（即"岗位发布成功"）
    // - 后端code!==200时，拦截器会抛错并自动提示错误，进入catch
    const res = await publishJob(publishForm);
    console.log('接口返回的data：', res); // 日志6：此时res是"岗位发布成功"

    // 成功提示（优先用后端返回的data，兜底用固定文案）
    ElMessage.success(res || '岗位发布成功！');
    // 发布成功后切换到列表页+重置表单
    activeTab.value = 'list';
    resetPublishForm();

  } catch (error) {
    console.error('发布失败的错误信息：', error); // 日志7：捕获所有错误
    // 【关键】无需手动提示错误！Axios响应拦截器已处理：
    // - 表单验证失败：error.name是ValidationError，手动提示
    // - 接口错误：拦截器已自动ElMessage.error提示，这里无需重复提示
    if (error.name === 'ValidationError') {
      console.log('表单验证失败：', error);
      ElMessage.warning('请完善表单必填项！');
    }
    // 接口错误无需额外提示（拦截器已处理）
  } finally {
    publishLoading.value = false; // 结束请求时取消loading
  }
};

// ========== 编辑岗位 ==========
const handleEditJobEmit = (row) => {
console.log('===== 点击发布按钮，进入发布逻辑 ====='); 
  // 强制清空编辑态的岗位ID
  publishForm.id = null; 
  if (!row || !row.id) {
    ElMessage.warning('请选择有效的岗位进行编辑！')
    return
  }

  isEditMode.value = true
  activeTab.value = 'form'

  Object.assign(publishForm, {
    id: row.id,
    employerId: employerId.value,
    jobName: row.jobName || '',
    jobType: row.jobType || '',
    workAddress: row.workAddress || '',
    workStartTime: row.workStartTime || '',
    workEndTime: row.workEndTime || '',
    dailySalary: row.dailySalary || null, // 薪资回显容错
    recruitNum: row.recruitNum || null,
    jobDesc: row.jobDesc || '',
    jobRequire: row.jobRequire || '',
    publishStatus: row.publishStatus || 0
  })

  if (row.workAddress && row.workAddress.includes('/')) {
    addressArr.value = row.workAddress.split('/')
  } else {
    addressArr.value = []
  }
}

const handleEditJob = async () => {
  if (!publishForm.id) {
    ElMessage.warning('未获取到岗位ID，无法编辑！')
    return
  }

  try {
    await publishFormRef.value.validate()
    const res = await updateJob(publishForm)
    if (res.code === 200 || res.success) {
      activeTab.value = 'list'
      resetPublishForm()
      isEditMode.value = false
      ElMessage.success('岗位修改成功！')
    } else {
      ElMessage.error('岗位修改失败：' + (res.message || '接口返回异常'))
    }
  } catch (error) {
    console.error('编辑岗位失败：', error)
  }
}

// ========== 重置表单 ==========
const resetPublishForm = () => {
  if (publishFormRef.value) {
    publishFormRef.value.resetFields()
  }
  isEditMode.value = false
  Object.assign(publishForm, {
    id: null,
    employerId: employerId.value,
    jobName: '',
    jobType: '',
    workAddress: '',
    workStartTime: '',
    workEndTime: '',
    dailySalary: null,
    recruitNum: null,
    jobDesc: '',
    jobRequire: '',
    publishStatus: 0
  })
  addressArr.value = []
}

// ========== 页面挂载 ==========
onMounted(() => {
  if (!employerId.value) {
    ElMessage.warning('未检测到雇主登录信息，请先登录！')
  }
})
</script>

<style scoped>
.job-add-container {
  width: 100%;
  padding: 10px;
}

.main-header {
  margin-bottom: 20px;
}

.main-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.main-tabs {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.publish-job-form {
  max-width: 800px;
}

:deep(.el-cascader),
:deep(.el-input) {
  width: 100%;
}
</style>