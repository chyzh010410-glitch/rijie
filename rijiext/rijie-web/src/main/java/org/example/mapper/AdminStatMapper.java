package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.dto.ChartDataDTO;
import org.example.dto.PieDataDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

/**
 * 管理员平台统计Mapper
 */
@Mapper
@Repository
public interface AdminStatMapper {

    // ------------------------------ 概览统计 ------------------------------
    /**
     * 统计总用户数
     */
    @Select("SELECT COUNT(*) FROM sys_user")
    Long countTotalUser();

    /**
     * 统计总岗位数（已发布）
     */
    @Select("SELECT COUNT(*) FROM part_time_job WHERE publish_status = 1")
    Long countTotalJob();

    /**
     * 统计总薪资发放金额
     */
    @Select("SELECT SUM(salary_amount) FROM salary WHERE pay_status = 1")
    Double sumTotalSalary();

    /**
     * 统计今日新增用户数
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE DATE(create_time) = #{today}")
    Long countTodayNewUser(LocalDate today);

    // ------------------------------ 时间维度统计 ------------------------------
    /**
     * 近7天用户增长统计（按日期分组）
     */
    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS num FROM sys_user WHERE create_time >= DATE_SUB(#{today}, INTERVAL 6 DAY) GROUP BY DATE(create_time) ORDER BY date")
    Iterable<Map<String, Object>> countUserGrowth7d(LocalDate today);

    /**
     * 近12个月岗位发布统计（按月份分组）
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') AS month, COUNT(*) AS num FROM part_time_job WHERE publish_status = 1 GROUP BY DATE_FORMAT(create_time, '%Y-%m') ORDER BY month DESC LIMIT 12")
    Iterable<Map<String, Object>> countJobPublish12m();

    // ------------------------------ 分类统计 ------------------------------
    /**
     * 岗位类型分布统计（饼图）
     */
    @Select("SELECT job_type AS name, COUNT(*) AS value FROM part_time_job WHERE publish_status = 1 GROUP BY job_type")
    Iterable<Map<String, Object>> statJobTypeDistribution();

    /**
     * 用户角色分布统计（饼图）
     */
    @Select("SELECT CASE role_type WHEN 1 THEN '雇主' WHEN 2 THEN '求职者' ELSE '管理员' END AS name, COUNT(*) AS value FROM sys_user GROUP BY role_type")
    Iterable<Map<String, Object>> statUserRoleDistribution();
}