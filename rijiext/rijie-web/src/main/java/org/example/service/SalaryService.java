package org.example.service;

import org.example.pojo.Salary;

import java.time.LocalDate;
import java.util.List;

/**
 * 薪资结算与发放业务层接口
 */
public interface SalaryService {

    /**
     * 自动算薪（根据考勤记录生成薪资，支持单条/批量日期）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @param workDate 工作日期（单日期）
     * @return 操作结果：true=成功，false=失败
     */
    boolean calculateSalary(Long seekerId, Long jobId, LocalDate workDate);

    /**
     * 求职者查询自己的所有薪资记录
     * @param seekerId 求职者ID
     * @return 薪资记录列表
     */
    List<Salary> getMySalaries(Long seekerId);

    /**
     * 雇主查询自己岗位下的所有薪资记录
     * @param employerId 雇主ID
     * @return 薪资记录列表
     */
    List<Salary> getJobSalaries(Long employerId);

    /**
     * 雇主标记薪资为已发放
     * @param id 薪资ID
     * @return 操作结果：true=成功，false=失败
     */
    boolean paySalary(Long id);
}