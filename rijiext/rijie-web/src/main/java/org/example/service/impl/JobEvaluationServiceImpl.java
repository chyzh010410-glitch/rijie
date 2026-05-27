package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.mapper.JobEvaluationMapper;
import org.example.pojo.JobEvaluation;
import org.example.pojo.Result;
import org.example.service.JobEvaluationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobEvaluationServiceImpl implements JobEvaluationService {

    @Resource
    private JobEvaluationMapper evaluationMapper;

    // 新增评价
    @Override
    public Result addEvaluation(JobEvaluation evaluation) {
        // 校验：重复评价
        JobEvaluation old = evaluationMapper.selectBySeekerAndJob(evaluation.getSeekerId(), evaluation.getJobId());
        if (old != null) {
            return Result.fail("你已评价过该岗位，不可重复评价");
        }

        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setStatus(1);
        evaluationMapper.insertEvaluation(evaluation);
        return Result.success("评价提交成功");
    }

    // 员工查看我的评价
    @Override
    public Result getMyEvaluation(Long seekerId) {
        List<JobEvaluation> list = evaluationMapper.selectBySeekerId(seekerId);
        return Result.success(list);
    }

    // 雇主查看收到的评价
    @Override
    public Result getEmployerEvaluation(Long employerId) {
        List<JobEvaluation> list = evaluationMapper.selectByEmployerId(employerId);
        return Result.success(list);
    }

    // 管理员查看所有评价
    @Override
    public Result getAllEvaluation() {
        List<JobEvaluation> list = evaluationMapper.selectAll();
        return Result.success(list);
    }

    // 管理员删除评价
    @Override
    public Result deleteEvaluation(Long id) {
        evaluationMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @Override
    public Result getEvaluationByJobId(Long jobId) {
        List<JobEvaluation> list = evaluationMapper.selectByJobId(jobId);
        return Result.success(list);
    }
}