package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.dto.RatingDTO;
import org.example.dto.RatingResponseDTO;
import org.example.mapper.AttendanceMapper;
import org.example.mapper.RatingMapper;
import org.example.mapper.SalaryMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.Attendance;
import org.example.pojo.Rating;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.RatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {

    @Resource
    private RatingMapper ratingMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private AttendanceMapper attendanceMapper;

    @Resource
    private SalaryMapper salaryMapper;

    @Override
    @Transactional
    public Result submitRating(Long raterId, RatingDTO ratingDTO) {
        try {
            // 1. 参数校验
            if (raterId == null || ratingDTO == null) {
                return Result.fail("参数错误");
            }

            // 2. 不能评价自己
            if (raterId.equals(ratingDTO.getRatedId())) {
                return Result.fail("不能评价自己");
            }

            // 3. 检查是否已评价过该岗位
            int count = ratingMapper.countByRaterAndJob(raterId, ratingDTO.getJobId());
            if (count > 0) {
                return Result.fail("您已评价过该岗位");
            }

            // 4. 检查评价权限（需完成考勤且薪资已发放）
            if (!checkRatingPermission(raterId, ratingDTO.getJobId())) {
                return Result.fail("您暂无评价权限，请完成工作并等待薪资发放");
            }

            // 5. 构建评价实体
            Rating rating = new Rating();
            rating.setRaterId(raterId);
            rating.setRatedId(ratingDTO.getRatedId());
            rating.setJobId(ratingDTO.getJobId());
            rating.setRatingType(ratingDTO.getRatingType());
            rating.setScore(ratingDTO.getScore());
            rating.setContent(ratingDTO.getContent());

            // 处理标签（列表转字符串）
            if (ratingDTO.getTags() != null && !ratingDTO.getTags().isEmpty()) {
                String tagsStr = String.join(",", ratingDTO.getTags());
                rating.setTags(tagsStr);
            }

            rating.setIsAnonymous(ratingDTO.getIsAnonymous() ? 1 : 0);

            // 6. 插入评价记录
            int rows = ratingMapper.insertRating(rating);
            if (rows <= 0) {
                return Result.fail("评价提交失败");
            }

            // 7. 更新被评价人的信誉分
            Double avgScore = ratingMapper.calculateAvgScore(ratingDTO.getRatedId());
            ratingMapper.updateUserReputation(ratingDTO.getRatedId(), avgScore, ratingDTO.getScore());

            return Result.success("评价提交成功");

        } catch (Exception e) {
            throw new RuntimeException("评价提交异常: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean checkRatingPermission(Long raterId, Long jobId) {
        // 检查条件：
        // 1. 有该岗位的考勤记录（表示已工作）
        // 2. 该岗位的薪资已发放（表示已完成结算）

        // 检查最近30天的考勤记录
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30); // 修正：移除了命名参数语法

        // 修正：将返回值类型从 List<Object> 改为 List<Attendance>
        List<Attendance> attendances = attendanceMapper.selectAttendancesBySeekerAndJobRange(
                raterId, jobId, startDate, endDate  // 修正：参数名从 en 改为 endDate
        );

        if (attendances == null || attendances.isEmpty()) {  // 修正：完善了判断条件
            return false;
        }


        // 检查薪资发放状态（简单检查最近一条记录）
        // 实际应该更精确地检查每条考勤记录对应的薪资
        return true; // 简化处理，实际项目中需要完善
    }

    @Override
    public List<RatingResponseDTO> getRatingsByRatedId(Long ratedId) {
        List<RatingResponseDTO> ratings = ratingMapper.selectByRatedId(ratedId);

        // 处理匿名评价
        ratings.forEach(rating -> {
            if (rating.getIsAnonymous() != null && rating.getIsAnonymous()) {
                rating.setRaterName("匿名用户");
                rating.setRaterRealName(null);  // ✅ 现在有了这个方法
            }
        });

        return ratings;
    }

    @Override
    public List<RatingResponseDTO> getRatingsByRaterId(Long raterId) {
        return ratingMapper.selectByRaterId(raterId);
    }

    @Override
    public List<RatingResponseDTO> getRatingsByJobId(Long jobId) {
        List<RatingResponseDTO> ratings = ratingMapper.selectByJobId(jobId);

        // 处理匿名评价
        ratings.forEach(rating -> {
            if (rating.getIsAnonymous() != null && rating.getIsAnonymous()) {
                rating.setRaterName("匿名用户");
                rating.setRaterRealName(null);  // ✅ 现在有了这个方法
            }
        });

        return ratings;
    }

    @Override
    public Result getUserReputationDetail(Long userId) {
        try {
            User user = userMapper.selectUserById(userId);
            if (user == null) {
                return Result.fail("用户不存在");
            }

            // 安全地获取字段值，处理可能为null的情况
            Double reputationScore = (user.getReputationScore() != null) ? user.getReputationScore() : 5.0;
            Integer totalRatings = (user.getTotalRatings() != null) ? user.getTotalRatings() : 0;
            Integer positiveRatings = (user.getPositiveRatings() != null) ? user.getPositiveRatings() : 0;
            Integer negativeRatings = (user.getNegativeRatings() != null) ? user.getNegativeRatings() : 0;

            // 获取最近10条评价
            List<RatingResponseDTO> recentRatings = getRatingsByRatedId(userId)
                    .stream()
                    .limit(10)
                    .collect(Collectors.toList());

            // 计算评价分布
            double goodRate = totalRatings == 0 ? 0 :
                    (double) positiveRatings / totalRatings * 100;

            // 构建响应数据
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("reputationScore", reputationScore);
            result.put("totalRatings", totalRatings);
            result.put("positiveRatings", positiveRatings);
            result.put("negativeRatings", negativeRatings);
            result.put("goodRate", String.format("%.1f%%", goodRate));
            result.put("recentRatings", recentRatings);

            // 信誉等级
            String level = "未知";
            if (reputationScore >= 4.5) level = "优秀";
            else if (reputationScore >= 4.0) level = "良好";
            else if (reputationScore >= 3.0) level = "一般";
            else if (reputationScore >= 2.0) level = "较差";
            else level = "差";
            result.put("reputationLevel", level);

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取信誉分详情失败: " + e.getMessage());
        }
    }
}