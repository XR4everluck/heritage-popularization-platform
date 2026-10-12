package com.heritage.dto;

import lombok.Data;

/**
 * 用户内容发布入参（科普快讯 / 科普视频）
 */
@Data
public class SubmissionDTO {

    /** 类型：news-科普快讯，course-科普视频 */
    private String type;

    /** 标题 */
    private String title;

    /** 关联非遗项目ID（可空） */
    private Long heritageId;

    /** 快讯正文 / 视频简介 */
    private String content;

    /** 视频链接（B站视频页或检索页） */
    private String videoUrl;
}
