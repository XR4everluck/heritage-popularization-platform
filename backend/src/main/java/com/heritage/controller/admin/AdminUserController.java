package com.heritage.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.entity.SysUser;
import com.heritage.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台用户管理接口：分页查询用户列表、启用/禁用账号
 *
 * <p>用户列表不返回密码（实体 password 字段已配置序列化忽略）。</p>
 */
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final SysUserService sysUserService;

    /**
     * 分页查询用户列表
     *
     * @param keyword  用户名/昵称关键词（可空，模糊匹配）
     * @param status   账号状态筛选：1-正常，0-禁用（可空）
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 10
     */
    @GetMapping("/page")
    public Result<Page<SysUser>> page(@RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Integer status,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        qw.and(StrUtil.isNotBlank(keyword), w -> w.like(SysUser::getUsername, keyword)
                        .or().like(SysUser::getNickname, keyword))
                .eq(status != null, SysUser::getStatus, status)
                .orderByDesc(SysUser::getCreateTime);
        return Result.ok(sysUserService.page(new Page<>(page, pageSize), qw));
    }

    /**
     * 启用/禁用用户账号（禁用后该用户无法登录）
     *
     * @param id     用户ID
     * @param status 目标状态：1-启用，0-禁用
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser update = new SysUser();
        update.setId(id);
        update.setStatus(status);
        sysUserService.updateById(update);
        return Result.ok();
    }
}
