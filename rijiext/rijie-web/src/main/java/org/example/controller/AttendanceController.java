package org.example.controller;

import jakarta.annotation.Resource;
import org.example.pojo.Attendance;
import org.example.pojo.Result;
import org.example.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤管理控制层
 * 处理签到、签退、考勤查询等请求
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Resource
    private AttendanceService attendanceService;

    // ------------------------------ 求职者接口 ------------------------------
    /**
     * 求职者签到（默认使用当天日期，也可指定日期）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @param workDate 工作日期（可选，默认当天）
     * @return 操作结果
     */
    @PostMapping("/sign/in")
    public Result signIn(@RequestParam Long seekerId,
                         @RequestParam Long jobId,
                         @RequestParam(required = false) LocalDate workDate) {
        try {
            // 若无指定日期，使用当天
            LocalDate date = workDate == null ? LocalDate.now() : workDate;
            boolean success = attendanceService.signIn(seekerId, jobId, date);
            if (success) {
                return Result.success("签到成功");
            } else {
                return Result.fail("签到失败（可能已重复签到或岗位不存在）");
            }
        } catch (Exception e) {
            return Result.fail("签到异常：" + e.getMessage());
        }
    }

    /**
     * 求职者签退（默认使用当天日期）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @param workDate 工作日期（可选，默认当天）
     * @return 操作结果
     */
    @PostMapping("/sign/out")
    public Result signOut(@RequestParam Long seekerId,
                          @RequestParam Long jobId,
                          @RequestParam(required = false) LocalDate workDate) {
        try {
            LocalDate date = workDate == null ? LocalDate.now() : workDate;
            boolean success = attendanceService.signOut(seekerId, jobId, date);
            if (success) {
                return Result.success("签退成功");
            } else {
                return Result.fail("签退失败（可能未签到或已签退）");
            }
        } catch (Exception e) {
            return Result.fail("签退异常：" + e.getMessage());
        }
    }

    /**
     * 求职者查询自己的考勤记录
     * @param seekerId 求职者ID
     * @return 考勤记录列表
     */
    @GetMapping("/my")
    public Result getMyAttendances(@RequestParam Long seekerId) {
        try {
            List<Attendance> attendanceList = attendanceService.getMyAttendances(seekerId);
            return Result.success(attendanceList);
        } catch (Exception e) {
            return Result.fail("查询个人考勤失败：" + e.getMessage());
        }
    }

    // ------------------------------ 雇主接口 ------------------------------
    /**
     * 雇主查询自己岗位下的考勤记录
     * @param employerId 雇主ID
     * @return 考勤记录列表
     */
    @GetMapping("/employer")
    public Result getJobAttendances(@RequestParam Long employerId) {
        try {
            List<Attendance> attendanceList = attendanceService.getJobAttendances(employerId);
            return Result.success(attendanceList);
        } catch (Exception e) {
            return Result.fail("查询岗位考勤失败：" + e.getMessage());
        }
    }

    /**
     * 雇主手动更新考勤状态
     * @param id 考勤ID
     * @param attendanceStatus 目标状态：0-正常，1-迟到，2-早退，3-旷工
     * @return 操作结果
     */
    @PutMapping("/status/update")
    public Result updateAttendanceStatus(@RequestParam Long id,
                                         @RequestParam Integer attendanceStatus) {
        try {
            boolean success = attendanceService.updateAttendanceStatus(id, attendanceStatus);
            if (success) {
                String statusMsg = switch (attendanceStatus) {
                    case 0 -> "正常";
                    case 1 -> "迟到";
                    case 2 -> "早退";
                    case 3 -> "旷工";
                    default -> "";
                };
                return Result.success("考勤状态已更新为" + statusMsg);
            } else {
                return Result.fail("更新考勤状态失败");
            }
        } catch (Exception e) {
            return Result.fail("更新考勤状态异常：" + e.getMessage());
        }
    }
}