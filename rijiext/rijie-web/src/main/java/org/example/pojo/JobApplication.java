package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 岗位申请实体类（对应job_application表）
 */
@Data
public class JobApplication {
    /**
     * 申请ID（自增）
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
     * 岗位名称
     */
    private String jobName;

    /**
     * 申请状态：0-待审核，1-已通过，2-已拒绝，3-已入职
     */
    private Integer applyStatus;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 拒绝原因（仅当状态为2时非空）
     */
    private String rejectReason;
}