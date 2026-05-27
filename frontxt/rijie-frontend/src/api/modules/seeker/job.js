import request from '@/api/index.js';

/**
 * 求职者端：分页查询已审核通过的岗位
 * @param {Object} params - { jobType, pageNum, pageSize }
 * @returns {Promise} 分页结果 { list: [], total: 0 }
 */
export const getApprovedJobsForSeeker = (params) => {
    return request.get('/job/published/list', {
        params: params
    });
};

/**
 * 求职者端：模糊搜索已审核岗位（适配handleSearch）
 * @param {Object} params - { keyword, jobType, pageNum, pageSize }
 * @returns {Promise} 分页结果 { list: [], total: 0 }
 */
export const searchApprovedJobs = (params) => {
    return request.get('/job/published/search', {
        params: params
    });
};

// ✅ 新增：调用后端现成的【岗位详情接口】，路径传参
export const getJobDetailById = (jobId) => {
    return request({
        url: `/job/detail/${jobId}`, // 对应后端 /detail/{id} 路径传参
        method: 'GET'
    })
}
