package org.example.controller;

import jakarta.annotation.Resource;
import org.example.pojo.Result;
import org.example.pojo.Salary;
import org.example.service.SalaryService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 薪资结算与发放控制层
 * 处理算薪、薪资查询、发放等请求
 */
@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    @Resource
    private SalaryService salaryService;

    // ------------------------------ 系统/雇主接口（算薪） ------------------------------
    /**
     * 自动算薪（可由系统定时触发，或雇主手动触发）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @param workDate 工作日期（可选，默认昨天）
     * @return 操作结果
     */
    @PostMapping("/calculate")
    public Result calculateSalary(@RequestParam Long seekerId,
                                  @RequestParam Long jobId,
                                  @RequestParam(required = false) LocalDate workDate) {
        try {
            // 若无指定日期，默认计算昨天的薪资（日结场景：次日算薪）
            LocalDate date = workDate == null ? LocalDate.now().minusDays(1) : workDate;
            boolean success = salaryService.calculateSalary(seekerId, jobId, date);
            if (success) {
                return Result.success("薪资计算成功");
            } else {
                return Result.fail("薪资计算失败（可能无考勤记录或已算薪）");
            }
        } catch (Exception e) {
            return Result.fail("算薪异常：" + e.getMessage());
        }
    }

    // ------------------------------ 求职者接口 ------------------------------
    /**
     * 求职者查询自己的薪资记录
     * @param seekerId 求职者ID
     * @return 薪资记录列表
     */
    @GetMapping("/my")
    public Result getMySalaries(@RequestParam Long seekerId) {
        try {
            List<Salary> salaryList = salaryService.getMySalaries(seekerId);
            return Result.success(salaryList);
        } catch (Exception e) {
            return Result.fail("查询个人薪资失败：" + e.getMessage());
        }
    }

    // ------------------------------ 雇主接口 ------------------------------
    /**
     * 雇主查询自己岗位下的薪资记录
     * @param employerId 雇主ID
     * @return 薪资记录列表
     */
    @GetMapping("/employer")
    public Result getJobSalaries(@RequestParam Long employerId) {
        try {
            List<Salary> salaryList = salaryService.getJobSalaries(employerId);
            return Result.success(salaryList);
        } catch (Exception e) {
            return Result.fail("查询岗位薪资失败：" + e.getMessage());
        }
    }

    /**
     * 雇主标记薪资为已发放
     * @param id 薪资ID
     * @return 操作结果
     */
    @PutMapping("/pay")
    public Result paySalary(@RequestParam Long id) {
        try {
            boolean success = salaryService.paySalary(id);
            if (success) {
                return Result.success("薪资已标记为发放成功");
            } else {
                return Result.fail("薪资发放标记失败");
            }
        } catch (Exception e) {
            return Result.fail("发放薪资异常：" + e.getMessage());
        }
    }
}