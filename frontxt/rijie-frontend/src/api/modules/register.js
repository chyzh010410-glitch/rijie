// src/api/modules/register.js
import request from '../index'

/**
 * 用户注册接口（雇主/求职者）
 * @param {Object} userData 注册信息（username/password/roleType等）
 * @returns {Promise<Result>} 注册结果
 */
export const registerApi = (userData) => {
    // 后端接口路径：/api/user/register（Vite代理转发到8080）
    return request.post('/user/register', userData)
}