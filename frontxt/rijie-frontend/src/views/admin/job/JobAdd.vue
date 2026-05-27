<template>
  <div class="admin-add-job">
    <h2>管理员新增岗位</h2>
    <el-form :model="jobForm" label-width="120px" ref="jobFormRef" :rules="rules">
      <!-- 1. 选择雇主ID（管理员需指定岗位所属雇主） -->
      <el-form-item label="所属雇主ID" prop="employerId">
        <el-select v-model="jobForm.employerId" placeholder="请选择雇主">
          <!-- 实际项目中：下拉框选项需从“雇主列表接口”获取 -->
          <el-option label="雇主A（ID:1）" value="1" />
          <el-option label="雇主B（ID:2）" value="2" />
        </el-select>
      </el-form-item>

      <!-- 2. 岗位名称（必填） -->
      <el-form-item label="岗位名称" prop="jobName">
        <el-input v-model="jobForm.jobName" placeholder="请输入岗位名称" />
      </el-form-item>

      <!-- 3. 日薪（必填） -->
      <el-form-item label="日薪金额" prop="dailySalary">
        <el-input v-model.number="jobForm.dailySalary" placeholder="请输入日薪（元）" />
      </el-form-item>

      <!-- 其他字段：岗位类型、工作地址等，根据Job实体补充 -->
      <el-form-item label="岗位类型">
        <el-input v-model="jobForm.jobType" placeholder="请输入岗位类型（如兼职）" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit">提交新增</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { adminAddJob } from '@/api/modules/admin/job.js'; // 导入封装的API

// 表单数据（与后端Job实体字段对应）
const jobForm = reactive({
  employerId: null, // 管理员选择的雇主ID
  jobName: '',
  dailySalary: null,
  jobType: '',
  workAddress: '',
  // 其他字段：workStartTime、workEndTime等，按需补充
});

// 表单校验规则（匹配后端必填项）
const rules = {
  employerId: [{ required: true, message: '请选择所属雇主', trigger: 'change' }],
  jobName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  dailySalary: [
    { required: true, message: '请输入日薪', trigger: 'blur' },
    { type: 'number', message: '日薪必须是数字', trigger: 'blur' }
  ]
};

// 表单引用
const jobFormRef = ref(null);

// 提交新增
const handleSubmit = async () => {
  // 表单校验
  const valid = await jobFormRef.value.validate();
  if (!valid) return;

  try {
    // 调用雇主的publish接口
    await adminAddJob(jobForm);
    ElMessage.success('岗位新增成功');
    handleReset(); // 重置表单
    // 可选：跳转到岗位列表页
    // router.push('/job/list');
  } catch (err) {
    ElMessage.error('岗位新增失败：' + (err.message || '系统异常'));
  }
};

// 重置表单
const handleReset = () => {
  jobFormRef.value.resetFields();
};
</script>