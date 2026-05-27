package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.mapper.AttendanceMapper;
import org.example.mapper.JobMapper;
import org.example.pojo.Attendance;
import org.example.pojo.Job;
import org.example.service.AttendanceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 考勤管理业务层实现类
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Resource
    private AttendanceMapper attendanceMapper;

    @Resource
    private JobMapper jobMapper;

    // 迟到判定阈值：15分钟（签到时间晚于岗位开始时间15分钟内不算迟到，超过则算）
    private static final long LATE_THRESHOLD = 15;
    // 早退判定阈值：15分钟（签退时间早于岗位结束时间15分钟内不算早退，超过则算）
    private static final long EARLY_THRESHOLD = 15;

    /**
     * 求职者签到：校验重复签到→查询岗位时间→判定是否迟到→新增考勤记录
     */
    @Override
    public boolean signIn(Long seekerId, Long jobId, LocalDate workDate) {
        // 1. 入参校验
        if (seekerId == null || seekerId <= 0 || jobId == null || jobId <= 0 || workDate == null) {
            return false;
        }

        // 2. 校验当天是否已签到
        int count = attendanceMapper.countSignIn(seekerId, jobId, workDate);
        if (count > 0) {
            return false; // 重复签到
        }

        // 3. 查询岗位的工作开始时间
        Job job = jobMapper.selectJobTimeById(jobId);
        if (job == null) {
            return false; // 岗位不存在
        }
        LocalTime workStartTime = job.getWorkStartTime();

        // 4. 获取当前签到时间，判定是否迟到
        LocalDateTime signInTime = LocalDateTime.now();
        Integer status = 0; // 默认正常
        // 拼接岗位开始时间的完整时间（工作日期+开始时间）
        LocalDateTime workStartDateTime = LocalDateTime.of(workDate, workStartTime);
        // 计算签到时间与岗位开始时间的差值（分钟）
        long minutesLate = ChronoUnit.MINUTES.between(workStartDateTime, signInTime);
        if (minutesLate > LATE_THRESHOLD) {
            status = 1; // 迟到
        }

        // 5. 构建考勤实体，插入数据库
        Attendance attendance = new Attendance();
        attendance.setSeekerId(seekerId);
        attendance.setJobId(jobId);
        attendance.setWorkDate(workDate);
        attendance.setSignInTime(signInTime);
        attendance.setAttendanceStatus(status);

        int rows = attendanceMapper.insertAttendance(attendance);
        return rows > 0;
    }

    /**
     * 求职者签退：查询考勤记录→查询岗位结束时间→判定是否早退→更新签退时间和状态
     */
    @Override
    public boolean signOut(Long seekerId, Long jobId, LocalDate workDate) {
        // 1. 入参校验
        if (seekerId == null || seekerId <= 0 || jobId == null || jobId <= 0 || workDate == null) {
            return false;
        }

        // 2. 查询当天的考勤记录（必须已签到）
        Attendance attendance = attendanceMapper.selectAttendanceBySeekerAndJob(seekerId, jobId, workDate);
        if (attendance == null || attendance.getSignInTime() == null) {
            return false; // 未签到，无法签退
        }
        if (attendance.getSignOutTime() != null) {
            return false; // 已签退
        }

        // 3. 查询岗位的工作结束时间
        Job job = jobMapper.selectJobTimeById(jobId);
        if (job == null) {
            return false; // 岗位不存在
        }
        LocalTime workEndTime = job.getWorkEndTime();

        // 4. 获取当前签退时间，判定是否早退
        LocalDateTime signOutTime = LocalDateTime.now();
        Integer status = attendance.getAttendanceStatus(); // 继承签到的状态（正常/迟到）
        // 拼接岗位结束时间的完整时间（工作日期+结束时间）
        LocalDateTime workEndDateTime = LocalDateTime.of(workDate, workEndTime);
        // 计算签退时间与岗位结束时间的差值（分钟）
        long minutesEarly = ChronoUnit.MINUTES.between(signOutTime, workEndDateTime);
        if (minutesEarly > EARLY_THRESHOLD) {
            status = 2; // 早退（若已迟到则状态为早退，可根据需求调整为“迟到+早退”）
        }

        // 5. 更新签退时间和考勤状态
        int rows = attendanceMapper.updateSignOut(attendance.getId(), signOutTime, status);
        return rows > 0;
    }

    /**
     * 求职者查询个人考勤记录
     */
    @Override
    public List<Attendance> getMyAttendances(Long seekerId) {
        if (seekerId == null || seekerId <= 0) {
            return null;
        }
        return attendanceMapper.selectAttendancesBySeekerId(seekerId);
    }

    /**
     * 雇主查询旗下岗位的考勤记录
     */
    @Override
    public List<Attendance> getJobAttendances(Long employerId) {
        if (employerId == null || employerId <= 0) {
            return null;
        }
        return attendanceMapper.selectAttendancesByEmployerId(employerId);
    }

    /**
     * 雇主手动更新考勤状态
     */
    @Override
    public boolean updateAttendanceStatus(Long id, Integer attendanceStatus) {
        // 1. 入参校验：状态只能是0-正常、1-迟到、2-早退、3-旷工
        if (id == null || id <= 0 || attendanceStatus == null || attendanceStatus < 0 || attendanceStatus > 3) {
            return false;
        }
        // 2. 更新状态
        int rows = attendanceMapper.updateAttendanceStatus(id, attendanceStatus);
        return rows > 0;
    }
}