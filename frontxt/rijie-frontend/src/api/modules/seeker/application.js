import request from '@/api/index.js';

/**
 * 求职者提交岗位申请
 * @param {Object} params - { seekerId: 求职者ID, jobId: 岗位ID }
 * @returns {Promise} 申请结果
 */
export const submitJobApplication = (params) => {
    return request.post('/application/apply', null, {
        params: params // 后端用@RequestParam接收，所以传query参数
    });
};

// 新增：查询当前岗位的申请状态
export const getApplyStatus = (params) => {
    return request({
        url: '/application/status',
        method: 'GET',
        params: params // 传seekerId和jobId
    })
};

/**
 * ✅ 新增：查询我的所有申请记录
 * @param {Number} seekerId - 求职者ID
 * @returns {Promise} 申请记录列表
 */
export const getMyApplications = (seekerId) => {
    return request({
        url: '/application/my',
        method: 'GET',
        params: { seekerId }
    })
};