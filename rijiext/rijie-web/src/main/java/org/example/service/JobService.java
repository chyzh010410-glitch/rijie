package org.example.service;

import com.github.pagehelper.PageInfo;
import org.example.dto.JobFilterDTO;
import org.example.pojo.Job;
import java.util.List;
import java.util.Map;

/**
 * 岗位业务层接口
 * 定义岗位相关的核心业务操作
 */
public interface JobService {

    /**
     * 查询所有【未审核/已审核】的岗位
     * @return 未审核/已审核岗位列表
     */
    List<Job> getPublishedJobs();

    // 新增方法：查询所有状态的岗位
    List<Job> getAllJobs();

    /**
     * 根据岗位ID查询单个岗位详情（求职者/雇主/管理员端通用）
     * @param id 岗位ID
     * @return 岗位对象（无数据则返回null）
     */
    Job getJobById(Long id);

    /**
     * 新增岗位（雇主端发布岗位）
     * @param job 岗位实体对象
     * @return 操作结果：true=成功，false=失败
     */
    boolean addJob(Job job);

    /**
     * 分页查询雇主的岗位列表
     * @param employerId 雇主ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果（PageInfo包含list和total）
     */
    PageInfo<Job> getJobsByEmployerId(Long employerId, Integer pageNum, Integer pageSize);

    /**
     * 更新岗位的发布状态（如草稿→已发布、已发布→已结束）
     * @param id 岗位ID
     * @param publishStatus 目标状态（0=草稿，1=已发布，2=已结束）
     * @return 操作结果：true=成功，false=失败
     */
    boolean updateJobStatus(Long id, Integer publishStatus);

    /**
     * 根据ID删除岗位（雇主/管理员端删除岗位）
     * @param id 岗位ID
     * @return 操作结果：true=成功，false=失败
     */
    boolean deleteJobById(Long id);

    /**
     * 根据雇主ID查询其发布的所有岗位（雇主端个人中心使用）
     * @param employerId 雇主用户ID
     * @return 雇主发布的岗位列表
     */
    List<Job> getJobsByEmployerId(Long employerId);

    /**
     * 多条件筛选岗位（带分页）
     * @param filterDTO 筛选条件
     * @return 包含岗位列表和总数的Map
     */
    Map<String, Object> filterJobs(JobFilterDTO filterDTO);

    /**
     * 为求职者推荐岗位（技能+地址双重匹配）
     * @param seekerId 求职者ID
     * @return 推荐岗位列表
     */
    List<Job> recommendJobs(Long seekerId);
    // ========== 新增：求职者专属 - 分页查询已审核岗位 ==========
    /**
     * 求职者端：分页查询已审核通过的岗位
     * @param jobType 岗位类型（可为空）
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页后的已审核岗位数据
     */
    PageInfo<Job> getApprovedJobsForSeeker(String jobType, Integer pageNum, Integer pageSize);

    // ========== 求职者模糊查询已审核岗位 ==========
    /**
     * 求职者端：分页模糊查询已审核岗位
     * @param keyword 岗位名称关键词（可为空）
     * @param jobType 岗位类型（可为空）
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页后的匹配岗位数据
     */
    PageInfo<Job> searchApprovedJobs(String keyword, String jobType, Integer pageNum, Integer pageSize);

    PageInfo<Job> getUnreviewedJobs(String keyword, Integer pageNum, Integer pageSize);

    PageInfo<Job> getAllJobsWithPage(Integer pageNum, Integer pageSize);

}