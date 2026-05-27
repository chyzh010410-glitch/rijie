package org.example.service;

import java.math.BigDecimal;

public interface DashboardService {
    // 今日岗位数
    int getTodayJobCount();
    // 待审核岗位数
    int getPendingAuditJobCount();
    // 今日考勤人数
    int getTodayAttendanceCount();
    // 待发放薪资总额
    BigDecimal getPendingSalaryAmount();
}