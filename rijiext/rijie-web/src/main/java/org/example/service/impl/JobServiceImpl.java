package org.example.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.dto.JobFilterDTO;
import org.example.mapper.JobMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.Job;
import org.example.pojo.User;
import org.example.service.JobService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 岗位业务层实现类
 * 实现岗位相关的业务逻辑，依赖Mapper层完成数据操作
 */
@Service // 标记为Spring服务组件，纳入IOC容器管理
public class JobServiceImpl implements JobService {

    @Resource// 注入JobMapper代理对象（也可使用@Autowired）
    private JobMapper jobMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 查询所有【未审核/已审核】的岗位
     */
    @Override
    public List<Job> getPublishedJobs() {
        // 调用Mapper层方法
        return jobMapper.selectPublishedJobs();
    }

    // ========== 实现：求职者专属 - 分页查询已审核岗位 ==========
    @Override
    public PageInfo<Job> getApprovedJobsForSeeker(String jobType, Integer pageNum, Integer pageSize) {
        // 1. 开启分页（PageHelper自动拦截后续SQL）
        PageHelper.startPage(pageNum, pageSize);
        // 2. 调用Mapper查询已审核岗位（仅publish_status=1）
        List<Job> jobList = jobMapper.selectApprovedJobsForSeeker(jobType);
        // 3. 封装分页结果（包含总条数、总页数等）
        return new PageInfo<>(jobList);
    }

    // ========== 实现模糊查询方法 ==========
    @Override
    public PageInfo<Job> searchApprovedJobs(String keyword, String jobType, Integer pageNum, Integer pageSize) {
        // 1. 开启分页（PageHelper自动拦截后续SQL）
        PageHelper.startPage(pageNum, pageSize);
        // 2. 调用Mapper模糊查询（自动过滤已审核岗位）
        List<Job> jobList = jobMapper.selectApprovedJobsByKeyword(keyword, jobType);
        // 3. 封装分页结果（包含总条数、总页数等）
        return new PageInfo<>(jobList);
    }

    // 新增方法：查询所有状态的岗位
    @Override
    public List<Job> getAllJobs() {
        return jobMapper.selectAllJobs();
    }

    /**
     * 分页查询雇主的岗位列表
     */
    public PageInfo<Job> getJobsByEmployerId(Long employerId, Integer pageNum, Integer pageSize) {
        // 关键：先开启分页（PageHelper会自动给后续的SQL加LIMIT）
        PageHelper.startPage(pageNum, pageSize);
        // 调用Mapper查询（此时SQL会自动带分页）
        List<Job> jobList = jobMapper.getJobsByEmployerId(employerId);
        // 封装成分页结果
        return new PageInfo<>(jobList);
    }

    /**
     * 根据ID查询岗位详情
     */
    @Override
    public Job getJobById(Long id) {
        if (id == null || id <= 0) {
            return null; // 入参校验，避免无效查询
        }
        return jobMapper.selectJobById(id);
    }

    /**
     * 新增岗位
     */
    @Override
    public boolean addJob(Job job) {
        if (job == null) {
            return false; // 入参校验
        }
        // 调用Mapper层插入方法，判断受影响行数是否大于0
        int rows = jobMapper.insertJob(job);
        return rows > 0;
    }

    /**
     * 更新岗位发布状态
     */
    @Override
    public boolean updateJobStatus(Long id, Integer publishStatus) {
        if (id == null || id <= 0 || publishStatus == null || publishStatus < 0 || publishStatus > 2) {
            return false; // 入参校验：状态只能是0/1/2
        }
        // 调用Mapper层更新方法
        int rows = jobMapper.updateJobStatus(id, publishStatus);
        return rows > 0;
    }

    /**
     * 根据ID删除岗位
     */
    @Override
    public boolean deleteJobById(Long id) {
        if (id == null || id <= 0) {
            return false; // 入参校验
        }
        // 调用Mapper层删除方法
        int rows = jobMapper.deleteJobById(id);
        return rows > 0;
    }

    /**
     * 根据雇主ID查询其发布的岗位
     */
    @Override
    public List<Job> getJobsByEmployerId(Long employerId) {
        if (employerId == null || employerId <= 0) {
            return null; // 入参校验
        }
        // 调用Mapper层自定义方法（需在JobMapper中补充该方法）
        return jobMapper.selectJobsByEmployerId(employerId);
    }

