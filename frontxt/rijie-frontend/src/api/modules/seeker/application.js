import request from '@/api/index.js';

/**
 * 求职者提交岗位申请
 * @param {Object} params - { seekerId: 求职者ID, jobId: 岗位ID }
 * @returns {Promise} 申请结果
 */
export const submitJobApplication = (params) => {
    return request.post('/application/apply', null, { params })
};

export const getApplyStatus = (params) => {
    return request({ url: '/application/status', method: 'GET', params })
};

export const getMyApplications = () => {
    return request({ url: '/application/my', method: 'GET' })
};