package com.heritage.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 注册请求参数
 */
@Data
public class RegisterDTO {

    /** 用户名：3-50位字母、数字或下划线 */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{3,50}$", message = "用户名只能是3-50位字母、数字或下划线")
    private String username;

    /** 密码：6-100位 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度需在6-100位之间")
    private String password;

    /** 昵称（可空，默认取用户名） */
    @Size(max = 50, message = "昵称最长50个字符")
    private String nickname;
}
