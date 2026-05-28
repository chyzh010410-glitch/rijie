package org.example.config;

import jakarta.annotation.Resource;
import org.example.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Spring MVC配置类：注册拦截器
 */
@Configuration // 标识为配置类
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns("/api/user/login","/api/user/register"); // 排除登录接口（无需令牌）
    }

    /**
     * 配置跨域（新增核心方法）
     * 此方式比CorsFilter更适配WebMvc拦截器体系，避免冲突
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有路径生效
                // 允许前端源（必须是前端实际运行地址，不能用*，否则无法配合withCredentials）
                .allowedOrigins("http://localhost:3000")
                // 允许所有HTTP方法（GET/POST/PUT/DELETE等）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有请求头（包括Token、Content-Type等）
                .allowedHeaders("*")
                // 允许携带Cookie（和前端withCredentials: true对应，必须开启）
                .allowCredentials(true)
                // 预检请求（OPTIONS）缓存时间，减少请求次数（单位：秒）
                .maxAge(3600);
    }
}