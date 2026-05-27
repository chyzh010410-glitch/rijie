package org.example.dto;

import lombok.Data;

import java.util.List;
@Data
public class PageResult {
    // 当前页的数据列表（比如未审核岗位列表、筛选后的岗位列表）
    private List<?> list;
    // 总条数（前端分页组件需要这个值来计算页码）
    private Long total;

    // 快捷构造器：创建分页结果时直接传列表+总条数
    public PageResult(List<?> list, Long total) {
        this.list = list;
        this.total = total;
    }
}
