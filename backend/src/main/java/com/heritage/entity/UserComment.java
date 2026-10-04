package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 评论实体（对应表 user_comment）
 *
 * <p>用户对非遗项目的评论，管理员可屏蔽（status=0）违规评论。</p>
 */
@Data
@TableName("user_comment")
public class UserComment {

    /** 评论ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评论用户ID（业务层关联 sys_user.id） */
    @TableField("user_id")
    private Long userId;

    /** 非遗项目ID（业务层关联 heritage_info.id） */
    @TableField("heritage_id")
    private Long heritageId;

    /** 评论内容 */
    @TableField("content")
    private String content;

    /** 点赞数（冗余统计字段，业务层维护） */
    @TableField("like_count")
    private Integer likeCount;

    /** 状态：1-正常，0-屏蔽（管理员审核后屏蔽） */
    @TableField("status")
    private Integer status;

    /** 逻辑删除标记：0-未删除，1-已删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /** 评论时间 */
    @TableField("create_time")
    private Date createTime;
}
