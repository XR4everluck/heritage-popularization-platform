package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 非遗科普快讯表
 */
@Data
@TableName("heritage_news")
public class HeritageNews {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 快讯标题
     */
    private String title;

    /**
     * 快讯内容
     */
    private String content;

    /**
     * 关联的非遗项目ID（外键关联heritage_info表）
     */
    private Long heritageId;

    /**
     * 非遗项目名称（冗余字段，用于列表展示）
     */
    private String heritageName;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 是否置顶：0-不置顶，1-置顶
     */
    private Integer isTop;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序序号
     */
    private Integer sort;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

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