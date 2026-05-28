// ===== 发布状态映射（岗位相关） =====
export const publishStatusMap = {
    0: { text: '未审核', tagType: 'info' },
    1: { text: '已审核', tagType: 'success' },
    2: { text: '已结束', tagType: 'warning' }
}

export const getPublishStatusText = (code) => publishStatusMap[code]?.text || '未知'
export const getPublishStatusTagType = (code) => publishStatusMap[code]?.tagType || 'info'

// ===== 申请状态映射 =====
export const applyStatusMap = {
    0: { text: '待审核', tagType: 'warning' },
    1: { text: '已通过', tagType: 'success' },
    2: { text: '已拒绝', tagType: 'danger' },
    3: { text: '已入职', tagType: 'primary' },
    4: { text: '已离职', tagType: 'info' }
}

export const getApplyStatusText = (code) => applyStatusMap[code]?.text || '未知'
export const getApplyStatusTagType = (code) => applyStatusMap[code]?.tagType || 'info'

// ===== 角色映射 =====
export const roleMap = {
    0: { text: '管理员', tagType: 'danger' },
    1: { text: '雇主', tagType: 'primary' },
    2: { text: '求职者', tagType: 'success' }
}

export const getRoleText = (code) => roleMap[code]?.text || '未知'
export const getRoleTagType = (code) => roleMap[code]?.tagType || 'info'

// ===== 账号状态映射 =====
export const accountStatusMap = {
    0: { text: '已禁用', tagType: 'danger' },
    1: { text: '已启用', tagType: 'success' }
}

export const getAccountStatusText = (code) => accountStatusMap[code]?.text || '未知'
export const getAccountStatusTagType = (code) => accountStatusMap[code]?.tagType || 'info'

// ===== 时间格式化 =====
export const formatTime = (str) => {
    if (!str) return '-'
    return str.replace('T', ' ').substring(0, 19)
}

export { formatDate, formatDateTime } from '@/utils/format.js'
