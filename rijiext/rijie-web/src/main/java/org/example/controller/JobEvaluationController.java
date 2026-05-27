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

    // 员工：提交评价
    @PostMapping("/add")
    public Result add(@RequestBody JobEvaluation evaluation) {
        return evaluationService.addEvaluation(evaluation);
    }

    // 员工：查看我的评价
    @GetMapping("/my/{seekerId}")
    public Result myEvaluation(@PathVariable Long seekerId) {
        return evaluationService.getMyEvaluation(seekerId);
    }

    // 雇主：查看收到的评价
    @GetMapping("/employer/{employerId}")
    public Result employerEvaluation(@PathVariable Long employerId) {
        return evaluationService.getEmployerEvaluation(employerId);
    }

    // 管理员：查看所有评价
    @GetMapping("/admin/list")
    public Result adminList() {
        return evaluationService.getAllEvaluation();
    }

    // 管理员：删除评价
    @DeleteMapping("/admin/{id}")
    public Result delete(@PathVariable Long id) {
        return evaluationService.deleteEvaluation(id);
    }

    // 公开接口：查询某个岗位的所有评价（求职者/雇主/管理员都能调用）
    @GetMapping("/job/{jobId}")
    public Result getJobEvaluation(@PathVariable Long jobId) {
        return evaluationService.getEvaluationByJobId(jobId);
    }
}