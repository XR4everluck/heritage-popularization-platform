package com.heritage.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.common.ResultCode;
import com.heritage.dto.LoginDTO;
import com.heritage.dto.RegisterDTO;
import com.heritage.dto.UserUpdateDTO;
import com.heritage.entity.SysUser;
import com.heritage.exception.BusinessException;
import com.heritage.mapper.SysUserMapper;
import com.heritage.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 系统用户业务实现：注册/登录/资料维护；密码使用 Hutool BCrypt 加密与校验
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /** 普通用户角色标识（与 sys_user.role 取值约定一致） */
    private static final String ROLE_USER = "user";

    @Override
    public void register(RegisterDTO dto) {
        boolean exists = this.lambdaQuery().eq(SysUser::getUsername, dto.getUsername()).exists();
        if (exists) {
            throw new BusinessException("用户名已被注册");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        // BCrypt 加密存储，数据库中不出现明文密码
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(StrUtil.isBlank(dto.getNickname()) ? dto.getUsername() : dto.getNickname());
        user.setRole(ROLE_USER);
        user.setStatus(1);
        try {
            this.save(user);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 并发注册同名用户时由 username 唯一索引兜底，转换为友好提示
            throw new BusinessException("用户名已被注册，请更换用户名");
        }
    }

    @Override
    public SysUser login(LoginDTO dto) {
        SysUser user = this.lambdaQuery().eq(SysUser::getUsername, dto.getUsername()).one();
        // 统一提示，避免暴露"账号是否存在"
        if (user == null || !BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        return user;
    }

    @Override
    public void updateProfile(Long userId, UserUpdateDTO dto) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        // 仅更新 DTO 携带的字段（updateById 自动忽略 null 字段），不触碰 role/status/deleted
        SysUser update = new SysUser();
        update.setId(userId);
        update.setNickname(dto.getNickname());
        update.setPhone(dto.getPhone());
        update.setEmail(dto.getEmail());
        update.setAvatar(dto.getAvatar());
        update.setIntroduction(dto.getIntroduction());
        this.updateById(update);
    }
}
