package com.heritage.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.SysUser;
import com.heritage.service.SysUserService;
import com.heritage.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 登录拦截器
 *
 * <p>拦截范围与放行路径见 WebMvcConfig（公开接口已在注册时排除）。
 * 校验流程：Authorization 请求头中的 Bearer token → 签名/过期校验 →
 * 用户存在且状态正常校验 → 管理员接口额外校验 admin 角色；
 * 校验通过后将 userId/role 写入请求属性，供控制器通过 AuthContext 获取。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    /** 请求属性键：当前登录用户ID */
    public static final String ATTR_USER_ID = "heritageUserId";

    /** 请求属性键：当前登录用户角色 */
    public static final String ATTR_ROLE = "heritageRole";

    private final JwtUtil jwtUtil;

    private final SysUserService sysUserService;

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return reject(response, ResultCode.UNAUTHORIZED, "未登录或缺少令牌，请先登录");
        }
        String token = auth.substring(7).trim();

        Claims claims;
        try {
            claims = jwtUtil.parse(token);
        } catch (JwtException | IllegalArgumentException e) {
            return reject(response, ResultCode.UNAUTHORIZED, "登录已过期或令牌无效，请重新登录");
        }

        Long userId = Long.valueOf(claims.getSubject());
        String role = claims.get("role", String.class);

        // 实时校验账号状态：禁用/注销的用户即使持有未过期 token 也立即失效
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return reject(response, ResultCode.UNAUTHORIZED, "账号不存在或已注销，请重新登录");
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            return reject(response, ResultCode.UNAUTHORIZED, "账号已被禁用，请联系管理员");
        }

        // 管理员接口仅 admin 角色可访问
        if (request.getRequestURI().startsWith("/api/admin") && !"admin".equals(role)) {
            return reject(response, ResultCode.FORBIDDEN, "无权限访问管理员接口");
        }

        request.setAttribute(ATTR_USER_ID, userId);
        request.setAttribute(ATTR_ROLE, role);
        return true;
    }

    /** 以统一 Result 结构返回认证/权限错误（拦截器不经过全局异常处理器，需手动写出 JSON） */
    private boolean reject(HttpServletResponse response, ResultCode code, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), Result.error(code.getCode(), msg));
        return false;
    }
}
