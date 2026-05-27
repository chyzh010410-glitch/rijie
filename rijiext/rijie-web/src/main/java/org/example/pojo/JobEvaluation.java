package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobEvaluation {
    private Long id;
    private Long jobId;      // 岗位ID
    private Long seekerId;   // 求职者ID
    private Long employerId; // 雇主ID
    private Integer score;   // 1-5星
    private String content;  // 评价内容
    private LocalDateTime createTime;
    private Integer status;  // 1正常 0禁用
}