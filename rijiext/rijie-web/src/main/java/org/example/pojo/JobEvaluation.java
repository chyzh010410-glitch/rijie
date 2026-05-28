package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobEvaluation {
    private Long id;
    private Long jobId;       // 岗位ID
    private Long seekerId;    // 求职者ID（评价人）
    private Long employerId;  // 雇主ID（被评价人）
    private Integer score;    // 1-5星
    private String content;   // 评价内容
    private String tags;      // 评价标签（逗号分隔）
    private Integer isAnonymous; // 是否匿名: 0-否, 1-是
    private LocalDateTime createTime;
    private Integer status;   // 1正常 0禁用

    // 以下为JOIN查询时填充的额外字段
    private String seekerName;
    private String seekerRealName;
    private String employerName;
    private String employerRealName;
    private String jobName;
    private Double employerReputationScore;
    private Integer employerTotalRatings;
}
