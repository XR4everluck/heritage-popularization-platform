package com.heritage.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求参数
 */
@Data
public class LoginDTO {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（明文传输由 HTTPS/部署环境保障，后端只存密文） */
    @NotBlank(message = "密码不能为空")
    private String password;
}
