package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.JobEvaluation;

import java.util.List;

@Mapper
public interface JobEvaluationMapper {

    @Insert("INSERT INTO job_evaluation (job_id, seeker_id, employer_id, score, content, tags, is_anonymous, create_time, status) " +
            "VALUES (#{jobId}, #{seekerId}, #{employerId}, #{score}, #{content}, #{tags}, #{isAnonymous}, #{createTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertEvaluation(JobEvaluation evaluation);

    @Select("SELECT COUNT(*) FROM job_evaluation WHERE seeker_id = #{seekerId} AND job_id = #{jobId}")
    int countBySeekerAndJob(@Param("seekerId") Long seekerId, @Param("jobId") Long jobId);

    @Select("SELECT * FROM job_evaluation WHERE seeker_id = #{seekerId} AND job_id = #{jobId}")
    JobEvaluation selectBySeekerAndJob(@Param("seekerId") Long seekerId, @Param("jobId") Long jobId);

    @Select("SELECT * FROM job_evaluation WHERE seeker_id = #{seekerId} ORDER BY create_time DESC")
    List<JobEvaluation> selectBySeekerId(Long seekerId);

    @Select("SELECT * FROM job_evaluation WHERE employer_id = #{employerId} ORDER BY create_time DESC")
    List<JobEvaluation> selectByEmployerId(Long employerId);

    @Select("SELECT * FROM job_evaluation ORDER BY create_time DESC")
    List<JobEvaluation> selectAll();

    @Delete("DELETE FROM job_evaluation WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM job_evaluation WHERE job_id = #{jobId} AND status = 1 ORDER BY create_time DESC")
    List<JobEvaluation> selectByJobId(Long jobId);

    // ===== 带 JOIN 的增强查询 =====

    @Select("SELECT e.*, " +
            "su.username AS seeker_name, su.real_name AS seeker_real_name, " +
            "eu.username AS employer_name, eu.real_name AS employer_real_name, " +
            "j.job_name " +
            "FROM job_evaluation e " +
            "LEFT JOIN sys_user su ON e.seeker_id = su.id " +
            "LEFT JOIN sys_user eu ON e.employer_id = eu.id " +
            "LEFT JOIN part_time_job j ON e.job_id = j.id " +
            "WHERE e.employer_id = #{employerId} AND e.status = 1 " +
            "ORDER BY e.create_time DESC")
    List<JobEvaluation> selectByEmployerIdWithDetails(@Param("employerId") Long employerId);

    @Select("SELECT e.*, " +
            "su.username AS seeker_name, su.real_name AS seeker_real_name, " +
            "eu.username AS employer_name, eu.real_name AS employer_real_name, " +
            "j.job_name " +
            "FROM job_evaluation e " +
            "LEFT JOIN sys_user su ON e.seeker_id = su.id " +
            "LEFT JOIN sys_user eu ON e.employer_id = eu.id " +
            "LEFT JOIN part_time_job j ON e.job_id = j.id " +
            "WHERE e.job_id = #{jobId} AND e.status = 1 " +
            "ORDER BY e.create_time DESC")
    List<JobEvaluation> selectByJobIdWithDetails(@Param("jobId") Long jobId);

    @Select("SELECT e.*, " +
            "su.username AS seeker_name, su.real_name AS seeker_real_name, " +
            "eu.username AS employer_name, eu.real_name AS employer_real_name, " +
            "j.job_name, " +
            "eu.reputation_score AS employer_reputation_score, " +
            "eu.total_ratings AS employer_total_ratings " +
            "FROM job_evaluation e " +
            "LEFT JOIN sys_user su ON e.seeker_id = su.id " +
            "LEFT JOIN sys_user eu ON e.employer_id = eu.id " +
            "LEFT JOIN part_time_job j ON e.job_id = j.id " +
            "WHERE e.status = 1 " +
            "ORDER BY e.create_time DESC")
    List<JobEvaluation> selectAllWithDetails();

    // ===== 评分统计和信誉分更新 =====

    @Select("SELECT AVG(score) FROM job_evaluation WHERE employer_id = #{employerId} AND status = 1")
    Double calculateAvgScoreForEmployer(@Param("employerId") Long employerId);

    @Update("UPDATE sys_user SET " +
            "reputation_score = #{score}, " +
            "total_ratings = total_ratings + 1, " +
            "positive_ratings = positive_ratings + CASE WHEN #{newScore} >= 4 THEN 1 ELSE 0 END, " +
            "negative_ratings = negative_ratings + CASE WHEN #{newScore} <= 2 THEN 1 ELSE 0 END " +
            "WHERE id = #{userId}")
    int updateUserReputation(@Param("userId") Long userId,
                             @Param("score") Double score,
                             @Param("newScore") Integer newScore);
}
