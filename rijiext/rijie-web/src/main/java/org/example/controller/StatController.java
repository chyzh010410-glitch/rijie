package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.ChartDataDTO;
import org.example.dto.OverviewDTO;
import org.example.dto.PieDataDTO;
import org.example.pojo.Result;
import org.example.service.StatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 数据统计与可视化控制层
 */
@RestController
@RequestMapping("/api/stat")
public class StatController {

    @Resource
    private StatService statService;

    // ------------------------------ 管理员平台统计接口 ------------------------------
    @GetMapping("/admin/overview")
    public Result getAdminOverview() {
        try {
            return Result.success(statService.getAdminOverview());
        } catch (Exception e) {
            return Result.fail("获取平台概览失败：" + e.getMessage());
        }
    }

    @GetMapping("/admin/user/growth/7d")
    public Result getUserGrowth7d() {
        try {
            return Result.success(statService.getUserGrowth7d());
        } catch (Exception e) {
            return Result.fail("获取用户增长统计失败：" + e.getMessage());
        }
    }

    @GetMapping("/admin/job/publish/12m")
    public Result getJobPublish12m() {
        try {
            return Result.success(statService.getJobPublish12m());
        } catch (Exception e) {
            return Result.fail("获取岗位发布统计失败：" + e.getMessage());
        }
    }

    @GetMapping("/admin/job/type/distribution")
    public Result getJobTypeDistribution() {
        try {
            return Result.success(statService.getJobTypeDistribution());
        } catch (Exception e) {
            return Result.fail("获取岗位类型分布失败：" + e.getMessage());
        }
    }

    @GetMapping("/admin/user/role/distribution")
    public Result getUserRoleDistribution() {
        try {
            return Result.success(statService.getUserRoleDistribution());
        } catch (Exception e) {
            return Result.fail("获取用户角色分布失败：" + e.getMessage());
        }
    }

    // ------------------------------ 雇主个人统计接口 ------------------------------
    @GetMapping("/employer/overview/{employerId}")
    public Result getEmployerOverview(@PathVariable Long employerId) {
        try {
            return Result.success(statService.getEmployerOverview(employerId));
        } catch (Exception e) {
            return Result.fail("获取雇主概览失败：" + e.getMessage());
        }
    }

    @GetMapping("/employer/attendance/rate/7d/{employerId}")
    public Result getAttendanceRate7d(@PathVariable Long employerId) {
        try {
            return Result.success(statService.getAttendanceRate7d(employerId));
        } catch (Exception e) {
            return Result.fail("获取考勤正常率统计失败：" + e.getMessage());
        }
    }

    @GetMapping("/employer/recruit/job/{employerId}")
    public Result getRecruitByJob(@PathVariable Long employerId) {
        try {
            return Result.success(statService.getRecruitByJob(employerId));
        } catch (Exception e) {
            return Result.fail("获取招聘人数统计失败：" + e.getMessage());
        }
    }
}