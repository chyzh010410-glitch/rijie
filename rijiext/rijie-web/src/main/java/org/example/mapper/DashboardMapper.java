package org.example.mapper;

import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

public interface DashboardMapper {
    // 1. 今日岗位数：统计今日创建的岗位
    @Select("SELECT COUNT(*) FROM part_time_job WHERE DATE(create_time) = CURDATE()")
    int countTodayJobs();

    // 2. 待审核岗位数：统计状态为0（未审核）的岗位
    @Select("SELECT COUNT(*) FROM part_time_job WHERE publish_status = 0")
    int countPendingAuditJobs();

    // 3. 今日考勤人数：统计今日打卡的求职者（去重）
    @Select("SELECT COUNT(DISTINCT seeker_id) FROM attendance WHERE DATE(sign_in_time) = CURDATE()")
    int countTodayAttendance();

    // 4. 待发放薪资总额：统计未发放的薪资总和（如果要统计数量，改成 COUNT(*)）
    @Select("SELECT SUM(salary_amount) FROM salary WHERE pay_status = 0")
    BigDecimal sumPendingSalary();
}
