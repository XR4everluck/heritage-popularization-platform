package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 非遗项目实体（对应表 heritage_info）
 *
 * <p>平台核心内容表，存储非遗项目的级别、地区、传承人、图文详情等；
 * categoryId 为业务层关联 heritage_category.id 的逻辑外键。</p>
 */
@Data
@TableName("heritage_info")
public class HeritageInfo {

    /** 非遗项目ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类ID（业务层关联 heritage_category.id） */
    @TableField("category_id")
    private Long categoryId;

    /** 非遗名称 */
    @TableField("name")
    private String name;

    /** 非遗级别：国家级/省级/市级 */
    @TableField("level")
    private String level;

    /** 所属地区 */
    @TableField("region")
    private String region;

    /** 代表性传承人（群体传承可填"群体传承"） */
    @TableField("inheritor")
    private String inheritor;

    /** 非遗简介（列表页摘要展示） */
    @TableField("summary")
    private String summary;

    /** 详细内容（富文本图文详情） */
    @TableField("content")
    private String content;

    /** 封面图片地址 */
    @TableField("cover_image")
    private String coverImage;

    /** 浏览量（冗余统计字段，业务层累加） */
    @TableField("view_count")
    private Integer viewCount;

    /** 收藏数（冗余统计字段，业务层维护） */
    @TableField("collection_count")
    private Integer collectionCount;

    /** 发布时间（NULL 表示未发布/草稿） */
    @TableField("publish_time")
    private Date publishTime;

    /** 更新时间 */
    @TableField("update_time")
    private Date updateTime;

    /** 逻辑删除标记：0-未删除，1-已删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;
}
