// src/api/modules/login.js
// 模块化管理登录相关接口（原user.js的登录接口迁移至此）
// import request from '@/api/index'

// // 登录接口：入参为用户名、密码
// export const loginApi = (username, password) => {
//   return request({
//     url: '/user/login',
//     method: 'POST',
//     params: { username, password } // 与后端Controller的@RequestParam对应
//   })
// }

// import request from '../index' // 导入全局Axios实例

// /**
//  * 登录接口
//  * @param {Object} userData {username, password}
//  * @returns {Promise<LoginInfo>} 登录信息（包含token、roleType等）
//  */
// export const loginApi = (userData) => {
//   // 后端接口路径：/api/user/login（Vite代理会转发到8080）
//   return request.post('/user/login', userData)
// }

import request from '../index'

/**
 * 登录接口（适配后端@RequestParam接收URL参数）
 * @param {String} username 用户名
 * @param {String} password 密码
 * @returns {Promise<LoginInfo>}
 */
export const loginApi = (username, password) => {
  return request.post(
    '/user/login',
    {}, // POST请求体为空（后端不从body拿参数）
    {
      params: { username, password } // 把参数拼到URL后（?username=xxx&password=xxx）
    }
  )
}

// 后续可在此添加退出登录、刷新令牌等登录相关接口