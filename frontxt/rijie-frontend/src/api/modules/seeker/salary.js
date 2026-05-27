// src/api/modules/seeker/salary.js
import request from '@/api/index.js'; // 复用项目原有axios封装

/**
 * 自动算薪（打卡后触发）
 * @param {Object} params - seekerId:求职者ID, jobId:岗位ID, workDate:工作日期（可选）
 * @returns {Promise}
 */
export const calculateSalary = (params) => {
    return request({
        url: '/salary/calculate',
        method: 'post',
        params // 后端是@RequestParam，用params传参
    })
}

/**
 * 求职者查询个人薪资记录
 * @param {Number} seekerId - 求职者ID
 * @returns {Promise}
 */
export const getMySalaries = (seekerId) => {
    return request({
        url: '/salary/my',
        method: 'get',
        params: { seekerId }
    })
}