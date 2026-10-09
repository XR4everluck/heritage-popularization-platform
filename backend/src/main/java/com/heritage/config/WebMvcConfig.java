package com.heritage.config;

import com.heritage.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Web MVC 配置：注册 JWT 拦截器（含公开接口白名单）、上传文件静态资源映射
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /** 上传文件存储目录（与 heritage.upload.path 一致） */
    @Value("${heritage.upload.path}")
    private String uploadPath;

    /**
     * JWT 拦截器：默认拦截所有 /api/** 接口，白名单（无需登录）如下——
     * 注册/登录、分类、非遗、科普快讯、课程、传承人、门户（轮播图/公告）、评论分页、自检接口、静态文件。
     * 其余接口均需携带 Authorization: Bearer {token}；/api/admin/** 额外要求 admin 角色。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/register",
                        "/api/user/login",
                        "/api/category/**",
                        "/api/heritage/**",
                        "/api/heritage-news/**",
                        "/api/course/**",
                        "/api/inheritor/**",
                        "/api/portal/**",
                        "/api/comment/page",
                        "/api/hello/**",
                        "/files/**");
    }

    /**
     * 上传文件静态资源映射：http://host:8080/files/** -> 本地 heritage.upload.path 目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadPath).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
