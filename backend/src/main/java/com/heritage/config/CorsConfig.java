package com.heritage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 *
 * <p>前后端分离架构下，允许前端开发服务器（如 Vite 默认 http://localhost:5173）
 * 跨域访问后端接口。生产环境建议将 allowedOriginPatterns 收紧为具体的前端域名。</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Spring 5.3+ 中 allowCredentials(true) 不再允许搭配 allowedOrigins("*")，
                // 需使用 allowedOriginPatterns
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
