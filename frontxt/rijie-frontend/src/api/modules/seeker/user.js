// src/api/modules/user.js 【最终完整版，直接复制覆盖】
import request from '@/api/index.js'

/**
 * 修改个人信息接口 (完美适配你的后端PUT接口)
 * @param {Object} user - 参数：{id, username, realName, phone, email} 必须带id
 * @returns Promise
 */
export const updateUserInfo = (user) => {
    return request({
        url: '/user/info/update', // ✅ 拼接后 = /api + /user/info/update = 后端完整地址 /api/user/info/update
        method: 'PUT',            // ✅ 必须是PUT，和后端@PutMapping完全对应
        data: user                // ✅ @RequestBody接收JSON，用data传参，完美匹配
    })
}
// ========== 新增：手机号实时校验接口 ==========
export const checkPhoneUnique = (phone) => {
    return request({
        url: '/user/check/phone',
        method: 'GET',
        params: { phone: phone },
        //skipErrorToast: true // 新增：跳过拦截器的自动提示
    })
}
// ========== 新增：邮箱实时校验接口 ==========
export const checkEmailUnique = (email) => {
    return request({
        url: '/user/check/email',
        method: 'GET',
        params: { email: email },
        //skipErrorToast: true // 新增：跳过拦截器的自动提示
    })
}