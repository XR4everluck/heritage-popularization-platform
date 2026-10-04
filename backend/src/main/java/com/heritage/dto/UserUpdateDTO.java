package com.heritage.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 修改个人资料请求参数（全部可选，仅更新传入的字段）
 *
 * <p>不包含 role/status 等敏感字段，防止越权修改。</p>
 */
@Data
public class UserUpdateDTO {

    /** 昵称 */
    @Size(max = 50, message = "昵称最长50个字符")
    private String nickname;

    /** 手机号 */
    @Size(max = 20, message = "手机号最长20个字符")
    private String phone;

    /** 邮箱 */
    @Size(max = 100, message = "邮箱最长100个字符")
    private String email;

    /** 头像图片地址 */
    @Size(max = 255, message = "头像地址过长")
    private String avatar;

    /** 个人简介 */
    @Size(max = 500, message = "个人简介最长500个字符")
    private String introduction;
}
