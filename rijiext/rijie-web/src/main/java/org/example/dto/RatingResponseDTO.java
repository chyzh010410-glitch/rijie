package org.example.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价响应DTO
 */
@Data
public class RatingResponseDTO {
    private Long id;
    private Long raterId;           // 评价人ID
    private String raterName;       // 评价人用户名
    private String raterRealName;   // 评价人真实姓名
    private Long ratedId;           // 被评价人ID
    private String ratedName;       // 被评价人用户名
    private String ratedRealName;   // 被评价人真实姓名
    private Long jobId;
    private String jobName;
    private Integer ratingType;     // 评价类型: 1-雇主评求职者, 2-求职者评雇主
    private Integer score;          // 评分(1-5星)
    private String content;         // 评价内容
    private String tags;            // 评价标签
    private Boolean isAnonymous;    // 是否匿名
    private LocalDateTime createTime;

    // 用户信誉分信息（可选）
    private Double ratedReputationScore;
    private Integer ratedTotalRatings;
}