    /**
     * 多条件筛选岗位：计算分页偏移量，调用Mapper查询
     */
    @Override
    public Map<String, Object> filterJobs(JobFilterDTO filterDTO) {
        // 1. 计算分页偏移量（offset = (pageNum-1)*pageSize）
        Integer offset = (filterDTO.getPageNum() - 1) * filterDTO.getPageSize();

        // 2. 调用Mapper查询岗位列表
        List<Job> jobList = jobMapper.selectJobsByFilter(
                filterDTO.getJobType(),
                filterDTO.getMinSalary(),
                filterDTO.getMaxSalary(),
                filterDTO.getWorkAddress(),
                filterDTO.getKeyword(),
                filterDTO.getPageSize(),
                offset
        );

        // 3. 统计总数
        int total = jobMapper.countJobsByFilter(filterDTO);

        // 4. 封装结果（分页数据）
        Map<String, Object> result = new HashMap<>();
        result.put("jobList", jobList);
        result.put("total", total);
        result.put("pageNum", filterDTO.getPageNum());
        result.put("pageSize", filterDTO.getPageSize());
        result.put("totalPages", (total + filterDTO.getPageSize() - 1) / filterDTO.getPageSize());

        return result;
    }

    // JobServiceImpl实现
    public PageInfo<Job> getAllJobsWithPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); // 开启物理分页
        List<Job> jobList = jobMapper.selectAllJobs(); // 调用你的全量查询Mapper方法
        return new PageInfo<>(jobList); // 封装分页结果
    }

    @Override
    public PageInfo<Job> getUnreviewedJobs(String keyword, Integer pageNum, Integer pageSize) {
        // 1. 开启分页（PageHelper核心用法）
        PageHelper.startPage(pageNum, pageSize);
        // 2. 调用Mapper查询数据
        List<Job> jobList = keyword == null ?
                jobMapper.getJobsByStatus(0) :
                jobMapper.searchUnreviewedJobs(keyword);
        // 3. 封装分页信息并返回
        return new PageInfo<>(jobList);
    }
    /**
     * 推荐岗位：先按技能标签匹配，再按地址匹配，去重后返回
     */
    @Override
    public List<Job> recommendJobs(Long seekerId) {
        // 1. 查询求职者信息
        User user = userMapper.selectUserById(seekerId);
        // 获取求职者信誉分
        Double reputationScore = user.getReputationScore() != null ? user.getReputationScore() : 5.0;
        if (user == null || user.getSkillTags() == null && user.getResidentAddress() == null) {
            return new ArrayList<>(); // 无匹配条件，返回空
        }

        List<Job> recommendList = new ArrayList<>();

        // 2. 按技能标签推荐
        if (user.getSkillTags() != null && !user.getSkillTags().isEmpty()) {
            // 将技能标签拆分为列表（如“餐饮,收银”→["餐饮","收银"]）
            List<String> skillList = List.of(user.getSkillTags().split(","));
            List<Job> skillJobs = jobMapper.recommendJobsBySkill(skillList);
            recommendList.addAll(skillJobs);
        }

        // 3. 按地址推荐
        if (user.getResidentAddress() != null && !user.getResidentAddress().isEmpty()) {
            List<Job> addressJobs = jobMapper.recommendJobsByAddress(user.getResidentAddress());
            recommendList.addAll(addressJobs);
        }

        // 4. 去重（根据岗位ID去重）
        List<Job> finalList = recommendList.stream()
                .distinct()
                .filter(job -> {
                    // 查询雇主的信誉分
                    User employer = userMapper.selectUserById(job.getEmployerId());
                    return employer != null &&
                            employer.getReputationScore() != null &&
                            employer.getReputationScore() >= 3.0;
                })
                .sorted((j1, j2) -> {
                    // 按雇主信誉分+薪资综合排序
                    User emp1 = userMapper.selectUserById(j1.getEmployerId());
                    User emp2 = userMapper.selectUserById(j2.getEmployerId());
                    double score1 = (emp1.getReputationScore() * 20) + j1.getDailySalary().doubleValue();
                    double score2 = (emp2.getReputationScore() * 20) + j2.getDailySalary().doubleValue();
                    return Double.compare(score2, score1); // 降序
                })
                .limit(10) // 最多推荐10个
                .collect(Collectors.toList());

        return finalList;
    }
}