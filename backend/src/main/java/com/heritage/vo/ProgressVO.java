package com.heritage.vo;

import lombok.Data;

import java.util.Date;

/**
 * 学习进度视图对象：进度记录 + 课程名称 + 章节标题
 */
@Data
public class ProgressVO {

    /** 学习进度ID */
    private Long id;

    /** 课程ID */
    private Long courseId;

    /** 课程名称 */
    private String courseName;

    /** 章节ID */
    private Long chapterId;

    /** 章节标题 */
    private String chapterTitle;

    /** 累计学习时长（分钟） */
    private Integer studyDuration;

    /** 完成状态：0-未完成，1-已完成 */
    private Integer finished;

    /** 最近学习时间 */
    private Date updateTime;
}
