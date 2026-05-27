// src/api/modules/job.js
import request from '../../index' // 导入全局Axios实例

/**
 * 管理员查询所有岗位（未审核/未审核/已结束）
 * @returns {Promise<Array>} 岗位列表
 * 获取所有岗位（支持分页）
 * @param {Object} params - 分页参数 { pageNum, pageSize }
 * @returns {Promise} 分页结果 { list: [], total: 0 }
 */
export const getAllJobs = (params) => {
    return request.get('/job/admin/all',
        { params: params }
    ) // 对应后端接口：/api/job/admin/all（Vite代理后转后端地址），传递分页参数给后端

}

/**
 * 管理员新增岗位（复用雇主发布接口）
 * @param {Object} jobForm 岗位表单（需包含employerId、jobName、dailySalary等必填项）
 * @returns {Promise}
 */
export const adminAddJob = (jobForm) => {
    return request.post('/job/publish', jobForm); // 直接调用雇主的publish接口
};

/**
 * 管理员获取所有未审核岗位列表
 * @param {Object} params - 分页参数（pageNum/pageSize）
 * @returns {Promise} 未审核岗位列表
 */
export const getUnreviewedJobs = (params) => {
    return request.get('/job/admin/list', { params })
}

/**
 * 管理员更新岗位状态（审核通过/拒绝）
 * @param {Number} id - 岗位ID
 * @param {Number} publishStatus - 目标状态（1=已审核）
 * @returns {Promise} 操作结果
 */
export const adminUpdateJobStatus = (id, publishStatus) => {
    return request.put('/job/admin/status/update', null, {
        params: { id, publishStatus }
    })
}

/**
 * 管理员删除岗位（核心新增）
 * @param {Number|String} id - 要删除的岗位ID
 * @returns {Promise} - 后端返回的操作结果
 */
// export const deleteJob = (id) => {
//     return request({
//         url: `/job/admin/delete/${id}`, // 删除接口路径：/api/job/{id}
//         method: 'delete'   // 请求方式：DELETE
//     })
// }
export const deleteJob = (id) => {
    return request.delete(`/job/admin/delete/${id}`) // 对应后端@PathVariable的路径参数
}