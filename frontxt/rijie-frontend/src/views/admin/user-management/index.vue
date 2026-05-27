<template>
  <div class="user-management">
    <h1>用户管理</h1>

    <div class="filter-container">
      <el-select
        v-model="roleType"
        placeholder="按角色筛选"
        clearable
        style="width: 200px"
        @change="handleRoleFilter"
      >
        <el-option label="全部用户" :value="''" />
        <el-option label="管理员" :value="0" />
        <el-option label="雇主" :value="1" />
        <el-option label="求职者" :value="2" />
      </el-select>
    </div>

    <div v-if="loading" class="loading-tip">加载中...</div>
    <div v-if="errorMsg" class="error-tip">{{ errorMsg }}</div>

    <div class="table-container" v-if="!loading && !errorMsg">
      <el-table
        height="600"
        :data="userList"
        border
        style="width: 100%"
        show-overflow-tooltip
        empty-text="暂无用户数据"
      >
        <el-table-column fixed type="index" label="序号" width="70" align="center" />
        <el-table-column prop="id" label="用户ID" width="100" align="center" />
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="realName" label="真实姓名" width="130" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />

        <el-table-column prop="roleType" label="角色类型" width="130" align="center">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.roleType)">
              {{ getRoleText(scope.row.roleType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="190">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column prop="updateTime" label="更新时间" width="190">
          <template #default="scope">
            {{ formatTime(scope.row.updateTime) }}
          </template>
        </el-table-column>

        <el-table-column fixed="right" label="操作" width="160" align="center">
          <template #default="scope">
            <el-button
              link
              :type="scope.row.status === 1 ? 'danger' : 'success'"
              @click="handleToggleStatus(scope.row.id, scope.row.status === 1 ? 0 : 1)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页（完全套用你岗位管理的写法） -->
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAllUsers,
  getUsersByRoleType,
  updateUserStatus
} from '@/api/modules/admin/user.js'

// 分页变量（和岗位管理完全一样）
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const roleType = ref('')
const userList = ref([])
const loading = ref(false)
const errorMsg = ref('')

// 加载用户列表
const fetchUserList = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    let res
    const pageParams = { pageNum: currentPage.value, pageSize: pageSize.value }

    if (roleType.value === '' || roleType.value === undefined) {
      res = await getAllUsers(pageParams)
    } else {
      res = await getUsersByRoleType(roleType.value, pageParams)
    }

    // ✅ 核心修复：取 res.list，且兜底为空数组
    userList.value = res?.list || [] 
    total.value = res?.total || 0

    if (userList.value.length === 0) {
      ElMessage.info('暂无用户数据')
    }
  } catch (err) {
    // 错误处理保持不变
    if (err.message.includes('Network Error')) {
      errorMsg.value = '网络错误：请检查后端是否启动'
    } else if (err.response) {
      errorMsg.value = `后端错误 ${err.response.status}：${err.response.data?.message || '异常'}`
    } else {
      errorMsg.value = '请求异常：' + err.message
    }
    console.error('获取用户列表失败', err)
  } finally {
    loading.value = false
  }
}

// 角色筛选
const handleRoleFilter = () => {
  currentPage.value = 1
  fetchUserList()
}

// 分页事件
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchUserList()
}
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUserList()
}

// 启用/禁用用户
const handleToggleStatus = async (id, targetStatus) => {
  const text = targetStatus === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(
      `确定要${text}该用户吗？`,
      '确认操作',
      { type: 'warning' }
    )

    await updateUserStatus({ id, status: targetStatus })
    ElMessage.success(text + '成功')
    fetchUserList()
  } catch (err) {
    if (err.message !== 'cancel') {
      ElMessage.error(text + '失败：' + err.message)
    }
  }
}

// 角色映射
const getRoleText = (code) => {
  const map = { 0: '管理员', 1: '雇主', 2: '求职者' }
  return map[code] || '未知'
}
const getRoleTagType = (code) => {
  const map = { 0: 'danger', 1: 'primary', 2: 'success' }
  return map[code] || 'info'
}

// 时间格式化
const formatTime = (str) => {
  if (!str) return '-'
  return str.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.user-management {
  margin: 30px;
}
.filter-container {
  margin: 10px 0 20px;
}
.loading-tip, .error-tip {
  text-align: center;
  padding: 20px;
  font-size: 14px;
}
.loading-tip { color: #666; }
.error-tip { color: #f56c6c; }
.table-container { margin-top: 20px; }
:deep(.el-table) {
  --el-table-header-text-color: #333;
  --el-table-row-hover-bg-color: #f8f9fa;
}
</style>