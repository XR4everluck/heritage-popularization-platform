package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 系统用户实体（对应表 sys_user）
 *
 * <p>存储普通用户与管理员账号，role 字段区分角色。</p>
 */
@Data
@TableName("sys_user")
public class SysUser {

    /** 用户ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名（登录账号），唯一 */
    @TableField("username")
    private String username;

    /** 密码（BCrypt加密存储），序列化时不输出到前端 */
    @TableField("password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 用户昵称 */
    @TableField("nickname")
    private String nickname;

    /** 手机号 */
    @TableField("phone")
    private String phone;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 头像图片地址 */
    @TableField("avatar")
    private String avatar;

    /** 角色：user-普通用户，admin-系统管理员 */
    @TableField("role")
    private String role;

    /** 个人简介 */
    @TableField("introduction")
    private String introduction;

    /** 账号状态：1-正常，0-禁用 */
    @TableField("status")
    private Integer status;

    /** 累计积分 */
    @TableField("total_score")
    private Integer totalScore;

    /** 逻辑删除标记：0-未删除，1-已删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /** 创建时间（注册时间） */
    @TableField("create_time")
    private Date createTime;

    /** 更新时间 */
    @TableField("update_time")
    private Date updateTime;
}