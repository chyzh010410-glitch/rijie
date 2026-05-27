package org.example.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.dto.ApiLogDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * API日志切面类（使用Jackson处理JSON）
 */
@Slf4j
@Aspect
@Component
public class ApiLogAspect {

    /**
     * 切点：匹配controller包下的所有方法（测试用，范围简单）
     */
    @Pointcut("execution(* org.example.controller.*.*(..))")
    public void pt() {}

    /**
     * 环绕通知：只记录核心信息
     */
    @Around("pt()")
    public Object aroundLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取请求基本信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String params = joinPoint.getArgs().toString(); // 简单获取参数（测试用）

        // 2. 记录方法执行前的日志
        log.info("===== 请求开始 =====");
        log.info("请求URL：{}", url);
        log.info("请求方式：{}", method);
        log.info("请求参数：{}", params);

        // 3. 执行目标方法，统计耗时
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // 执行原控制器方法
        long costTime = System.currentTimeMillis() - startTime;

        // 4. 记录方法执行后的日志
        log.info("响应结果：{}", result);
        log.info("执行耗时：{} 毫秒", costTime);
        log.info("===== 请求结束 =====\n");

        return result;
    }
}