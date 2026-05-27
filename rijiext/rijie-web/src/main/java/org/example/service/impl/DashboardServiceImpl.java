package org.example.service.impl;

import org.example.mapper.DashboardMapper;
import org.example.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public int getTodayJobCount() {
        return dashboardMapper.countTodayJobs();
    }

    @Override
    public int getPendingAuditJobCount() {
        return dashboardMapper.countPendingAuditJobs();
    }

    @Override
    public int getTodayAttendanceCount() {
        return dashboardMapper.countTodayAttendance();
    }

    @Override
    public BigDecimal getPendingSalaryAmount() {
        BigDecimal amount = dashboardMapper.sumPendingSalary();
        // 空值处理：如果没有待发放薪资，返回0
        return amount == null ? BigDecimal.ZERO : amount;
    }
}