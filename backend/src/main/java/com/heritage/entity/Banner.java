package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 轮播图实体（对应表 banner）
 *
 * <p>前台首页轮播图配置，sort 控制展示顺序，status 控制启用/停用。</p>
 */
@Data
@TableName("banner")
public class Banner {

    /** 轮播图ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 图片地址 */
    @TableField("image")
    private String image;

    /** 点击跳转链接（前端路由或外部地址） */
    @TableField("link_url")
    private String linkUrl;

    /** 标题 */
    @TableField("title")
    private String title;

    /** 排序号（数字越小越靠前） */
    @TableField("sort")
    private Integer sort;

    /** 状态：1-启用，0-停用 */
    @TableField("status")
    private Integer status;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;
}
