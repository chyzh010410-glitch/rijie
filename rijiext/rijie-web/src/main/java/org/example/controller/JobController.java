package org.example.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.JobFilterDTO;
import org.example.dto.PageResult;
import org.example.pojo.Job;
import org.example.pojo.Result;
import org.example.service.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 岗位管理控制层
 * 处理岗位相关的前端请求，适配求职者、雇主、管理员三类角色的操作需求
 */
@RestController
@RequestMapping("/api/job") // 统一接口前缀，便于前端请求管理
public class JobController {

    @Resource
    private JobService jobService;

    // ------------------------------ 求职者端接口 ------------------------------
    /**
     * 查询所有已发布的岗位列表（支持岗位类型筛选）
     * @param jobType 岗位类型（可选，如餐饮、快递，不传则查所有）
     * @return 岗位列表
     */
//    @GetMapping("/published/list")
//    public Result getPublishedJobs(@RequestParam(required = false) String jobType) {
//        try {
//            List<Job> jobList = jobService.getPublishedJobs();
//            // 若传入岗位类型，进行过滤（也可在Mapper层实现更高效的数据库筛选）
//            if (jobType != null && !jobType.isEmpty()) {
//                jobList = jobList.stream()
//                        .filter(job -> jobType.equals(job.getJobType()))
//                        .toList();
//            }
//            return Result.success(jobList);
//        } catch (Exception e) {
//            return Result.fail("查询已发布岗位失败：" + e.getMessage());
//        }
//    }
    /**
     * 求职者端：分页查询所有已审核通过的岗位
     * @param jobType 岗位类型（可选，如“服务业”“销售”）
     * @param pageNum 当前页（默认第1页）
     * @param pageSize 每页条数（默认10条）
     * @return 分页后的已审核岗位数据
     */
    @GetMapping("/published/list")
    public Result getApprovedJobsForSeeker(
            @RequestParam(required = false) String jobType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        try {
            // 调用Service的求职者专属方法
            PageInfo<Job> pageInfo = jobService.getApprovedJobsForSeeker(jobType, pageNum, pageSize);
            // 返回前端需要的格式（list+total）
            return Result.success(new PageResult(pageInfo.getList(), pageInfo.getTotal()));
        } catch (Exception e) {
            return Result.fail("求职者查询已审核岗位失败：" + e.getMessage());
        }
    }

    /**
     * 求职者端：模糊搜索已审核岗位（适配前端handleSearch）
     * @param keyword 岗位名称关键词（前端搜索框输入）
     * @param jobType 岗位类型（前端筛选的类型）
     * @param pageNum 当前页（默认第1页）
     * @param pageSize 每页条数（默认10条）
     * @return 分页结果 {list: [], total: 0}
     */
    @GetMapping("/published/search")
    public Result searchApprovedJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String jobType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        try {
            // 调用Service模糊查询方法
            PageInfo<Job> pageInfo = jobService.searchApprovedJobs(keyword, jobType, pageNum, pageSize);
            // 返回前端需要的格式（list+total）
            return Result.success(new PageResult(pageInfo.getList(), pageInfo.getTotal()));
        } catch (Exception e) {
            return Result.fail("搜索岗位失败：" + e.getMessage());
        }
    }

    /**
     * 根据岗位ID查询岗位详情（求职者查看岗位详情）
     * @param id 岗位ID
     * @return 岗位详情
     */
    @GetMapping("/detail/{id}")
    public Result getJobDetail(@PathVariable Long id) {
        try {
            Job job = jobService.getJobById(id);
            if (job == null) {
                return Result.fail("该岗位不存在或已下架");
            }
            return Result.success(job);
        } catch (Exception e) {
            return Result.fail("查询岗位详情失败：" + e.getMessage());
        }
    }

    // ------------------------------ 雇主端接口 ------------------------------
    /**
     * 雇主发布新岗位
     * @param job 岗位信息（JSON格式传递）
     * @return 操作结果
     */
    @PostMapping("/publish")
    public Result publishJob(@RequestBody Job job) {
        try {
            // 简单参数校验：核心字段非空判断
            if (job.getEmployerId() == null || job.getJobName() == null || job.getDailySalary() == null) {
                return Result.fail("岗位名称、雇主ID、日薪为必填项");
            }
            boolean success = jobService.addJob(job);
            if (success) {
                return Result.success("岗位发布成功");
            } else {
                return Result.fail("岗位发布失败");
            }
        } catch (Exception e) {
            return Result.fail("发布岗位异常：" + e.getMessage());
        }
    }

    /**
     * 雇主查询自己发布的所有岗位（增加分页）
     * @param employerId 雇主用户ID
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 分页后的岗位列表
     */
    @GetMapping("/employer/list")
    public Result getEmployerJobs(
            @RequestParam Long employerId,
            @RequestParam(defaultValue = "1") Integer pageNum,  // 新增：分页参数-页码
            @RequestParam(defaultValue = "10") Integer pageSize  // 新增：分页参数-每页条数
    ) {
        try {
            // 调用Service层的分页查询方法
            PageInfo<Job> jobPage = jobService.getJobsByEmployerId(employerId, pageNum, pageSize);
            // 返回分页结果（包含列表+总条数）
            return Result.success(jobPage);
        } catch (Exception e) {
            return Result.fail("查询您的岗位列表失败：" + e.getMessage());
        }
    }

    /**
     * 雇主更新自己岗位的发布状态（如草未审核→已审核、已审核→已结束）
     * @param id 岗位ID
     * @param publishStatus 目标状态（0=未审核，1=已审核，2=已结束）
     * @return 操作结果
     */
    @PutMapping("/status/update")
    public Result updateJobStatus(@RequestParam Long id, @RequestParam Integer publishStatus) {
        try {
            boolean success = jobService.updateJobStatus(id, publishStatus);
            if (success) {
                String statusMsg = switch (publishStatus) {
                    case 0 -> "未审核";
                    case 1 -> "已审核";
                    case 2 -> "已结束";
                    default -> "";
                };
                return Result.success("岗位状态已更新为" + statusMsg);
            } else {
                return Result.fail("岗位状态更新失败");
            }
        } catch (Exception e) {
            return Result.fail("更新岗位状态异常：" + e.getMessage());
        }
    }

    /**
     * 雇主删除自己的岗位
     * @param id 岗位ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteJob(@PathVariable Long id) {
        try {
            boolean success = jobService.deleteJobById(id);
            if (success) {
                return Result.success("岗位删除成功");
            } else {
                return Result.fail("岗位删除失败");
            }
        } catch (Exception e) {
            return Result.fail("删除岗位异常：" + e.getMessage());
        }
    }

    // ------------------------------ 管理员端接口 ------------------------------
    /**
     * 管理员查询所有岗位（包括草稿、已发布、已结束）
     * @return 所有岗位列表
     */
    @GetMapping("/admin/all")
