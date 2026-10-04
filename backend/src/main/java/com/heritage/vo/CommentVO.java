package com.heritage.vo;

import lombok.Data;

import java.util.Date;

/**
 * 评论视图对象：在评论字段基础上补充评论人昵称/头像与非遗名称
 */
@Data
public class CommentVO {

    /** 评论ID */
    private Long id;

    /** 非遗项目ID */
    private Long heritageId;

    /** 非遗名称（后台管理列表展示用） */
    private String heritageName;

    /** 评论用户ID */
    private Long userId;

    /** 评论人昵称 */
    private String nickname;

    /** 评论人头像 */
    private String avatar;

    /** 评论内容 */
    private String content;

    /** 点赞数 */
    private Integer likeCount;

    /** 状态：1-正常，0-屏蔽（前台仅展示正常） */
    private Integer status;

    /** 评论时间 */
    private Date createTime;
}
