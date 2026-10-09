package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 非遗传承人实体
 */
@Data
@TableName("inheritor")
public class Inheritor {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String avatar;

    private String biography;

    private String representativeWorks;

    private String inheritanceRelation;

    private String region;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<HeritageInfo> heritageList;
}