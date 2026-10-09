package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 非遗冷知识实体（对应表 heritage_tip）
 *
 * <p>存储非遗趣味冷知识，可用于首页「今日非遗」板块展示；
 * heritageId 为空时表示通用冷知识（非特定项目相关）。</p>
 */
@Data
@TableName("heritage_tip")
public class HeritageTip {

    /** 冷知识ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 冷知识内容 */
    @TableField("content")
    private String content;

    /** 关联非遗项目ID（可空，空表示通用冷知识） */
    @TableField("heritage_id")
    private Long heritageId;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;
}
