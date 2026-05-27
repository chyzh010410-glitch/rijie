import request from '@/api/index.js'

// 获取仪表盘统计数据
export const getDashboardStats = () => {
    return request({
        url: '/dashboard/stats', // 对应后端接口路径
        method: 'get'
    });
};