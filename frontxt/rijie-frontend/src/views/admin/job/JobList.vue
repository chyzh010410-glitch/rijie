<template>
    <div class="job">
        <h1>岗位管理</h1>
        <div class="containes">
            <el-button type="primary" @click="handleAdd">+ 新增岗位</el-button>
        </div>
        <!-- 加载/错误提示 -->
        <div v-if="loading" class="loading-tip">加载中...</div>
        <div v-if="errorMsg" class="error-tip">{{ errorMsg }}</div>

        <div class="containes" v-if="!loading && !errorMsg">
            <el-table height="600" :data="jobList" border style="width: 100%" show-overflow-tooltip empty-text="暂无岗位数据">
                <el-table-column fixed type="index" label="序号" width="70" align="center" />
                <el-table-column prop="id" label="ID" width="100" align="center" />
                <el-table-column prop="employerId" label="雇主ID" width="100" align="center" />
                <el-table-column prop="jobName" label="岗位名称" width="180" />
                <el-table-column prop="jobType" label="岗位类型" width="120" />
                <el-table-column prop="workAddress" label="工作地址" min-width="200" />
                <el-table-column prop="workStartTime" label="工作开始时间" width="120" />
                <el-table-column prop="workEndTime" label="工作结束时间" width="120" />
                <el-table-column prop="dailySalary" label="日薪(元)" width="100">
                    <template #default="scope">
                        {{ scope.row.dailySalary || 0 }}
                    </template>
                </el-table-column>
                <el-table-column prop="recruitNum" label="招聘人数" width="100" align="center" />
                <el-table-column prop="jobDesc" label="岗位描述" min-width="200" />
                <el-table-column prop="jobRequire" label="岗位要求" min-width="200" />
                <!-- 发布状态格式化 -->
                <el-table-column prop="publishStatus" label="发布状态" width="120" align="center">
                    <template #default="scope">
                        <el-tag :type="getStatusTagType(scope.row.publishStatus)">
                            {{ getStatusText(scope.row.publishStatus) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <!-- 时间格式化 -->
                <el-table-column prop="createTime" label="创建时间" width="180">
                    <template #default="scope">
                        {{ formatTime(scope.row.createTime) }}
                    </template>
                </el-table-column>
                <el-table-column prop="updateTime" label="更新时间" width="180">
                    <template #default="scope">
                        {{ formatTime(scope.row.updateTime) }}
                    </template>
                </el-table-column>
                <el-table-column fixed="right" label="操作" width="180" align="center">
                    <template #default="scope">
                        <el-button type="primary" size="small" @click="handleEdit(scope.row)">
                            编辑
                        </el-button>
                        <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页组件 -->
            <div class="pagination-container" style="margin-top: 20px; text-align: right;">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </div>
    </div>
</template>

<script setup>
// ========== 关键修改1：替换原生axios为全局request实例 ==========
// import axios from 'axios'; // ❌ 移除原生axios
import request from '@/api/index.js'; // ✅ 导入全局request（自动加Token）
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus'; // 新增ElMessageBox，删除前确认
// 导入封装好的岗位查询API
import { getAllJobs } from '@/api/modules/admin/job.js';

// 分页响应式变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 初始化响应式数据
const jobList = ref([]);
const loading = ref(false);
const errorMsg = ref('');

// 改造后的查询方法（支持分页）
const search = async () => {
    loading.value = true;
    errorMsg.value = '';
    try {
        const res = await getAllJobs({
            pageNum: currentPage.value,
            pageSize: pageSize.value
        });
        jobList.value = res.list || [];
        total.value = res.total || 0;
        if (res.list.length === 0) {
            ElMessage.info('暂无岗位数据');
        }
    } catch (err) {
        if (err.message.includes('Network Error')) {
            errorMsg.value = '网络错误：请检查后端服务是否启动（localhost:8080）';
        } else if (err.response) {
            errorMsg.value = `后端错误：${err.response.status} - ${err.response.data?.message || '接口不存在'}`;
        } else {
            errorMsg.value = `请求异常：${err.message}`;
        }
        console.error('查询岗位失败详情：', err);
    } finally {
        loading.value = false;
    }
};

// 分页事件处理
const handleSizeChange = (newPageSize) => {
    pageSize.value = newPageSize;
    search();
};
const handleCurrentChange = (newCurrentPage) => {
    currentPage.value = newCurrentPage;
    search();
};

// 发布状态文字映射
const getStatusText = (code) => {
    const statusMap = {
        0: '未审核',
        1: '已审核',
        2: '已结束'
    };
    return statusMap[code] || `未知状态(${code})`;
};

// 发布状态标签样式
const getStatusTagType = (code) => {
    const typeMap = {
        0: 'info',
        1: 'success',
        2: 'info'
    };
    return typeMap[code] || 'info';
};

// 时间格式化
const formatTime = (timeStr) => {
    if (!timeStr) return '-';
    return timeStr.replace('T', ' ').substring(0, 19);
};

// 新增岗位（占位）
const handleAdd = () => {
    ElMessage.info('新增岗位功能待实现');
};

// 编辑岗位（占位）
const handleEdit = (row) => {
    ElMessage.info(`编辑岗位ID：${row.id}`);
};

// ========== 关键修改2：修复删除逻辑，添加确认弹窗+使用request ==========
const handleDelete = async (id) => {
    try {
        // 第一步：删除前确认（用户体验优化）
        await ElMessageBox.confirm(
            '确定要删除该岗位吗？删除后数据无法恢复！',
            '删除确认',
            {
                confirmButtonText: '确认删除',
                cancelButtonText: '取消',
                type: 'warning'
            }
        );

        // 第二步：使用全局request发送删除请求（自动带Token）
        // ✅ 路径简化为/job/${id}（request的baseURL已包含/api）
        await request.delete(`/job/admin/delete/${id}`);
        
        ElMessage.success('岗位删除成功！');
        search(); // 删除后刷新列表
    } catch (err) {
        // 区分：取消删除 vs 真正的错误
        if (err.message !== 'cancel') {
            ElMessage.error(`删除失败：${err.message || '权限不足或接口异常'}`);
        }
    }
};

// 页面挂载时查询
onMounted(() => {
    search();
});
</script>

<style scoped>
.job {
    margin: 30px;
}

.containes {
    margin: 10px 0px;
}

/* 加载/错误提示样式 */
.loading-tip {
    text-align: center;
    padding: 20px;
    color: #666;
    font-size: 14px;
}

.error-tip {
    text-align: center;
    padding: 20px;
    color: #f56c6c;
    font-size: 14px;
}

/* 表格样式优化 */
:deep(.el-table) {
    --el-table-header-text-color: #333;
    --el-table-row-hover-bg-color: #f8f9fa;
}

/* 分页容器样式 */
.pagination-container {
    margin-top: 20px;
    text-align: right;
}
</style>