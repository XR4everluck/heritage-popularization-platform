package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.dto.LoginDTO;
import com.heritage.dto.RegisterDTO;
import com.heritage.dto.UserUpdateDTO;
import com.heritage.entity.SysUser;

/**
 * 系统用户业务接口：继承 IService 获得通用 CRUD，另含注册/登录/资料维护业务方法
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 注册：校验用户名唯一，密码 BCrypt 加密后入库，默认角色 user、状态正常
     */
    void register(RegisterDTO dto);

    /**
     * 登录：校验密码（BCrypt）与账号状态，成功返回用户信息（含角色，供前端路由）
     */
    SysUser login(LoginDTO dto);

    /**
     * 修改个人资料：仅更新 DTO 中传入的字段，不影响角色/状态等敏感字段
     */
    void updateProfile(Long userId, UserUpdateDTO dto);
}
