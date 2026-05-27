// src/api/modules/attendance.js 考勤接口统一封装
import request from '../index'

// ===================== 求职者接口 =====================
/**
 * 求职者签到
 * @param {Object} params {seekerId, jobId, workDate}
 */
export const signIn = (params) => {
    return request({
        url: '/attendance/sign/in',
        method: 'POST',
        params: params // 后端@RequestParam接收，用params传参 ✔️
    })
}

/**
 * 求职者签退
 * @param {Object} params {seekerId, jobId, workDate}
 */
export const signOut = (params) => {
    return request({
        url: '/attendance/sign/out',
        method: 'POST',
        params: params
    })
}

/**
 * 求职者查询个人考勤记录
 * @param {Number} seekerId 求职者ID
 */
export const getMyAttendance = (seekerId) => {
    return request({
        url: '/attendance/my',
        method: 'GET',
        params: { seekerId }
    })
}

// ===================== 雇主接口 =====================
/**
 * 雇主查询名下所有考勤记录
 * @param {Number} employerId 雇主ID
 */
export const getEmployerAttendance = (employerId) => {
    return request({
        url: '/attendance/employer',
        method: 'GET',
        params: { employerId }
    })
}

/**
 * 雇主手动更新考勤状态
 * @param {Object} params {id:考勤ID, attendanceStatus:状态码}
 */
export const updateAttendanceStatus = (params) => {
    return request({
        url: '/attendance/status/update',
        method: 'PUT',
        params: params // 后端@RequestParam接收 ✔️
    })
}