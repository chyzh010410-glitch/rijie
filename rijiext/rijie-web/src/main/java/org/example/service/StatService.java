package org.example.service;

import org.example.dto.ChartDataDTO;
import org.example.dto.OverviewDTO;
import org.example.dto.PieDataDTO;

/**
 * 数据统计服务接口
 */
public interface StatService {

    // ------------------------------ 管理员平台统计 ------------------------------
    /**
     * 获取平台概览统计
     */
    OverviewDTO getAdminOverview();

    /**
     * 获取近7天用户增长统计（柱状图）
     */
    ChartDataDTO getUserGrowth7d();

    /**
     * 获取近12个月岗位发布统计（折线图）
     */
    ChartDataDTO getJobPublish12m();

    /**
     * 获取岗位类型分布统计（饼图）
     */
    PieDataDTO getJobTypeDistribution();

    /**
     * 获取用户角色分布统计（饼图）
     */
    PieDataDTO getUserRoleDistribution();

    // ------------------------------ 雇主个人统计 ------------------------------
    /**
     * 获取雇主个人概览统计
     */
    OverviewDTO getEmployerOverview(Long employerId);

    /**
     * 获取雇主近7天考勤正常率统计（折线图）
     */
    ChartDataDTO getAttendanceRate7d(Long employerId);

    /**
     * 获取雇主旗下岗位招聘人数统计（饼图）
     */
    PieDataDTO getRecruitByJob(Long employerId);
}