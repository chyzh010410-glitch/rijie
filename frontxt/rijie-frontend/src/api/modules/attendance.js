// src/api/modules/attendance.js 考勤接口统一封装
import request from '../index'

// ===================== 求职者接口 =====================
export const signIn = (params) => {
    return request({ url: '/attendance/sign/in', method: 'POST', params })
}

export const signOut = (params) => {
    return request({ url: '/attendance/sign/out', method: 'POST', params })
}

export const getMyAttendance = () => {
    return request({ url: '/attendance/my', method: 'GET' })
}

// ===================== 雇主接口 =====================
export const getEmployerAttendance = () => {
    return request({ url: '/attendance/employer', method: 'GET' })
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