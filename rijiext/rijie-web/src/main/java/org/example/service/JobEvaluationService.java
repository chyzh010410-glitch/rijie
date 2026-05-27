package org.example.service;

import org.example.pojo.JobEvaluation;
import org.example.pojo.Result;

public interface JobEvaluationService {
    Result addEvaluation(JobEvaluation evaluation);
    Result getMyEvaluation(Long seekerId);
    Result getEmployerEvaluation(Long employerId);
    Result getAllEvaluation();
    Result deleteEvaluation(Long id);
    Result getEvaluationByJobId(Long jobId);
}