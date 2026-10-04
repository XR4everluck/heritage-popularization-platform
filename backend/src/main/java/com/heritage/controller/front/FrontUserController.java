package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.dto.LoginDTO;
import com.heritage.dto.RegisterDTO;
import com.heritage.dto.UserUpdateDTO;
import com.heritage.entity.SysUser;
import com.heritage.service.SysUserService;
import com.heritage.util.AuthContext;
import com.heritage.util.JwtUtil;
import com.heritage.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 前台用户接口：注册、登录（返回 JWT）、个人信息查询与修改
 *
 * <p>注册/登录为公开接口；查询与修改个人资料需携带 Authorization: Bearer {token}，
 * 用户身份由拦截器解析 token 后写入请求属性，控制器经 AuthContext 获取（前端无需再传 userId）。</p>
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class FrontUserController {

    private final SysUserService sysUserService;

    private final JwtUtil jwtUtil;

    /**
     * 注册（无需登录）
     *
     * @param dto 用户名、密码、昵称（昵称可空）
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO dto) {
        sysUserService.register(dto);
        return Result.ok();
    }

    /**
     * 登录（无需登录），成功返回 JWT 令牌与用户信息（含角色，密码不回传）
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO dto) {
        SysUser user = sysUserService.login(dto);
        return Result.ok(new LoginVO(jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole()), user));
    }

    /**
     * 查询个人信息（需登录，用户ID取自 token）
     */
    @GetMapping("/profile")
    public Result<SysUser> profile(HttpServletRequest request) {
        return Result.ok(sysUserService.getById(AuthContext.getUserId(request)));
    }

    /**
     * 修改个人信息（需登录，仅更新传入字段：昵称/手机号/邮箱/头像/简介）
     *
     * @param dto 资料字段（均为可选）
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Validated @RequestBody UserUpdateDTO dto, HttpServletRequest request) {
        sysUserService.updateProfile(AuthContext.getUserId(request), dto);
        return Result.ok();
    }
}
