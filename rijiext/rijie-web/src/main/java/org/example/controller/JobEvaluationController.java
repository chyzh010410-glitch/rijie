package org.example.controller;

import jakarta.annotation.Resource;
import org.example.pojo.JobEvaluation;
import org.example.pojo.Result;
import org.example.service.JobEvaluationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluation")
public class JobEvaluationController {

    @Resource
    private JobEvaluationService evaluationService;

    /** 求职者提交评价 */
    @PostMapping("/add")
    public Result add(@RequestBody JobEvaluation evaluation) {
        return evaluationService.addEvaluation(evaluation);
    }

    /** 求职者查看我提交的评价 */
    @GetMapping("/my/{seekerId}")
    public Result myEvaluation(@PathVariable Long seekerId) {
        return evaluationService.getMyEvaluation(seekerId);
    }

    /** 雇主查看收到的评价 */
    @GetMapping("/employer/{employerId}")
    public Result employerEvaluation(@PathVariable Long employerId) {
        return evaluationService.getEmployerEvaluation(employerId);
    }

    /** 管理员查看所有评价 */
    @GetMapping("/admin/list")
    public Result adminList() {
        return evaluationService.getAllEvaluation();
    }

    /** 管理员删除评价 */
    @DeleteMapping("/admin/{id}")
    public Result delete(@PathVariable Long id) {
        return evaluationService.deleteEvaluation(id);
    }

    /** 查询某个岗位的所有评价（公开） */
    @GetMapping("/job/{jobId}")
    public Result getJobEvaluation(@PathVariable Long jobId) {
        return evaluationService.getEvaluationByJobId(jobId);
    }

    /** 检查用户是否有评价权限 */
    @GetMapping("/check-permission")
    public Result checkPermission(@RequestParam Long seekerId,
                                  @RequestParam Long jobId) {
        return evaluationService.checkEvaluationPermission(seekerId, jobId);
    }

    /** 获取用户信誉分详情 */
    @GetMapping("/reputation/{userId}")
    public Result getUserReputation(@PathVariable Long userId) {
        return evaluationService.getUserReputationDetail(userId);
    }
}
