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