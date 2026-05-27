import request from '@/api/index.js';

/**
 * 1. 雇主查询自己岗位的申请记录
 * @param {Number} employerId - 雇主ID（从登录态获取）
 * @returns {Promise} 申请记录列表
 */
export const getEmployerApplications = (employerId) => {
    return request.get('/application/employer', {
        params: { employerId }
    });
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
// /**
//  * 1. 获取雇主自己发布的岗位列表
//  * @param {Number} employerId - 雇主ID（从登录态获取）
//  * @returns {Promise} 岗位列表
//  */
// export const getEmployerJobList = (employerId) => {
//     return request.get('/employer/job/list', {
//         params: { employerId }
//     });
// };

// /**
//  * 2. 获取全部求职者列表（带筛选+分页）
//  * @param {Object} params - { employerId, jobName, minAge, maxAge, gender, experience, currentPage, pageSize }
//  * @returns {Promise} 求职者列表+总数
//  */
// export const getAllCandidateList = (params) => {
//     return request.get('/api/employer/candidate/all', { params });
// };

// /**
//  * 3. 获取指定岗位的报名者列表（带分页）
//  * @param {Object} params - { jobId, currentPage, pageSize }
//  * @returns {Promise} 报名者列表+总数
//  */
// export const getApplyCandidateList = (params) => {
//     return request.get('/api/employer/candidate/apply', { params });
// };

// /**
//  * 4. 主动聘用求职者
//  * @param {Object} params - { employerId, candidateId, jobId（可选） }
//  * @returns {Promise} 操作结果
//  */
// export const recruitCandidate = (params) => {
//     return request.post('/api/employer/candidate/recruit', null, { params });
// };

// /**
//  * 5. 审核通过岗位报名申请
//  * @param {Object} params - { applyId, employerId }
//  * @returns {Promise} 操作结果
//  */
// export const passApply = (params) => {
//     return request.post('/employer/apply/pass', null, { params });
// };

// /**
//  * 6. 拒绝岗位报名申请
//  * @param {Object} params - { applyId, employerId, reason（可选） }
//  * @returns {Promise} 操作结果
//  */
// export const rejectApply = (params) => {
//     return request.post('/api/employer/apply/reject', null, { params });
// };