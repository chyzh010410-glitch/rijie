package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.dto.JobFilterDTO;
import org.example.pojo.Job;

import java.math.BigDecimal;
import java.util.List;

/**
 * 岗位数据操作Mapper接口（对应part_time_job表）
 * 提供岗位的CRUD核心操作
 */
@Mapper // MyBatis注解：标记为Mapper接口，由Spring自动扫描并生成代理对象
public interface JobMapper {

    /**
     * 查询所有【未审核/已审核】的岗位（publish_status=1）
     * @return 未审核/已审核岗位列表
     */
    @Select("SELECT * FROM part_time_job WHERE publish_status IN (0,1) ORDER BY create_time DESC")
    List<Job> selectPublishedJobs();

    /**
     * 根据状态查询岗位（用于管理员获取未审核岗位）
     * @param status 岗位状态（0=未审核）
     * @return 对应状态的岗位列表
     */
    @Select("SELECT * FROM part_time_job WHERE publish_status = #{status} ORDER BY create_time DESC")
    List<Job> getJobsByStatus(Integer status);

    /**
     * 搜索未审核岗位（模糊匹配岗位名称/雇主ID）
     * @param keyword 搜索关键词
     * @return 匹配的未审核岗位列表
     */
    @Select("SELECT * FROM part_time_job WHERE publish_status = 0 " +
            "AND (job_name LIKE CONCAT('%', #{keyword}, '%') OR employer_id = #{keyword}) " +
            "ORDER BY create_time DESC")
    List<Job> searchUnreviewedJobs(@Param("keyword") String keyword);

    // ========== 新增：求职者专属 - 查询已审核岗位 ==========
    /**
     * 求职者端：查询所有已审核通过的岗位（支持岗位类型筛选）
     * @param jobType 岗位类型（可为空，为空则查所有已审核岗位）
     * @return 已审核岗位列表
     */
    @Select("SELECT * FROM part_time_job " +
            "WHERE publish_status = 1 " +  // 核心：只查已审核通过的岗位
            "AND (#{jobType} IS NULL OR job_type = #{jobType}) " +  // 支持类型筛选
            "ORDER BY create_time DESC")  // 按创建时间倒序（最新的岗位在前）
    List<Job> selectApprovedJobsForSeeker(@Param("jobType") String jobType);

