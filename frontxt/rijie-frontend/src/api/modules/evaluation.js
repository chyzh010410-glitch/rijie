import request from '@/api/index.js'

// 【公开接口】查询指定岗位的所有评价（求职者/雇主/管理员都可调用）
export const getJobEvaluation = (jobId) => {
    return request({
        url: `/evaluation/job/${jobId}`,
        method: 'get'
    })
}

// 【求职者】提交评价（seekerId由后端从JWT读取）
export const addEvaluation = (data) => {
    return request({
        url: '/evaluation/add',
        method: 'post',
        data: data
    })
}

// 【求职者】查询我的所有评价
export const getMyEvaluation = () => {
    return request({ url: '/evaluation/my', method: 'get' })
}

// 【雇主】查询我收到的所有评价
export const getEmployerEvaluation = () => {
    return request({ url: '/evaluation/employer', method: 'get' })
}

// 【管理员】查询所有评价
export const getAdminEvaluationList = () => {
    return request({
        url: '/evaluation/admin/list',
        method: 'get'
    })
}

// 【管理员】删除评价
export const deleteEvaluation = (id) => {
    return request({
        url: `/evaluation/admin/${id}`,
        method: 'delete'
    })
}