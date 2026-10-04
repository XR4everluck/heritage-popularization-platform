package com.heritage.vo;

import com.heritage.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果视图对象：JWT 令牌 + 用户信息（密码字段不序列化）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    /** JWT 令牌，后续请求携带于 Authorization: Bearer {token} */
    private String token;

    /** 用户信息（含角色，供前端路由与展示） */
    private SysUser user;
}
