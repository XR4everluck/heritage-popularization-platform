package com.heritage.util;

import com.heritage.interceptor.JwtInterceptor;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证上下文工具：从请求属性中获取 JWT 拦截器解析出的当前登录用户信息
 *
 * <p>仅在拦截器覆盖的受保护接口中可用；公开接口中返回 null。</p>
 */
public class AuthContext {

    private AuthContext() {
    }

    /** 当前登录用户ID（来自 token，可信） */
    public static Long getUserId(HttpServletRequest request) {
        Object value = request.getAttribute(JwtInterceptor.ATTR_USER_ID);
        return value == null ? null : (Long) value;
    }

    /** 当前登录用户角色：user / admin */
    public static String getRole(HttpServletRequest request) {
        Object value = request.getAttribute(JwtInterceptor.ATTR_ROLE);
        return value == null ? null : (String) value;
    }
}
