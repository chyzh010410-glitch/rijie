package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价实体类
 */
@Data
public class Rating {
    private Long id;
    private Long raterId;       // 评价人ID
    private Long ratedId;       // 被评价人ID
    private Long jobId;         // 岗位ID
    private Integer ratingType; // 评价类型: 1-雇主评求职者, 2-求职者评雇主
    private Integer score;      // 评分(1-5星)
    private String content;     // 评价内容
    private String tags;        // 评价标签(逗号分隔)
    private Integer isAnonymous;// 是否匿名: 0-否, 1-是
    private LocalDateTime createTime;
}