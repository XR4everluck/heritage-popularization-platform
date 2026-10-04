package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.dto.LoginDTO;
import com.heritage.dto.RegisterDTO;
import com.heritage.dto.UserUpdateDTO;
import com.heritage.entity.SysUser;
import com.heritage.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台用户接口：注册、登录、个人资料查询与修改
 *
 * <p>说明：阶段4暂不接入 JWT 鉴权，用户身份由前端传 userId 参数（调试模式），
 * 后续接入 JWT 后改为从登录态解析，接口路径与响应结构保持不变。</p>
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class FrontUserController {

    private final SysUserService sysUserService;

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
     * 登录（无需登录），成功返回用户信息（含角色，密码不回传）
     */
    @PostMapping("/login")
    public Result<SysUser> login(@Validated @RequestBody LoginDTO dto) {
        return Result.ok(sysUserService.login(dto));
    }

    /**
     * 查询个人信息
     *
     * @param userId 用户ID（调试模式，后续由登录态解析）
     */
    @GetMapping("/profile")
    public Result<SysUser> profile(@RequestParam Long userId) {
        return Result.ok(sysUserService.getById(userId));
    }

    /**
     * 修改个人信息（仅更新传入字段，昵称/手机号/邮箱/头像/简介）
     *
     * @param userId 用户ID（调试模式，后续由登录态解析）
     * @param dto    资料字段（均为可选）
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestParam Long userId, @Validated @RequestBody UserUpdateDTO dto) {
        sysUserService.updateProfile(userId, dto);
        return Result.ok();
    }
}
