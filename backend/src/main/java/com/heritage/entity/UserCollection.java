package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户收藏实体（对应表 user_collection）
 *
 * <p>用户与非遗项目"多对多"收藏联系的关联表，(userId, heritageId) 有联合唯一索引防重复收藏；
 * 取消收藏即物理删除记录，故无 deleted 字段。</p>
 */
@Data
@TableName("user_collection")
public class UserCollection {

    /** 收藏ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（业务层关联 sys_user.id） */
    @TableField("user_id")
    private Long userId;

    /** 非遗项目ID（业务层关联 heritage_info.id） */
    @TableField("heritage_id")
    private Long heritageId;

    /** 收藏时间 */
    @TableField("create_time")
    private Date createTime;
}
