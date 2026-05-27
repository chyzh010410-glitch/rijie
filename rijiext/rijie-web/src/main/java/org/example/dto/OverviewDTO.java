package org.example.dto;

import lombok.Data;

@Data
public class OverviewDTO {
    /**
     * 总用户数
     */
    private Long totalUser;

    /**
     * 总岗位数
     */
    private Long totalJob;

    /**
     * 总薪资发放金额
     */
    private Double totalSalary;

    /**
     * 今日新增用户数
     */
    private Long todayNewUser;
}