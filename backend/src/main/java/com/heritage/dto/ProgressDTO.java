package com.heritage.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新学习进度请求参数（按"用户+章节"维度 upsert：存在则累计，不存在则新建）
 */
@Data
public class ProgressDTO {

    /** 章节ID（必填，courseId 以章节记录为准，传入仅用于一致性校验） */
    @NotNull(message = "章节ID不能为空")
    private Long chapterId;

    /** 课程ID（可空；传入时校验与章节所属课程一致） */
    private Long courseId;

    /** 本次学习时长增量（单位：分钟，可空默认0） */
    private Integer studyDuration;

    /** 完成状态：0-未完成，1-已完成（可空默认保持原状态） */
    private Integer finished;
}
