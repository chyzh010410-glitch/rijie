package org.example.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 薪资实体类（对应salary表）
 */
@Data
public class Salary {
    /**
     * 薪资ID（自增）
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
     * 工作日期（对应考勤日期）
     */
    private LocalDate workDate;

    /**
     * 薪资金额
     */
    private BigDecimal salaryAmount;

    /**
     * 发放状态：0-待发放，1-已发放
     */
    private Integer payStatus;

    /**
     * 发放时间
     */
    private LocalDateTime payTime;

    private String jobName;

    // 🔥 必须生成 getter 和 setter（MyBatis 靠这个赋值）
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}