package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.JobEvaluation;
import java.util.List;

@Mapper
public interface JobEvaluationMapper {

    // 新增评价
    @Insert("INSERT INTO job_evaluation(job_id, seeker_id, employer_id, score, content, create_time, status) " +
            "VALUES(#{jobId}, #{seekerId}, #{employerId}, #{score}, #{content}, #{createTime}, #{status})")
    int insertEvaluation(JobEvaluation evaluation);

    // 校验是否重复评价
    @Select("SELECT * FROM job_evaluation WHERE seeker_id=#{seekerId} AND job_id=#{jobId}")
    JobEvaluation selectBySeekerAndJob(@Param("seekerId") Long seekerId, @Param("jobId") Long jobId);

    // 员工：查看我的评价
    @Select("SELECT * FROM job_evaluation WHERE seeker_id=#{seekerId} ORDER BY create_time DESC")
    List<JobEvaluation> selectBySeekerId(Long seekerId);

    // 雇主：查看收到的评价
    @Select("SELECT * FROM job_evaluation WHERE employer_id=#{employerId} ORDER BY create_time DESC")
    List<JobEvaluation> selectByEmployerId(Long employerId);

    // 管理员：查询所有评价
    @Select("SELECT * FROM job_evaluation ORDER BY create_time DESC")
    List<JobEvaluation> selectAll();

    // 管理员：删除评价
    @Delete("DELETE FROM job_evaluation WHERE id=#{id}")
    int deleteById(Long id);

    // 根据岗位ID，查询该岗位的所有公开评价（所有人可见）
    @Select("SELECT * FROM job_evaluation WHERE job_id=#{jobId} AND status=1 ORDER BY create_time DESC")
    List<JobEvaluation> selectByJobId(Long jobId);
}