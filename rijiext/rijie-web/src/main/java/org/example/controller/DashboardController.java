package org.example.controller;

import org.example.pojo.Result;
import org.example.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard") // 接口前缀：/api/dashboard
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // 获取仪表盘所有统计数据
    @GetMapping("/stats")
    public Result getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayJobCount", dashboardService.getTodayJobCount());       // 今日岗位数
        stats.put("pendingAuditJobCount", dashboardService.getPendingAuditJobCount()); // 待审核岗位
        stats.put("todayAttendanceCount", dashboardService.getTodayAttendanceCount()); // 今日考勤人数
        stats.put("pendingSalaryAmount", dashboardService.getPendingSalaryAmount()); // 待发放薪资
        return Result.success(stats);
    }
}