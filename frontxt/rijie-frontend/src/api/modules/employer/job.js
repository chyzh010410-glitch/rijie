// 导入全局配置好的Axios实例
import request from '@/api/index.js'

/**
 * 雇主发布新岗位
 * @param {Object} job - 岗位信息对象（需包含employerId、jobName、dailySalary等核心字段）
 * @returns {Promise} - 后端响应的Promise
 */
export const publishJob = (job) => {
    return request.post('/job/publish', job)
}

// 新增：编辑岗位接口
export const updateJob = (job) => {
    return request.put('/job/update', job)
}

/**
 * 雇主查询自己发布的所有岗位
 * @param {Number} employerId - 雇主的用户ID
 * @returns {Promise} - 岗位列表的Promise
 */
// export const getEmployerJobs = (employerId) => {
//     return request.get('/job/employer/list', {
//         params: { employerId } // 对应后端@RequestParam的参数
//     })
// }
// 调整getEmployerJobs接口支持分页（需后端配合）
export const getEmployerJobs = (params) => {
    return request.get('/job/employer/list', { params })
}

/**
 * 雇主更新岗位发布状态
 * @param {Number} id - 岗位ID
 * @param {Number} publishStatus - 目标状态（0=草稿，1=已发布，2=已结束）
 * @returns {Promise} - 操作结果的Promise
 */
export const updateJobStatus = (id, publishStatus) => {
    return request.put('/job/status/update', null, {
        params: { id, publishStatus } // 对应后端@RequestParam的参数
    })
}

/**
 * 雇主删除自己的岗位
 * @param {Number} id - 岗位ID
 * @returns {Promise} - 操作结果的Promise
 */
export const deleteJob = (id) => {
    return request.delete(`/job/delete/${id}`) // 对应后端@PathVariable的路径参数
}