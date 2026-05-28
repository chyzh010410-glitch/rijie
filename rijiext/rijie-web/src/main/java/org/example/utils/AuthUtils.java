package org.example.utils;

import jakarta.servlet.http.HttpServletRequest;

/** 从JWT拦截器设置的请求属性中提取当前登录用户ID */
public class AuthUtils {

    private AuthUtils() {}

    public static Long getCurrentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("未登录或token无效");
        }
        return (Long) userId;
    }
}
