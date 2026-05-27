package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.dto.ChartDataDTO;
import org.example.dto.OverviewDTO;
import org.example.dto.PieDataDTO;
import org.example.mapper.AdminStatMapper;
import org.example.mapper.EmployerStatMapper;
import org.example.service.StatService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务实现类
 */
@Service
public class StatServiceImpl implements StatService {

    @Resource
    private AdminStatMapper adminStatMapper;

    @Resource
    private EmployerStatMapper employerStatMapper;

    // ------------------------------ 通用工具方法：转换数据库结果为可视化DTO ------------------------------
    /**
     * 转换Map列表为ChartDataDTO（柱状图/折线图）
     */
    private ChartDataDTO convertToChartData(Iterable<Map<String, Object>> dataList, String xKey, String yKey) {
        ChartDataDTO dto = new ChartDataDTO();
        List<String> xAxis = new ArrayList<>();
        List<Number> yAxis = new ArrayList<>();

        for (Map<String, Object> map : dataList) {
            xAxis.add(map.get(xKey).toString());
            yAxis.add((Number) map.get(yKey));
        }

        dto.setXAxis(xAxis);
        dto.setYAxis(yAxis);
        return dto;
    }

    /**
     * 转换Map列表为PieDataDTO（饼图）
     */
    private PieDataDTO convertToPieData(Iterable<Map<String, Object>> dataList, String nameKey, String valueKey) {
        PieDataDTO dto = new PieDataDTO();
        List<String> names = new ArrayList<>();
        List<Number> values = new ArrayList<>();

        for (Map<String, Object> map : dataList) {
            names.add(map.get(nameKey).toString());
            values.add((Number) map.get(valueKey));
        }

        dto.setNames(names);
        dto.setValues(values);
        return dto;
    }

    // ------------------------------ 管理员平台统计实现 ------------------------------
    @Override
    public OverviewDTO getAdminOverview() {
        LocalDate today = LocalDate.now();
        OverviewDTO dto = new OverviewDTO();
        dto.setTotalUser(adminStatMapper.countTotalUser());
        dto.setTotalJob(adminStatMapper.countTotalJob());
        dto.setTotalSalary(adminStatMapper.sumTotalSalary() == null ? 0 : adminStatMapper.sumTotalSalary());
        dto.setTodayNewUser(adminStatMapper.countTodayNewUser(today));
        return dto;
    }

    @Override
    public ChartDataDTO getUserGrowth7d() {
        LocalDate today = LocalDate.now();
        Iterable<Map<String, Object>> dataList = adminStatMapper.countUserGrowth7d(today);
        return convertToChartData(dataList, "date", "num");
    }

    @Override
    public ChartDataDTO getJobPublish12m() {
        Iterable<Map<String, Object>> dataList = adminStatMapper.countJobPublish12m();
        return convertToChartData(dataList, "month", "num");
    }

    @Override
    public PieDataDTO getJobTypeDistribution() {
        Iterable<Map<String, Object>> dataList = adminStatMapper.statJobTypeDistribution();
        return convertToPieData(dataList, "name", "value");
    }

    @Override
    public PieDataDTO getUserRoleDistribution() {
        Iterable<Map<String, Object>> dataList = adminStatMapper.statUserRoleDistribution();
        return convertToPieData(dataList, "name", "value");
    }

    // ------------------------------ 雇主个人统计实现 ------------------------------
    @Override
    public OverviewDTO getEmployerOverview(Long employerId) {
        LocalDate today = LocalDate.now();
        OverviewDTO dto = new OverviewDTO();
        dto.setTotalJob(employerStatMapper.countMyJob(employerId));
        dto.setTotalUser(employerStatMapper.countMyApplication(employerId)); // 用totalUser字段存申请数
        dto.setTotalSalary(employerStatMapper.sumMySalaryThisMonth(employerId, today) == null ? 0 : employerStatMapper.sumMySalaryThisMonth(employerId, today));
        dto.setTodayNewUser(0L); // 雇主概览暂不统计今日新增
        return dto;
    }

    @Override
    public ChartDataDTO getAttendanceRate7d(Long employerId) {
        LocalDate today = LocalDate.now();
        Iterable<Map<String, Object>> dataList = employerStatMapper.statAttendanceRate7d(employerId, today);
        return convertToChartData(dataList, "date", "rate");
    }

    @Override
    public PieDataDTO getRecruitByJob(Long employerId) {
        Iterable<Map<String, Object>> dataList = employerStatMapper.statRecruitByJob(employerId);
        return convertToPieData(dataList, "name", "value");
    }
}