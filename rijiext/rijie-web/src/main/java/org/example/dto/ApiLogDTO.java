package org.example.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * API日志DTO
 */
@Data
public class ApiLogDTO {
    /**
     * 请求ID（唯一标识一次请求）
     */
    private String requestId;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求方式（GET/POST/PUT/DELETE）
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 操作用户ID（若未登录则为匿名）
     */
    private Long userId;

    /**
     * 接口执行时间（毫秒）
     */
    private Long executeTime;

    /**
     * 响应结果
     */
    private String responseData;

    /**
     * 异常信息（若有）
     */
    private String exceptionMsg;

    /**
     * 请求时间
     */
    private LocalDateTime requestTime;
}