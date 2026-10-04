package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.SysUser;
import com.heritage.mapper.SysUserMapper;
import com.heritage.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 系统用户业务实现：继承 ServiceImpl 获得完整通用 CRUD 能力，业务方法在接口层阶段补充
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
