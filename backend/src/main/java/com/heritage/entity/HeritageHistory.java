package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 非遗历史节点实体（对应表 heritage_history）
 *
 * <p>记录非遗项目的发展历程节点，作为课程从属型内容数据采用物理删除；
 * 种子数据按年代先后顺序插入，展示时按 id 升序即时间顺序。</p>
 */
@Data
@TableName("heritage_history")
public class HeritageHistory {

    /** 历史节点ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 非遗项目ID（业务层关联 heritage_info.id） */
    @TableField("heritage_id")
    private Long heritageId;

    /** 年代（如"唐代""1955年"，古代项目可填时期名称） */
    @TableField("year")
    private String year;

    /** 事件标题 */
    @TableField("event")
    private String event;

    /** 事件描述 */
    @TableField("description")
    private String description;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;
}
