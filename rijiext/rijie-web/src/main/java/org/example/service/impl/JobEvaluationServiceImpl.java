package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.mapper.AttendanceMapper;
import org.example.mapper.JobEvaluationMapper;
import org.example.mapper.SalaryMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.Attendance;
import org.example.pojo.JobEvaluation;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.JobEvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobEvaluationServiceImpl implements JobEvaluationService {

    @Resource
    private JobEvaluationMapper evaluationMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private AttendanceMapper attendanceMapper;

    @Resource
    private SalaryMapper salaryMapper;

    @Override
    @Transactional
    public Result addEvaluation(JobEvaluation evaluation) {
        if (evaluation == null || evaluation.getSeekerId() == null
                || evaluation.getJobId() == null || evaluation.getEmployerId() == null) {
            return Result.fail("参数错误");
        }

        if (evaluation.getScore() == null || evaluation.getScore() < 1 || evaluation.getScore() > 5) {
            return Result.fail("评分必须在1-5之间");
        }

        if (evaluation.getSeekerId().equals(evaluation.getEmployerId())) {
            return Result.fail("不能评价自己");
        }

        // 重复评价检查
        int count = evaluationMapper.countBySeekerAndJob(evaluation.getSeekerId(), evaluation.getJobId());
        if (count > 0) {
            return Result.fail("您已评价过该岗位");
        }

        // 权限检查
        Result permResult = checkEvaluationPermission(evaluation.getSeekerId(), evaluation.getJobId());
        if (permResult.getCode() == 500) {
            return Result.fail("您暂无评价权限，请完成工作并等待薪资发放");
        }

        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setStatus(1);
        if (evaluation.getIsAnonymous() == null) {
            evaluation.setIsAnonymous(0);
        }

        int rows = evaluationMapper.insertEvaluation(evaluation);
        if (rows <= 0) {
            return Result.fail("评价提交失败");
        }

        // 更新被评价雇主信誉分
        Double avgScore = evaluationMapper.calculateAvgScoreForEmployer(evaluation.getEmployerId());
        evaluationMapper.updateUserReputation(evaluation.getEmployerId(), avgScore, evaluation.getScore());

        return Result.success("评价提交成功");
    }

    @Override
    public Result getMyEvaluation(Long seekerId) {
        if (seekerId == null) {
            return Result.fail("参数错误");
        }
        List<JobEvaluation> list = evaluationMapper.selectBySeekerId(seekerId);
        return Result.success(list);
    }

    @Override
    public Result getEmployerEvaluation(Long employerId) {
        if (employerId == null) {
            return Result.fail("参数错误");
        }
        List<JobEvaluation> list = evaluationMapper.selectByEmployerIdWithDetails(employerId);
        // 匿名评价隐藏评价人信息
        list.forEach(e -> {
            if (e.getIsAnonymous() != null && e.getIsAnonymous() == 1) {
                e.setSeekerName("匿名用户");
                e.setSeekerRealName(null);
            }
        });
        return Result.success(list);
    }

    @Override
    public Result getAllEvaluation() {
        List<JobEvaluation> list = evaluationMapper.selectAllWithDetails();
        list.forEach(e -> {
            if (e.getIsAnonymous() != null && e.getIsAnonymous() == 1) {
                e.setSeekerName("匿名用户");
                e.setSeekerRealName(null);
            }
        });
        return Result.success(list);
    }

    @Override
    public Result deleteEvaluation(Long id) {
        if (id == null || id <= 0) {
            return Result.fail("参数错误");
        }
        evaluationMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @Override
    public Result getEvaluationByJobId(Long jobId) {
        if (jobId == null) {
            return Result.fail("参数错误");
        }
        List<JobEvaluation> list = evaluationMapper.selectByJobIdWithDetails(jobId);
        list.forEach(e -> {
            if (e.getIsAnonymous() != null && e.getIsAnonymous() == 1) {
                e.setSeekerName("匿名用户");
                e.setSeekerRealName(null);
            }
        });
        return Result.success(list);
    }

    @Override
    public Result checkEvaluationPermission(Long seekerId, Long jobId) {
        if (seekerId == null || jobId == null) {
            return Result.fail("参数错误");
        }

        // 检查最近60天的考勤记录
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(60);

        List<Attendance> attendances = attendanceMapper.selectAttendancesBySeekerAndJobRange(
                seekerId, jobId, startDate, endDate);

        if (attendances == null || attendances.isEmpty()) {
            return Result.fail("无考勤记录");
        }

        // 检查薪资是否已发放
        for (Attendance att : attendances) {
            int salaryCount = salaryMapper.countSalary(seekerId, jobId, att.getWorkDate());
            if (salaryCount > 0) {
                return Result.success(true);
            }
        }

        return Result.fail("薪资尚未发放");
    }

    @Override
    public Result getUserReputationDetail(Long userId) {
        if (userId == null) {
            return Result.fail("参数错误");
        }

        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        double reputationScore = user.getReputationScore() != null ? user.getReputationScore() : 5.0;
        int totalRatings = user.getTotalRatings() != null ? user.getTotalRatings() : 0;
        int positiveRatings = user.getPositiveRatings() != null ? user.getPositiveRatings() : 0;
        int negativeRatings = user.getNegativeRatings() != null ? user.getNegativeRatings() : 0;

        double goodRate = totalRatings == 0 ? 0 : (double) positiveRatings / totalRatings * 100;

        String level;
        if (reputationScore >= 4.5) level = "优秀";
        else if (reputationScore >= 4.0) level = "良好";
        else if (reputationScore >= 3.0) level = "一般";
        else if (reputationScore >= 2.0) level = "较差";
        else level = "差";

        // 最近评价
        List<JobEvaluation> recentEvaluations = evaluationMapper.selectByEmployerIdWithDetails(userId);
        if (recentEvaluations.size() > 10) {
            recentEvaluations = recentEvaluations.subList(0, 10);
        }
        recentEvaluations.forEach(e -> {
            if (e.getIsAnonymous() != null && e.getIsAnonymous() == 1) {
                e.setSeekerName("匿名用户");
                e.setSeekerRealName(null);
            }
        });

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("reputationScore", reputationScore);
        result.put("totalRatings", totalRatings);
        result.put("positiveRatings", positiveRatings);
        result.put("negativeRatings", negativeRatings);
        result.put("goodRate", String.format("%.1f%%", goodRate));
        result.put("reputationLevel", level);
        result.put("recentEvaluations", recentEvaluations);

        return Result.success(result);
    }
}
