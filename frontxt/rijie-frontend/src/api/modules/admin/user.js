import request from '@/api/index.js'

/**
 * 管理员查询所有用户（带分页）
 * @param {Object} params { pageNum: 1, pageSize: 10 }
 * @returns {Promise} 分页结果 { list: [], total: 0 }
 */
export const getAllUsers = () => {
    return request({
        url: '/user/admin/all',
        method: 'get'
    })
}

/**
 * 管理员按角色类型查询用户（带分页）
 * @param {Number} roleType 0-管理员，1-雇主，2-求职者
 * @param {Object} params { pageNum: 1, pageSize: 10 }
 * @returns {Promise} 分页结果
 */
export const getUsersByRoleType = (roleType) => {
    return request({
        url: `/user/admin/role/${roleType}`,
        method: 'get'
    })
}

/**
 * 管理员修改用户状态（启用/禁用）
 * @param {Object} params {id: 用户ID, status: 0-禁用，1-启用}
 * @returns {Promise} 操作结果
 */
export const updateUserStatus = (params) => {
    return request({
        url: '/user/admin/status/update',
        method: 'put',
        params
    })
}