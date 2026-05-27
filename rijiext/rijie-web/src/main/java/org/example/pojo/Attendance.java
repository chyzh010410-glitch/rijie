package org.example.pojo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤实体类（对应attendance表）
 */
@Data
public class Attendance {
    /**
     * 考勤ID（自增）
     */
    private Long id;

    /**
     * 求职者用户ID
     */
    private Long seekerId;

    /**
     * 岗位ID
     */
    private Long jobId;

    /**
     * 工作日期
     */
    private LocalDate workDate;

    /**
     * 签到时间
     */
    private LocalDateTime signInTime;

    /**
     * 签退时间
     */
    private LocalDateTime signOutTime;

    /**
     * 考勤状态：0-正常，1-迟到，2-早退，3-旷工
     */
    private Integer attendanceStatus;
}