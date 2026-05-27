package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChartDataDTO {
    /**
     * x轴标签列表（如["2025-12-01", "2025-12-02"]）
     */
    private List<String> xAxis;

    /**
     * y轴数值列表（如[10, 20, 30]）
     */
    private List<Number> yAxis;
}