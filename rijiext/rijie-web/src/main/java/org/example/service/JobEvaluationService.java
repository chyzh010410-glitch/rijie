package org.example.service;

import org.example.pojo.JobEvaluation;
import org.example.pojo.Result;

public interface JobEvaluationService {

    /** 提交评价 */
    Result addEvaluation(JobEvaluation evaluation);

    /** 求职者查看我提交的评价 */
    Result getMyEvaluation(Long seekerId);

    /** 雇主查看收到的评价（含详细信息） */
    Result getEmployerEvaluation(Long employerId);

    /** 管理员查看所有评价（含详细信息） */
    Result getAllEvaluation();

    /** 管理员删除评价 */
    Result deleteEvaluation(Long id);

    /** 根据岗位ID查询评价（公开） */
    Result getEvaluationByJobId(Long jobId);

    /** 检查用户是否有评价权限 */
    Result checkEvaluationPermission(Long seekerId, Long jobId);

    /** 获取用户信誉分详情 */
    Result getUserReputationDetail(Long userId);
}
