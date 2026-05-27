package org.example.service;

import org.example.pojo.Attendance;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤管理业务层接口
 */
public interface AttendanceService {

    /**
     * 求职者签到（自动判定是否迟到）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @param workDate 工作日期
     * @return 操作结果：true=成功，false=失败（如重复签到）
     */
    boolean signIn(Long seekerId, Long jobId, LocalDate workDate);

    /**
     * 求职者签退（自动判定是否早退）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @param workDate 工作日期
     * @return 操作结果：true=成功，false=失败（如未签到）
     */
    boolean signOut(Long seekerId, Long jobId, LocalDate workDate);

    /**
     * 求职者查询自己的所有考勤记录
     * @param seekerId 求职者ID
     * @return 考勤记录列表
     */
    List<Attendance> getMyAttendances(Long seekerId);

    /**
     * 雇主查询自己岗位下的所有考勤记录
     * @param employerId 雇主ID
     * @return 考勤记录列表
     */
    List<Attendance> getJobAttendances(Long employerId);

    /**
     * 雇主手动更新考勤状态（如迟到改正常、旷工改迟到）
     * @param id 考勤ID
     * @param attendanceStatus 目标状态
     * @return 操作结果：true=成功，false=失败
     */
    boolean updateAttendanceStatus(Long id, Integer attendanceStatus);
}