package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 课程实体（对应表 course）
 *
 * <p>围绕某个非遗项目开设的教学课程；
 * heritageId 为业务层关联 heritage_info.id 的逻辑外键。</p>
 */
@Data
@TableName("course")
public class Course {

    /** 课程ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联非遗项目ID（业务层关联 heritage_info.id） */
    @TableField("heritage_id")
    private Long heritageId;

    /** 课程名称 */
    @TableField("name")
    private String name;

    /** 课程简介 */
    @TableField("summary")
    private String summary;

    /** 课程封面图片地址 */
    @TableField("cover")
    private String cover;

    /** 讲师 */
    @TableField("teacher")
    private String teacher;

    /** 总时长（单位：分钟） */
    @TableField("duration")
    private Integer duration;

    /** 浏览量（冗余统计字段，业务层累加） */
    @TableField("view_count")
    private Integer viewCount;

    /** 发布时间（NULL 表示未发布/草稿） */
    @TableField("publish_time")
    private Date publishTime;

    /** 逻辑删除标记：0-未删除，1-已删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;
}
