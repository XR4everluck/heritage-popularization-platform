package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户内容发布实体（对应表 user_submission）
 *
 * <p>用户提交科普快讯/科普视频（B站外链），管理员审核通过（status=1）后前台展示。</p>
 */
@Data
@TableName("user_submission")
public class UserSubmission {

    /** 发布ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 提交用户ID（关联 sys_user.id） */
    @TableField("user_id")
    private Long userId;

    /** 类型：news-科普快讯，course-科普视频 */
    @TableField("type")
    private String type;

    /** 标题 */
    @TableField("title")
    private String title;

    /** 关联非遗项目ID（可空） */
    @TableField("heritage_id")
    private Long heritageId;

    /** 快讯正文 / 视频简介 */
    @TableField("content")
    private String content;

    /** 视频链接（B站视频页或检索页） */
    @TableField("video_url")
    private String videoUrl;

    /** 状态：0-待审核，1-已通过，2-未通过 */
    @TableField("status")
    private Integer status;

    /** 审核意见（驳回原因） */
    @TableField("review_note")
    private String reviewNote;

    /** 逻辑删除标记 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /** 提交时间 */
    @TableField("submit_time")
    private Date submitTime;

    /** 审核时间 */
    @TableField("review_time")
    private Date reviewTime;
}
