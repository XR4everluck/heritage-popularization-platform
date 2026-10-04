package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 非遗分类实体（对应表 heritage_category）
 *
 * <p>非遗项目的一级分类（传统音乐、传统技艺、民俗等），sort 字段控制前台导航顺序。</p>
 */
@Data
@TableName("heritage_category")
public class HeritageCategory {

    /** 分类ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称 */
    @TableField("name")
    private String name;

    /** 分类描述 */
    @TableField("description")
    private String description;

    /** 分类图标（图标名/图标图片地址） */
    @TableField("icon")
    private String icon;

    /** 排序号（数字越小越靠前） */
    @TableField("sort")
    private Integer sort;

    /** 逻辑删除标记：0-未删除，1-已删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;
}
