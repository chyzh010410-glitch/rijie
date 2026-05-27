package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.AttendanceMapper;
import org.example.mapper.JobMapper;
import org.example.mapper.SalaryMapper;
import org.example.pojo.Attendance;
import org.example.pojo.Salary;
import org.example.service.SalaryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j // 新增日志注解，方便调试
public class SalaryServiceImpl implements SalaryService {

    @Resource
    private SalaryMapper salaryMapper;

    @Resource
    private AttendanceMapper attendanceMapper;

    @Resource
    private JobMapper jobMapper;

    // 迟到/早退扣薪金额
    private static final BigDecimal DEDUCT_AMOUNT = new BigDecimal("20");
    // 旷工薪资金额
    private static final BigDecimal ABSENT_SALARY = BigDecimal.ZERO;

    /**
     * 自动算薪：查询考勤→获取日薪→按状态算薪→生成薪资记录
     */
    @Override
    public boolean calculateSalary(Long seekerId, Long jobId, LocalDate workDate) {
        // 1. 入参校验
        if (seekerId == null || seekerId <= 0 || jobId == null || jobId <= 0 || workDate == null) {
            log.error("算薪失败：参数异常 seekerId={}, jobId={}, workDate={}", seekerId, jobId, workDate);
            return false;
        }

        // 2. 校验是否已生成该日期的薪资（避免重复算薪）
        int count = salaryMapper.countSalary(seekerId, jobId, workDate);
        if (count > 0) {
            log.info("算薪失败：该记录已存在 seekerId={}, jobId={}, workDate={}", seekerId, jobId, workDate);
            return false;
        }

        // 3. 查询该日期的考勤记录（必须有考勤才能算薪）
        Attendance attendance = attendanceMapper.selectAttendanceBySeekerAndJob(seekerId, jobId, workDate);
        if (attendance == null) {
            log.error("算薪失败：无考勤记录 seekerId={}, jobId={}, workDate={}", seekerId, jobId, workDate);
            return false;
        }

        // 4. 查询岗位的日薪
        BigDecimal dailySalary = jobMapper.selectDailySalaryById(jobId);
        if (dailySalary == null) {
            log.error("算薪失败：未查询到岗位日薪 jobId={}", jobId);
            return false;
        }

        // ===================== 核心：打印考勤状态（排查扣薪关键！） =====================
        log.info("算薪调试：求职者={}, 岗位日薪={}, 考勤状态={}",
                seekerId, dailySalary, attendance.getAttendanceStatus());

        // 5. 根据考勤状态计算实际薪资
        BigDecimal salaryAmount = switch (attendance.getAttendanceStatus()) {
            case 0 -> dailySalary; // 正常：全额
            case 1, 2 -> {
                BigDecimal result = dailySalary.subtract(DEDUCT_AMOUNT);
                // 优化：防止扣完变成负数（保底0元）
                yield result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
            }
            case 3 -> ABSENT_SALARY; // 旷工：无薪
            default -> ABSENT_SALARY;
        };

        log.info("最终计算薪资：{} 元", salaryAmount);

        // 6. 构建薪资实体
        Salary salary = new Salary();
        salary.setSeekerId(seekerId);
        salary.setJobId(jobId);
        salary.setWorkDate(workDate);
        salary.setSalaryAmount(salaryAmount);
        salary.setPayStatus(0);

        // 7. 插入薪资记录
        int rows = salaryMapper.insertSalary(salary);
        return rows > 0;
    }

    /**
     * 求职者查询个人薪资记录
     */
    @Override
    public List<Salary> getMySalaries(Long seekerId) {
        if (seekerId == null || seekerId <= 0) {
            return null;
        }
        return salaryMapper.selectSalariesBySeekerId(seekerId);
    }

    /**
     * 雇主查询旗下岗位的薪资记录
     */
    @Override
    public List<Salary> getJobSalaries(Long employerId) {
        if (employerId == null || employerId <= 0) {
            return null;
        }
        return salaryMapper.selectSalariesByEmployerId(employerId);
    }

    /**
     * 雇主标记薪资为已发放
     */
    @Override
    public boolean paySalary(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        int rows = salaryMapper.updatePayStatus(id, 1, LocalDateTime.now());
        return rows > 0;
    }
}