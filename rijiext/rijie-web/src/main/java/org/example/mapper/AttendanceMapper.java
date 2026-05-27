package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考勤数据操作Mapper接口（对应attendance表）
 */
@Mapper
public interface AttendanceMapper {

    /**
     * 新增考勤记录（求职者签到）
     * @param attendance 考勤实体
     * @return 受影响行数
     */
    @Insert("INSERT INTO attendance (seeker_id, job_id, work_date, sign_in_time, attendance_status) " +
            "VALUES (#{seekerId}, #{jobId}, #{workDate}, #{signInTime}, #{attendanceStatus})")
    int insertAttendance(Attendance attendance);

    /**
     * 更新签退时间（求职者签退）
     * @param id 考勤ID
     * @param signOutTime 签退时间
     * @param attendanceStatus 更新后的考勤状态
     * @return 受影响行数
     */
    @Update("UPDATE attendance SET sign_out_time = #{signOutTime}, attendance_status = #{attendanceStatus} WHERE id = #{id}")
    int updateSignOut(@Param("id") Long id,
                      @Param("signOutTime") LocalDateTime signOutTime,
                      @Param("attendanceStatus") Integer attendanceStatus);

    /**
     * 校验求职者当天是否已签到（避免重复签到）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @param workDate 工作日期
     * @return 考勤记录数（>0表示已签到）
     */
    @Select("SELECT COUNT(*) FROM attendance WHERE seeker_id = #{seekerId} AND job_id = #{jobId} AND work_date = #{workDate}")
    int countSignIn(@Param("seekerId") Long seekerId,
                    @Param("jobId") Long jobId,
                    @Param("workDate") LocalDate workDate);

    /**
     * 根据求职者ID+岗位ID+日期查询考勤记录（用于签到/签退）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @param workDate 工作日期
     * @return 考勤实体
     */
    @Select("SELECT * FROM attendance WHERE seeker_id = #{seekerId} AND job_id = #{jobId} AND work_date = #{workDate}")
    Attendance selectAttendanceBySeekerAndJob(@Param("seekerId") Long seekerId,
                                              @Param("jobId") Long jobId,
                                              @Param("workDate") LocalDate workDate);

    /**
     * 根据求职者ID查询其所有考勤记录
     * @param seekerId 求职者ID
     * @return 考勤记录列表
     */
    @Select("SELECT * FROM attendance WHERE seeker_id = #{seekerId} ORDER BY work_date DESC")
    List<Attendance> selectAttendancesBySeekerId(Long seekerId);

    /**
     * 根据雇主ID查询其岗位下的所有考勤记录（关联岗位表）
     * @param employerId 雇主ID
     * @return 考勤记录列表
     */
    @Select("SELECT a.* FROM attendance a " +
            "LEFT JOIN part_time_job j ON a.job_id = j.id " +
            "WHERE j.employer_id = #{employerId} " +
            "ORDER BY a.work_date DESC")
    List<Attendance> selectAttendancesByEmployerId(Long employerId);

    /**
     * 雇主手动更新考勤状态（如迟到改正常）
     * @param id 考勤ID
     * @param attendanceStatus 目标状态
     * @return 受影响行数
     */
    @Update("UPDATE attendance SET attendance_status = #{attendanceStatus} WHERE id = #{id}")
    int updateAttendanceStatus(@Param("id") Long id,
                               @Param("attendanceStatus") Integer attendanceStatus);

    /**
     * 根据考勤ID查询详情
     * @param id 考勤ID
     * @return 考勤实体
     */
    @Select("SELECT * FROM attendance WHERE id = #{id}")
    Attendance selectAttendanceById(Long id);

    // 在AttendanceMapper接口中添加
    /**
     * 查询某求职者某岗位的考勤记录（按日期筛选）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 考勤记录列表
     */
    @Select("SELECT * FROM attendance WHERE seeker_id = #{seekerId} AND job_id = #{jobId} AND work_date BETWEEN #{startDate} AND #{endDate}")
    List<Attendance> selectAttendancesBySeekerAndJobRange(@Param("seekerId") Long seekerId,
                                                          @Param("jobId") Long jobId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);
}