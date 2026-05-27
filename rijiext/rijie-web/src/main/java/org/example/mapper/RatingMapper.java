package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Rating;
import org.example.dto.RatingResponseDTO;

import java.util.List;

@Mapper
public interface RatingMapper {

    @Insert("INSERT INTO rating (rater_id, rated_id, job_id, rating_type, score, content, tags, is_anonymous) " +
            "VALUES (#{raterId}, #{ratedId}, #{jobId}, #{ratingType}, #{score}, #{content}, #{tags}, #{isAnonymous})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertRating(Rating rating);

    @Select("SELECT COUNT(*) FROM rating WHERE rater_id = #{raterId} AND job_id = #{jobId}")
    int countByRaterAndJob(@Param("raterId") Long raterId, @Param("jobId") Long jobId);


    @Select("SELECT r.*, " +
            "ru.username as rater_name, ru.real_name as rater_real_name, " +
            "eu.username as rated_name, eu.real_name as rated_real_name, " +
            "j.job_name " +
            "FROM rating r " +
            "LEFT JOIN sys_user ru ON r.rater_id = ru.id " +
            "LEFT JOIN sys_user eu ON r.rated_id = eu.id " +
            "LEFT JOIN part_time_job j ON r.job_id = j.id " +
            "WHERE r.rated_id = #{ratedId} " +
            "ORDER BY r.create_time DESC")
    List<RatingResponseDTO> selectByRatedId(Long ratedId);

    @Select("SELECT r.*, " +
            "ru.username as rater_name, ru.real_name as rater_real_name, " +
            "eu.username as rated_name, eu.real_name as rated_real_name, " +
            "j.job_name " +
            "FROM rating r " +
            "LEFT JOIN sys_user ru ON r.rater_id = ru.id " +
            "LEFT JOIN sys_user eu ON r.rated_id = eu.id " +
            "LEFT JOIN part_time_job j ON r.job_id = j.id " +
            "WHERE r.rater_id = #{raterId} " +
            "ORDER BY r.create_time DESC")
    List<RatingResponseDTO> selectByRaterId(Long raterId);

    @Select("SELECT r.*, " +
            "ru.username as rater_name, ru.real_name as rater_real_name, " +
            "eu.username as rated_name, eu.real_name as rated_real_name, " +
            "j.job_name " +
            "FROM rating r " +
            "LEFT JOIN sys_user ru ON r.rater_id = ru.id " +
            "LEFT JOIN sys_user eu ON r.rated_id = eu.id " +
            "LEFT JOIN part_time_job j ON r.job_id = j.id " +
            "WHERE r.job_id = #{jobId} " +
            "ORDER BY r.create_time DESC")
    List<RatingResponseDTO> selectByJobId(Long jobId);
    @Select("SELECT AVG(score) FROM rating WHERE rated_id = #{ratedId}")
    Double calculateAvgScore(Long ratedId);

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