package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.JobApplication;

import java.util.List;

/**
 * 岗位申请数据操作Mapper接口（对应job_application表）
 */
@Mapper
public interface JobApplicationMapper {

    /**
     * 新增岗位申请（求职者提交申请）
     * @param application 申请实体
     * @return 受影响行数
     */
    @Insert("INSERT INTO job_application (seeker_id, job_id, apply_status, apply_time) " +
            "VALUES (#{seekerId}, #{jobId}, #{applyStatus}, NOW())")
    int insertApplication(JobApplication application);

    /**
     * 校验求职者是否已申请该岗位（避免重复申请）
     * @param seekerId 求职者ID
     * @param jobId 岗位ID
     * @return 申请记录数（>0表示已申请）
     */
    @Select("SELECT COUNT(*) FROM job_application WHERE seeker_id = #{seekerId} AND job_id = #{jobId}")
    int countApplication(@Param("seekerId") Long seekerId, @Param("jobId") Long jobId);

    /**
     * 根据求职者ID查询其所有申请记录
     * @param seekerId 求职者ID
     * @return 申请记录列表
     */
//    @Select("SELECT * FROM job_application WHERE seeker_id = #{seekerId} ORDER BY apply_time DESC")
//    List<JobApplication> selectApplicationsBySeekerId(Long seekerId);
    // ✅ 修改后：关联job表，查询岗位名称
    @Select("""
        SELECT a.*, j.job_name AS jobName 
        FROM job_application a 
        LEFT JOIN part_time_job j ON a.job_id = j.id 
        WHERE a.seeker_id = #{seekerId}
        """)
    List<JobApplication> selectApplicationsBySeekerId(Long seekerId);

    // ✅ 新增：根据seekerId和jobId查询单个申请
    @Select("SELECT * FROM job_application WHERE seeker_id = #{seekerId} AND job_id = #{jobId} LIMIT 1")
    JobApplication selectApplyStatus(@Param("seekerId") Long seekerId, @Param("jobId") Long jobId);


    /**
     * 根据雇主ID查询其岗位的所有申请记录（先通过岗位ID关联，再查申请）
     * @param employerId 雇主ID
     * @return 申请记录列表
     */
    @Select("SELECT a.* FROM job_application a " +
            "LEFT JOIN part_time_job j ON a.job_id = j.id " +
            "WHERE j.employer_id = #{employerId} " +
            "ORDER BY a.apply_time DESC")
    List<JobApplication> selectApplicationsByEmployerId(Long employerId);

    /**
     * 根据申请ID更新申请状态（雇主审核/确认入职）
     * @param id 申请ID
     * @param applyStatus 目标状态
     * @param rejectReason 拒绝原因（仅状态为2时传值）
     * @return 受影响行数
     */
    @Update("<script>" +
            "UPDATE job_application SET apply_status = #{applyStatus} " +
            "<if test='rejectReason != null'>, reject_reason = #{rejectReason}</if> " +
            "WHERE id = #{id}" +
            "</script>")
    int updateApplicationStatus(@Param("id") Long id,
                                @Param("applyStatus") Integer applyStatus,
                                @Param("rejectReason") String rejectReason);

    /**
     * 根据申请ID查询申请详情
     * @param id 申请ID
     * @return 申请实体
     */
    @Select("SELECT * FROM job_application WHERE id = #{id}")
    JobApplication selectApplicationById(Long id);

    /**
     * 注解版：根据岗位ID列表查询申请记录
     * @param jobIds 岗位ID列表
     * @return 申请记录列表
     */
    @Select({
            "<script>", // 关键：开启MyBatis脚本模式，支持动态SQL
            "SELECT * FROM job_application WHERE job_id IN",
            "<foreach collection='jobIds' item='jobId' open='(' separator=',' close=')'>",
            "#{jobId}",
            "</foreach>",
            "</script>"
    })
    List<JobApplication> selectByJobIds(@Param("jobIds") List<Long> jobIds);

}