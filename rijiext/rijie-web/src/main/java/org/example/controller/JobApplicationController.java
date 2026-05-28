package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.pojo.JobApplication;
import org.example.pojo.Result;
import org.example.service.JobApplicationService;
import org.example.utils.AuthUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 岗位申请控制层
 * 处理申请提交、审核、查询等请求
 */
@RestController
@RequestMapping("/api/application")
public class JobApplicationController {

    @Resource
    private JobApplicationService applicationService;

    @jakarta.annotation.Resource
    private HttpServletRequest request;

    // ------------------------------ 求职者接口 ------------------------------
    /**
     * 提交岗位申请
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @return 操作结果
     */
    @PostMapping("/apply")
    public Result applyJob(@RequestParam Long jobId) {
        try {
            Long seekerId = AuthUtils.getCurrentUserId(request);
            boolean success = applicationService.applyJob(seekerId, jobId);
            if (success) {
                return Result.success("岗位申请提交成功，请等待雇主审核");
            } else {
                return Result.fail("岗位申请失败（可能已重复申请或参数错误）");
            }
        } catch (Exception e) {
            return Result.fail("提交申请异常：" + e.getMessage());
        }
    }

    /**
     * 求职者查询自己的申请记录
     * @param seekerId 求职者ID
     * @return 申请记录列表
     */
    @GetMapping("/my")
    public Result getMyApplications() {
        try {
            Long seekerId = AuthUtils.getCurrentUserId(request);
            List<JobApplication> applicationList = applicationService.getMyApplications(seekerId);
            return Result.success(applicationList);
        } catch (Exception e) {
            return Result.fail("查询申请记录失败：" + e.getMessage());
        }
    }

    // ✅ 新增：查询单个岗位的申请状态（seekerId + jobId）
    @GetMapping("/status")
    public Result getApplyStatus(@RequestParam Long jobId) {
        try {
            Long seekerId = AuthUtils.getCurrentUserId(request);
            JobApplication application = applicationService.getApplyStatus(seekerId, jobId);
            if (application != null) {
                // 返回申请状态（0-待审核，1-已通过，2-已拒绝，3-已入职）
                return Result.success(application.getApplyStatus());
            } else {
                return Result.fail("未查询到该岗位的申请记录");
            }
        } catch (Exception e) {
            return Result.fail("查询申请状态异常：" + e.getMessage());
        }
    }

    /**
     * 求职者确认入职
     * @param id 申请ID
     * @return 操作结果
     */
    @PutMapping("/entry/confirm")
    public Result confirmEntry(@RequestParam Long id) {
        try {
            boolean success = applicationService.confirmEntry(id);
            if (success) {
                return Result.success("入职确认成功");
            } else {
                return Result.fail("入职确认失败");
            }
        } catch (Exception e) {
            return Result.fail("确认入职异常：" + e.getMessage());
        }
    }

    // ------------------------------ 雇主接口 ------------------------------
    /**
     * 雇主查询自己岗位的申请记录
     * @param employerId 雇主ID
     * @return 申请记录列表
     */
    @GetMapping("/employer")
    public Result getJobApplications() {
        try {
            Long employerId = AuthUtils.getCurrentUserId(request);
            List<Map<String, Object>> applicationList = applicationService.getJobApplications(employerId);
            return Result.success(applicationList);
        } catch (Exception e) {
            return Result.fail("查询岗位申请记录失败：" + e.getMessage());
        }
    }

    /**
     * 雇主审核申请
     * @param id 申请ID
     * @param applyStatus 审核结果：1-通过，2-拒绝
     * @param rejectReason 拒绝原因（仅拒绝时传值）
     * @return 操作结果
     */
    @PutMapping("/audit")
    public Result auditApplication(@RequestParam Long id,
                                   @RequestParam Integer applyStatus,
                                   @RequestParam(required = false) String rejectReason) {
        try {
            boolean success = applicationService.auditApplication(id, applyStatus, rejectReason);
            if (success) {
                String msg = applyStatus == 1 ? "申请审核通过" : "申请已拒绝";
                return Result.success(msg);
            } else {
                return Result.fail("审核操作失败");
            }
        } catch (Exception e) {
            return Result.fail("审核申请异常：" + e.getMessage());
        }
    }
}