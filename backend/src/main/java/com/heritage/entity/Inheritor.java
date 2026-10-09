package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 传承人信息表
 */
@Data
@TableName("inheritor")
public class Inheritor {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 传承人姓名
     */
    private String name;

    /**
     * 传承人标题/头衔
     */
    private String title;

    /**
     * 传承人头像图片URL
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 传承经历
     */
    private String experience;

    /**
     * 荣誉成就
     */
    private String achievements;

    /**
     * 标签（多个标签用逗号分隔）
     */
    private String tags;

    /**
     * 关联的非遗项目ID（外键关联heritage_info表）
     */
    private Long heritageId;

    /**
     * 非遗项目名称（冗余字段，用于列表展示）
     */
    private String heritageName;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序序号
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    private Integer deleted;
}