// 新增pageNum、pageSize参数（默认值：第1页，每页10条）
    public Result getAllJobs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // 调用“带分页的查询所有岗位”Service方法（需你给JobService新增该方法）
            PageInfo<Job> pageInfo = jobService.getAllJobsWithPage(pageNum, pageSize);
            // 返回“当前页数据+总条数”（适配前端分页组件）
            return Result.success(new PageResult(
                    pageInfo.getList(),  // 分页后的数据列表
                    pageInfo.getTotal()   // 岗位总条数
            ));
        } catch (Exception e) {
            return Result.fail("查询所有岗位失败：" + e.getMessage());
        }
    }

    /**
     * 管理员获取未审核岗位列表（分页）
     */
    @GetMapping("/admin/list")
    public Result getUnreviewedJobs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            // 1. 分页查询未审核岗位
            PageInfo<?> pageInfo = jobService.getUnreviewedJobs(keyword, pageNum, pageSize);
            // 2. 封装分页结果（list=数据列表，total=总条数）
            PageResult pageResult = new PageResult(pageInfo.getList(), pageInfo.getTotal());
            // 3. 用Result包装分页结果返回（符合统一响应规范）
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.fail("获取未审核岗位失败：" + e.getMessage());
        }
    }

    /**
     * 管理员强制更新岗位状态（可操作所有岗位）
     * @param id 岗位ID
     * @param publishStatus 目标状态
     * @return 操作结果
     */
    @PutMapping("/admin/status/update")
    public Result adminUpdateJobStatus(@RequestParam Long id, @RequestParam Integer publishStatus) {
        return updateJobStatus(id, publishStatus); // 复用雇主的状态更新方法
    }

    /**
     * 管理员强制删除岗位（可删除所有岗位）
     * @param id 岗位ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/delete/{id}")
    public Result adminDeleteJob(@PathVariable Long id) {
        return deleteJob(id); // 复用雇主的删除方法
    }



    // ------------------------------ 岗位筛选接口 ------------------------------
    /**
     * 岗位多条件筛选（带分页）
     * @param filterDTO 筛选条件（前端以JSON或表单参数传递）
     * @return 筛选结果（含分页信息）
     */
    @PostMapping("/filter")
    public Result filterJobs(@RequestBody JobFilterDTO filterDTO) {
        try {
            Map<String, Object> result = jobService.filterJobs(filterDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("岗位筛选失败：" + e.getMessage());
        }
    }

    /**
     * 为求职者推荐岗位
     * @param seekerId 求职者ID
     * @return 推荐岗位列表
     */
    @GetMapping("/recommend")
    public Result recommendJobs(@RequestParam Long seekerId) {
        try {
            return Result.success(jobService.recommendJobs(seekerId));
        } catch (Exception e) {
            return Result.fail("岗位推荐失败：" + e.getMessage());
        }
    }
}