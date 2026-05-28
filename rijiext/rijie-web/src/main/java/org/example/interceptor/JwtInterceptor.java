package org.example.interceptor;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Result;
import org.example.utils.JwtUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * JWT令牌验证拦截器（优化版）
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    // ======================== 提取常量：提高可维护性 ========================
    /** Token请求头前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";
    /** Token请求头名称 */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    /** 用户ID请求域Key（统一命名） */
    private static final String USER_ID_ATTR = "userId";
    /** 用户名请求域Key */
    private static final String USERNAME_ATTR = "username";
    /** 跨域允许的Origin（可配置到application.yml，这里先固定） */
    private static final String ALLOWED_ORIGIN = "http://localhost:3000";

    /**
     * 请求处理前执行（核心：校验令牌）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 处理跨域预检请求（OPTIONS）：直接放行，避免拦截器阻断预检
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            setCorsHeaders(response); // 加跨域头
            return true;
        }

        // 2. 打印请求日志：方便排查问题（URL+请求方式）
        String requestUrl = request.getRequestURI();
        String requestMethod = request.getMethod();
        log.info("JWT拦截器校验请求：{} {}", requestMethod, requestUrl);

        // 3. 加跨域响应头（非OPTIONS请求也需要）
        setCorsHeaders(response);

        // 4. 从请求头获取Token
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        log.info("请求头[{}]内容：{}", AUTHORIZATION_HEADER, authHeader);

        // 5. 校验Token是否存在且格式正确
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            returnError(response, Result.custom(401, "请先登录，未携带有效令牌"));
            return false;
        }

        // 6. 提取纯Token（去掉Bearer前缀）
        String token = authHeader.substring(TOKEN_PREFIX.length()).trim();
//        log.info("提取的纯Token：{}", token);

        try {
            // 7. 解析Token（工具类需保证抛出具体异常）
            Claims claims = JwtUtils.parseToken(token);

            // 8. 统一存放用户信息到请求域（避免重复key）
            Long userId = ((Number) claims.get("id")).longValue();
            String username = claims.get("username").toString();
            request.setAttribute(USER_ID_ATTR, userId);
            request.setAttribute(USERNAME_ATTR, username);
//            log.info("Token解析成功，用户ID：{}，用户名：{}", userId, username);

            // 9. Token有效，放行
            return true;

            // ======================== 细分异常类型：返回精准提示 ========================
        } catch (ExpiredJwtException e) {
            log.error("Token过期 - 请求URL：{}，异常：{}", requestUrl, e.getMessage());
            returnError(response, Result.custom(401, "登录令牌已过期，请重新登录"));
        } catch (MalformedJwtException e) {
            log.error("Token格式错误 - 请求URL：{}，异常：{}", requestUrl, e.getMessage());
            returnError(response, Result.custom(401, "登录令牌格式错误，请重新登录"));
        } catch (SignatureException e) {
            log.error("Token签名错误 - 请求URL：{}，异常：{}", requestUrl, e.getMessage());
            returnError(response, Result.custom(401, "登录令牌签名无效，请重新登录"));
        } catch (UnsupportedJwtException e) {
            log.error("不支持的Token类型 - 请求URL：{}，异常：{}", requestUrl, e.getMessage());
            returnError(response, Result.custom(401, "不支持的登录令牌类型，请重新登录"));
        } catch (IllegalArgumentException e) {
            log.error("Token参数为空 - 请求URL：{}，异常：{}", requestUrl, e.getMessage());
            returnError(response, Result.custom(401, "登录令牌为空，请重新登录"));
        } catch (JwtException e) {
            log.error("Token解析失败（其他JWT异常） - 请求URL：{}，异常：{}", requestUrl, e.getMessage());
            returnError(response, Result.custom(401, "登录令牌无效，请重新登录"));
        } catch (Exception e) {
            log.error("Token校验未知异常 - 请求URL：{}，异常：{}", requestUrl, e.getMessage(), e);
            returnError(response, Result.custom(500, "服务器校验令牌时发生异常"));
        }
        return false;
    }

    // ======================== 封装工具方法：简化代码 ========================

    /**
     * 设置跨域响应头（统一管理）
     */
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Max-Age", "3600"); // 预检请求缓存1小时
    }

    /**
     * 统一返回错误结果（对齐Result类，用JSON工具序列化）
     */
    private void returnError(HttpServletResponse response, Result result) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        // 统一设置401状态码（未授权），500异常除外
        response.setStatus(result.getCode() == 500 ?
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR :
                HttpServletResponse.SC_UNAUTHORIZED);

        try (PrintWriter writer = response.getWriter()) {
            // 用FastJSON序列化Result，避免硬编码JSON（杜绝字段名错误）
            String json = JSON.toJSONString(result);
            log.info("返回错误响应：{}", json);
            writer.write(json);
            writer.flush();
        }
    }
}
