package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

/**
 * 雇主个人统计Mapper
 */
@Mapper
@Repository
public interface EmployerStatMapper {

    // ------------------------------ 概览统计 ------------------------------
    /**
     * 雇主旗下岗位数
     */
    @Select("SELECT COUNT(*) FROM part_time_job WHERE employer_id = #{employerId}")
    Long countMyJob(Long employerId);

    /**
     * 雇主旗下岗位申请数
     */
    @Select("SELECT COUNT(*) FROM job_application a LEFT JOIN part_time_job j ON a.job_id = j.id WHERE j.employer_id = #{employerId}")
    Long countMyApplication(Long employerId);

    /**
     * 雇主本月薪资支出
     */
    @Select("SELECT SUM(s.salary_amount) FROM salary s LEFT JOIN part_time_job j ON s.job_id = j.id WHERE j.employer_id = #{employerId} AND DATE_FORMAT(s.work_date, '%Y-%m') = DATE_FORMAT(#{today}, '%Y-%m')")
    Double sumMySalaryThisMonth(@Param("employerId") Long employerId, @Param("today") LocalDate today);

    // ------------------------------ 时间维度统计 ------------------------------
    /**
     * 雇主近7天考勤正常率统计（按日期分组）
     */
    @Select("SELECT DATE(a.work_date) AS date, " +
            "SUM(CASE WHEN a.attendance_status = 0 THEN 1 ELSE 0 END) / COUNT(*) * 100 AS rate " +
            "FROM attendance a LEFT JOIN part_time_job j ON a.job_id = j.id " +
            "WHERE j.employer_id = #{employerId} AND a.work_date >= DATE_SUB(#{today}, INTERVAL 6 DAY) " +
            "GROUP BY DATE(a.work_date) ORDER BY date")
    Iterable<Map<String, Object>> statAttendanceRate7d(@Param("employerId") Long employerId, @Param("today") LocalDate today);

    /**
     * 雇主旗下岗位招聘人数统计（按岗位分组）
     */
    @Select("SELECT j.job_name AS name, COUNT(a.seeker_id) AS value " +
            "FROM part_time_job j LEFT JOIN job_application a ON j.id = a.job_id " +
            "WHERE j.employer_id = #{employerId} AND a.apply_status = 3 " +
            "GROUP BY j.id, j.job_name")
    Iterable<Map<String, Object>> statRecruitByJob(Long employerId);
}