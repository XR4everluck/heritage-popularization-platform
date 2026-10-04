package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统公告实体（对应表 notice）
 *
 * <p>平台公告的发布与下架管理，status 控制前台可见性。</p>
 */
@Data
@TableName("notice")
public class Notice {

    /** 公告ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 公告标题 */
    @TableField("title")
    private String title;

    /** 公告内容 */
    @TableField("content")
    private String content;

    /** 发布时间（NULL 表示未发布/草稿） */
    @TableField("publish_time")
    private Date publishTime;

    /** 状态：1-已发布，0-下架/草稿 */
    @TableField("status")
    private Integer status;

    /** 逻辑删除标记：0-未删除，1-已删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;
}
