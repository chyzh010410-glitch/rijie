package org.example.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 评价提交DTO
 */
@Data
public class RatingDTO {
    @NotNull(message = "被评价人ID不能为空")
    private Long ratedId;

    @NotNull(message = "岗位ID不能为空")
    private Long jobId;

    @NotNull(message = "评价类型不能为空")
    @Min(value = 1, message = "评价类型错误")
    @Max(value = 2, message = "评价类型错误")
    private Integer ratingType; // 1-雇主评求职者, 2-求职者评雇主

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分必须在1-5之间")
    @Max(value = 5, message = "评分必须在1-5之间")
    private Integer score;

    private String content;

    private List<String> tags; // 标签列表

    private Boolean isAnonymous = false;
}