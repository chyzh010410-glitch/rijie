package org.example.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 岗位筛选条件DTO
 */
@Data
public class JobFilterDTO {
    /**
     * 岗位类型（如餐饮、快递）
     */
    private String jobType;

    /**
     * 最低薪资
     */
    private BigDecimal minSalary;

    /**
     * 最高薪资
     */
    private BigDecimal maxSalary;

    /**
     * 工作地点（模糊匹配，如“北京朝阳”）
     */
    private String workAddress;

    /**
     * 关键词（匹配岗位名称/描述）
     */
    private String keyword;

    /**
     * 页码（分页用）
     */
    private Integer pageNum = 1;

    /**
     * 每页条数（分页用）
     */
    private Integer pageSize = 10;
}