    // ========== 求职者专属：模糊查询已审核岗位 ==========
    /**
     * 求职者端：模糊查询已审核通过的岗位（支持岗位名称+岗位类型筛选）
     * @param keyword 岗位名称关键词（模糊匹配）
     * @param jobType 岗位类型（可为空）
     * @return 已审核的匹配岗位列表
     */
    @Select("<script>" +
            "SELECT * FROM part_time_job " +
            "WHERE publish_status = 1 " +  // 核心：只查已审核岗位
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND job_name LIKE CONCAT('%', #{keyword}, '%') " + // 模糊匹配岗位名称
            "</if>" +
            "<if test='jobType != null and jobType != \"\"'>" +
            "AND job_type = #{jobType} " + // 筛选岗位类型
            "</if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<Job> selectApprovedJobsByKeyword(
            @Param("keyword") String keyword,
            @Param("jobType") String jobType
    );

    /**
     * 分页查询雇主的岗位列表（关联雇主ID+发布状态）
     */
    @Select("SELECT * FROM part_time_job WHERE employer_id = #{employerId} AND publish_status IN (0,1) ORDER BY create_time DESC")
    List<Job> getJobsByEmployerId(Long employerId);


    // 新增方法：查询所有状态的岗位（无publish_status条件）
    @Select("SELECT * FROM part_time_job ORDER BY create_time DESC")
    List<Job> selectAllJobs();

    /**
     * 根据岗位ID查询单个岗位
     * @param id 岗位ID
     * @return 岗位对象（无数据则返回null）
     */
    @Select("SELECT * FROM part_time_job WHERE id = #{id}")
    Job selectJobById(Long id);

    /**
     * 新增岗位（由雇主发布）
     * @param job 岗位实体对象（需包含employerId、jobName、jobType等核心字段）
     * @return 受影响的行数（1=新增成功，0=失败）
     */
    @Insert("INSERT INTO part_time_job (" +
            "employer_id, job_name, job_type, work_address, " +
            "work_start_time, work_end_time, daily_salary, recruit_num, " +
            "job_desc, job_require, publish_status, create_time, update_time" +
            ") VALUES (" +
            "#{employerId}, #{jobName}, #{jobType}, #{workAddress}, " +
            "#{workStartTime}, #{workEndTime}, #{dailySalary}, #{recruitNum}, " +
            "#{jobDesc}, #{jobRequire}, #{publishStatus}, NOW(), NOW()" +
            ")")
    int insertJob(Job job);

    /**
     * 更新岗位的发布状态（如：未审核→已审核、已审核→已结束）
     * @param id 岗位ID
     * @param publishStatus 目标状态（0=未审核，1=已审核，2=已结束）
     * @return 受影响的行数（1=更新成功，0=失败）
     */
    @Update("UPDATE part_time_job SET publish_status = #{publishStatus}, update_time = NOW() WHERE id = #{id}")
    int updateJobStatus(@Param("id") Long id, @Param("publishStatus") Integer publishStatus);

    /**
     * 根据ID删除岗位（仅管理员/雇主可操作）
     * @param id 岗位ID
     * @return 受影响的行数（1=删除成功，0=失败）
     */
    @Delete("DELETE FROM part_time_job WHERE id = #{id}")
    int deleteJobById(Long id);

    // 在JobMapper接口中添加以下方法
    /**
     * 根据雇主ID查询其发布的所有岗位
     * @param employerId 雇主用户ID
     * @return 岗位列表
     */
    @Select("SELECT * FROM part_time_job WHERE employer_id = #{employerId} ORDER BY create_time DESC")
    List<Job> selectJobsByEmployerId(Long employerId);

    // 在JobMapper接口中添加
    /**
     * 根据岗位ID查询岗位（仅获取工作时间等核心字段）
     * @param jobId 岗位ID
     * @return 岗位实体
     */
    @Select("SELECT work_start_time, work_end_time FROM part_time_job WHERE id = #{jobId}")
    Job selectJobTimeById(Long jobId);


    @Select("SELECT job_name FROM part_time_job WHERE id = #{jobId}")
    Job selectJobNameById(Long id);


    // 在JobMapper接口中添加
    /**
     * 根据岗位ID查询日薪
     * @param jobId 岗位ID
     * @return 日薪金额
     */
    @Select("SELECT daily_salary FROM part_time_job WHERE id = #{jobId}")
    BigDecimal selectDailySalaryById(Long jobId);

    /**
     * 多条件筛选岗位（支持分页）
     * @param jobType 筛选条件
     * @return 岗位列表
     */
    @Select("<script>" +
            "SELECT * FROM part_time_job WHERE publish_status = 1 " +
            "<if test='jobType != null and jobType != \"\"'>AND job_type = #{jobType}</if> " +
            "<if test='minSalary != null'>AND daily_salary &gt;= #{minSalary}</if> " +
            "<if test='maxSalary != null'>AND daily_salary &lt;= #{maxSalary}</if> " +
            "<if test='workAddress != null and workAddress != \"\"'>AND work_address LIKE CONCAT('%', #{workAddress}, '%')</if> " +
            "<if test='keyword != null and keyword != \"\"'>AND (job_name LIKE CONCAT('%', #{keyword}, '%') OR job_desc LIKE CONCAT('%', #{keyword}, '%'))</if> " +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Job> selectJobsByFilter(@Param("jobType") String jobType,
                                 @Param("minSalary") BigDecimal minSalary,
                                 @Param("maxSalary") BigDecimal maxSalary,
                                 @Param("workAddress") String workAddress,
                                 @Param("keyword") String keyword,
                                 @Param("pageSize") Integer pageSize,
                                 @Param("offset") Integer offset);

    /**
     * 统计筛选结果总数（分页用）
     * @param filterDTO 筛选条件
     * @return 总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM part_time_job WHERE publish_status = 1 " +
            "<if test='jobType != null and jobType != \"\"'>AND job_type = #{jobType}</if> " +
            "<if test='minSalary != null'>AND daily_salary &gt;= #{minSalary}</if> " +
            "<if test='maxSalary != null'>AND daily_salary &lt;= #{maxSalary}</if> " +
            "<if test='workAddress != null and workAddress != \"\"'>AND work_address LIKE CONCAT('%', #{workAddress}, '%')</if> " +
            "<if test='keyword != null and keyword != \"\"'>AND (job_name LIKE CONCAT('%', #{keyword}, '%') OR job_desc LIKE CONCAT('%', #{keyword}, '%'))</if> " +
            "</script>")
    int countJobsByFilter(JobFilterDTO filterDTO);

    /**
     * 基于技能标签推荐岗位（简化版）
     * @param skillList 技能标签（逗号分隔）
     * @return 匹配的岗位列表
     */
    @Select("<script>" +
            "SELECT * FROM part_time_job WHERE publish_status = 1 " +
            "<foreach collection='skillList' item='skill' separator='OR' open='AND (' close=')'>" +
            "job_type LIKE CONCAT('%', #{skill}, '%') OR job_require LIKE CONCAT('%', #{skill}, '%')" +
            "</foreach> " +
            "LIMIT 10" +
            "</script>")
    List<Job> recommendJobsBySkill(@Param("skillList") List<String> skillList);

    /**
     * 基于地址推荐岗位
     * @param residentAddress 常住地址
     * @return 匹配的岗位列表
     */
    @Select("SELECT * FROM part_time_job WHERE publish_status = 1 AND work_address LIKE CONCAT('%', #{residentAddress}, '%') LIMIT 10")
    List<Job> recommendJobsByAddress(String residentAddress);

    /**
     * 根据雇主ID查询其发布的所有岗位ID
     * @param employerId 雇主用户ID
     * @return 岗位ID列表
     */
    @Select("SELECT id FROM part_time_job WHERE employer_id = #{employerId}")
    List<Long> selectJobIdsByEmployerId(@Param("employerId") Long employerId);

}