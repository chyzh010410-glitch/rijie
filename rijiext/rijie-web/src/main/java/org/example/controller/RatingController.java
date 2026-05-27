package org.example.controller;


import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.dto.RatingDTO;
import org.example.pojo.Result;
import org.example.service.RatingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 信誉评价控制层
 */
@RestController
@RequestMapping("/api/rating")
public class RatingController {

    @Resource
    private RatingService ratingService;

    /**
     * 提交评价
     * @param raterId 评价人ID（从JWT token中获取）
     */
    @PostMapping("/submit")
    public Result submitRating(@RequestHeader("X-User-Id") Long raterId,
                               @RequestBody @Valid RatingDTO ratingDTO) {
        return ratingService.submitRating(raterId, ratingDTO);
    }

    /**
     * 查询用户的评价记录（作为被评价人）
     */
    @GetMapping("/user/{userId}")
    public Result getUserRatings(@PathVariable Long userId) {
        try {
            var ratings = ratingService.getRatingsByRatedId(userId);
            return Result.success(ratings);
        } catch (Exception e) {
            return Result.fail("查询评价记录失败: " + e.getMessage());
        }
    }

    /**
     * 查询我的评价记录（作为评价人）
     */
    @GetMapping("/my/given")
    public Result getMyGivenRatings(@RequestHeader("X-User-Id") Long userId) {
        try {
            var ratings = ratingService.getRatingsByRaterId(userId);
            return Result.success(ratings);
        } catch (Exception e) {
            return Result.fail("查询我的评价记录失败: " + e.getMessage());
        }
    }

    /**
     * 查询岗位的评价记录
     */
    @GetMapping("/job/{jobId}")
    public Result getJobRatings(@PathVariable Long jobId) {
        try {
            var ratings = ratingService.getRatingsByJobId(jobId);
            return Result.success(ratings);
        } catch (Exception e) {
            return Result.fail("查询岗位评价失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户信誉分详情
     */
    @GetMapping("/reputation/{userId}")
    public Result getUserReputationDetail(@PathVariable Long userId) {
        return ratingService.getUserReputationDetail(userId);
    }

    /**
     * 检查是否有评价权限
     */
    @GetMapping("/check-permission")
    public Result checkRatingPermission(@RequestHeader("X-User-Id") Long userId,
                                        @RequestParam Long jobId) {
        try {
            boolean hasPermission = ratingService.checkRatingPermission(userId, jobId);
            return Result.success(hasPermission);
        } catch (Exception e) {
            return Result.fail("检查评价权限失败: " + e.getMessage());
        }
    }
}