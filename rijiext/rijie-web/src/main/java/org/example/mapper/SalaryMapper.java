package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Salary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 薪资数据操作Mapper接口（对应salary表）
 */
@Mapper
public interface SalaryMapper {

    /**
     * 新增薪资记录（自动算薪后生成）
     * @param salary 薪资实体
     * @return 受影响行数
     */
    @Insert("INSERT INTO salary (seeker_id, job_id, work_date, salary_amount, pay_status) " +
            "VALUES (#{seekerId}, #{jobId}, #{workDate}, #{salaryAmount}, #{payStatus})")
    int insertSalary(Salary salary);

    /**
     * 校验是否已生成某求职者某岗位某日期的薪资（避免重复算薪）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @param workDate 工作日期
     * @return 薪资记录数（>0表示已生成）
     */
    @Select("SELECT COUNT(*) FROM salary WHERE seeker_id = #{seekerId} AND job_id = #{jobId} AND work_date = #{workDate}")
    int countSalary(@Param("seekerId") Long seekerId,
                    @Param("jobId") Long jobId,
                    @Param("workDate") LocalDate workDate);

    /**
     * 根据求职者ID查询其所有薪资记录
     * 🔥 修改点：LEFT JOIN 联表查询岗位名称 jobName
     * @param seekerId 求职者ID
     * @return 薪资记录列表
     */
    @Select("SELECT s.*, j.job_name AS jobName FROM salary s " +
            "LEFT JOIN part_time_job j ON s.job_id = j.id " +
            "WHERE s.seeker_id = #{seekerId} ORDER BY s.work_date DESC")
    List<Salary> selectSalariesBySeekerId(Long seekerId);

    /**
     * 根据雇主ID查询其岗位下的所有薪资记录（关联岗位表）
     * 🔥 修改点：新增查询岗位名称 jobName
     * @param employerId 雇主ID
     * @return 薪资记录列表
     */
    @Select("SELECT s.*, j.job_name AS jobName FROM salary s " +
            "LEFT JOIN part_time_job j ON s.job_id = j.id " +
            "WHERE j.employer_id = #{employerId} " +
            "ORDER BY s.work_date DESC")
    List<Salary> selectSalariesByEmployerId(Long employerId);

    /**
     * 更新薪资发放状态（雇主标记为已发放）
     * @param id 薪资ID
     * @param payStatus 目标状态：1-已发放
     * @param payTime 发放时间
     * @return 受影响行数
     */
    @Update("UPDATE salary SET pay_status = #{payStatus}, pay_time = #{payTime} WHERE id = #{id}")
    int updatePayStatus(@Param("id") Long id,
                        @Param("payStatus") Integer payStatus,
                        @Param("payTime") LocalDateTime payTime);

    /**
     * 根据薪资ID查询详情
     * @param id 薪资ID
     * @return 薪资实体
     */
    @Select("SELECT * FROM salary WHERE id = #{id}")
    Salary selectSalaryById(Long id);
}