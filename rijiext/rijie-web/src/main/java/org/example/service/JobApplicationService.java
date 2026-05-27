package org.example.service;

import org.example.pojo.JobApplication;

import java.util.List;
import java.util.Map;

/**
 * 岗位申请业务层接口
 */
public interface JobApplicationService {

    /**
     * 求职者提交岗位申请
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @return 操作结果：true=成功，false=失败（如重复申请）
     */
    boolean applyJob(Long seekerId, Long jobId);

    /**
     * 求职者查询自己的所有申请记录
     * @param seekerId 求职者ID
     * @return 申请记录列表
     */
    List<JobApplication> getMyApplications(Long seekerId);

    /**
     * 求职者查询自己的申请状态
     * @param seekerId,jobId 求职者ID,岗位id
     * @return 申请状态：0-待审核，1-已通过，2-已拒绝，3-已入职
     */
    JobApplication getApplyStatus(Long seekerId, Long jobId);

    /**
     * 雇主查询自己岗位的所有申请记录
     * @param employerId 雇主ID
     * @return 申请记录列表
     */
//    List<JobApplication> getJobApplications(Long employerId);
    List<Map<String, Object>> getJobApplications(Long employerId);
    /**
     * 雇主审核申请（通过/拒绝）
     * @param id 申请ID
     * @param applyStatus 目标状态：1-已通过，2-已拒绝
     * @param rejectReason 拒绝原因（仅状态为2时传值）
     * @return 操作结果：true=成功，false=失败
     */
    boolean auditApplication(Long id, Integer applyStatus, String rejectReason);

    /**
     * 求职者确认入职（将申请状态改为3-已入职）
     * @param id 申请ID
     * @return 操作结果：true=成功，false=失败
     */
    boolean confirmEntry(Long id);
}