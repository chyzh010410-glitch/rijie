package org.example.service;

import org.example.dto.RatingDTO;
import org.example.dto.RatingResponseDTO;
import org.example.pojo.Result;

import java.util.List;

public interface RatingService {

    /**
     * 提交评价
     */
    Result submitRating(Long raterId, RatingDTO ratingDTO);

    /**
     * 查询用户的评价记录（作为被评价人）
     */
    List<RatingResponseDTO> getRatingsByRatedId(Long ratedId);

    /**
     * 查询用户的评价记录（作为评价人）
     */
    List<RatingResponseDTO> getRatingsByRaterId(Long raterId);

    /**
     * 查询岗位的评价记录
     */
    List<RatingResponseDTO> getRatingsByJobId(Long jobId);

    /**
     * 检查用户是否有评价权限
     */
    boolean checkRatingPermission(Long raterId, Long jobId);

    /**
     * 获取用户信誉分详情
     */
    Result getUserReputationDetail(Long userId);
}