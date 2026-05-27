package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class PieDataDTO {
    /**
     * 分类名称（如["餐饮", "快递", "零售"]）
     */
    private List<String> names;

    /**
     * 分类数值（如[50, 30, 20]）
     */
    private List<Number> values;
}

