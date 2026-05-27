package org.example.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 日结兼职岗位实体类（对应part_time_job表）
 */
@Data
public class Job {
    /**
     * 岗位唯一ID（自增）
     */
    private Long id;

    /**
     * 关联雇主用户ID
     */
    private Long employerId;

    /**
     * 岗位名称
     */
    private String jobName;

    /**
     * 岗位类型（如餐饮、快递、促销）
     */
    private String jobType;

    /**
     * 工作地点
     */
    private String workAddress;

    /**
     * 工作开始时间
     */
    private LocalTime workStartTime;

    /**
     * 工作结束时间
     */
    private LocalTime workEndTime;

    /**
     * 日薪（单位：元）
     */
    private BigDecimal dailySalary;

    /**
     * 招聘人数
     */
    private Integer recruitNum;

    /**
     * 岗位描述
     */
    private String jobDesc;

    /**
     * 岗位要求
     */
    private String jobRequire;

    /**
     * 发布状态：0-未审核，1-已审核，2-已结束
     */
    private Integer publishStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}