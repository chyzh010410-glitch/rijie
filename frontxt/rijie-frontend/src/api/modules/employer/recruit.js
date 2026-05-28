import request from '@/api/index.js';

/**
 * 1. 雇主查询自己岗位的申请记录
 * @param {Number} employerId - 雇主ID（从登录态获取）
 * @returns {Promise} 申请记录列表
 */
export const getEmployerApplications = () => {
    return request.get('/application/employer');
};

/**
 * 2. 雇主审核申请
 * @param {Object} params - { id:申请ID, applyStatus:1通过/2拒绝, rejectReason:拒绝原因 }
 * @returns {Promise} 操作结果
 */
export const auditApplication = (params) => {
    return request.put('/application/audit', null, {
        params: params
    });
};