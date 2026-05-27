package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.mapper.JobApplicationMapper;
import org.example.mapper.JobMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.Job;
import org.example.pojo.JobApplication;
import org.example.pojo.User;
import org.example.service.JobApplicationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 岗位申请业务层实现类
 */
@Service
public class JobApplicationServiceImpl implements JobApplicationService {


    // 新增：注入JobMapper
    @Resource // 或 @Autowired
    private JobMapper jobMapper;

    // 同时确保已注入其他需要的Mapper（如UserMapper、JobApplicationMapper）
    @Resource
    private UserMapper userMapper;

    @Resource
    private JobApplicationMapper applicationMapper;

    /**
     * 提交岗位申请：先校验是否重复申请，再新增
     */
    @Override
    public boolean applyJob(Long seekerId, Long jobId) {
        // 1. 入参校验
        if (seekerId == null || seekerId <= 0 || jobId == null || jobId <= 0) {
            return false;
        }
        // 2. 校验是否已申请该岗位
        int count = applicationMapper.countApplication(seekerId, jobId);
        if (count > 0) {
            return false; // 重复申请
        }
        // 3. 构建申请实体，默认状态为0-待审核
        JobApplication application = new JobApplication();
        application.setSeekerId(seekerId);
        application.setJobId(jobId);
        application.setApplyStatus(0);
        // 4. 新增申请
        int rows = applicationMapper.insertApplication(application);
        return rows > 0;
    }

    /**
     * 查询求职者的申请记录
     */
    @Override
    public List<JobApplication> getMyApplications(Long seekerId) {
        if (seekerId == null || seekerId <= 0) {
            return null;
        }
        return applicationMapper.selectApplicationsBySeekerId(seekerId);
    }

    // ✅ 新增：根据seekerId和jobId查询单个申请状态
    @Override
    public JobApplication getApplyStatus(Long seekerId, Long jobId) {
        if (seekerId == null || jobId == null) {
            return null;
        }
        // 调用Mapper查询
        return applicationMapper.selectApplyStatus(seekerId, jobId);
    }

    /**
     * 查询雇主的岗位申请记录
     */
//    @Override
//    public List<JobApplication> getJobApplications(Long employerId) {
//        if (employerId == null || employerId <= 0) {
//            return null;
//        }
//        return applicationMapper.selectApplicationsByEmployerId(employerId);
//    }
    /**
     * 雇主查询自己岗位的申请记录（关联User+Job表）
     */
    @Override
    public List<Map<String, Object>> getJobApplications(Long employerId) {
        // 1. 先查该雇主发布的所有岗位ID
        List<Long> jobIds = jobMapper.selectJobIdsByEmployerId(employerId);
        if (jobIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 根据岗位ID查所有申请记录
        List<JobApplication> applicationList = applicationMapper.selectByJobIds(jobIds);

        // 3. 关联User和Job表，补充前端需要的字段
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (JobApplication app : applicationList) {
            Map<String, Object> map = new HashMap<>();
            // 申请记录原有字段
            map.put("id", app.getId());
            map.put("seekerId", app.getSeekerId());
            map.put("jobId", app.getJobId());
            map.put("applyStatus", app.getApplyStatus());
            map.put("applyTime", app.getApplyTime());
            map.put("rejectReason", app.getRejectReason());

            // 关联User表：补充求职者姓名、手机号
            User seeker = userMapper.selectUserById(app.getSeekerId());
            if (seeker != null) {
                map.put("seekerName", seeker.getRealName()); // 求职者真实姓名
                map.put("seekerPhone", seeker.getPhone());   // 求职者手机号
            } else {
                map.put("seekerName", "未知");
                map.put("seekerPhone", "未知");
            }

            // 关联Job表：补充岗位名称
            Job job = jobMapper.selectJobNameById(app.getJobId());
            if (job != null) {
                map.put("jobName", job.getJobName()); // 岗位名称
            } else {
                map.put("jobName", "未知岗位");
            }

            resultList.add(map);
        }
        return resultList;
    }


    /**
     * 雇主审核申请
     */
    @Override
    public boolean auditApplication(Long id, Integer applyStatus, String rejectReason) {
        // 1. 入参校验：仅允许状态1（通过）、2（拒绝）
        if (id == null || id <= 0 || applyStatus == null || (applyStatus != 1 && applyStatus != 2)) {
            return false;
        }
        // 2. 更新申请状态
        int rows = applicationMapper.updateApplicationStatus(id, applyStatus, rejectReason);
        return rows > 0;
    }

    /**
     * 求职者确认入职
     */
    @Override
    public boolean confirmEntry(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        // 将状态改为3-已入职，无拒绝原因
        int rows = applicationMapper.updateApplicationStatus(id, 3, null);
        return rows > 0;
    }
}