import request from '@/api/index.js'

// 管理员统计接口
export const getAdminOverview = () => request({ url: '/stat/admin/overview', method: 'get' })
export const getUserGrowth7d = () => request({ url: '/stat/admin/user/growth/7d', method: 'get' })
export const getJobPublish12m = () => request({ url: '/stat/admin/job/publish/12m', method: 'get' })
export const getJobTypeDistribution = () => request({ url: '/stat/admin/job/type/distribution', method: 'get' })
export const getUserRoleDistribution = () => request({ url: '/stat/admin/user/role/distribution', method: 'get' })