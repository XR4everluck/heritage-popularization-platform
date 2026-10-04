package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 学习进度实体（对应表 study_progress）
 *
 * <p>用户学习课程章节的进度记录，(userId, chapterId) 联合唯一索引保证"一人一章仅一条进度"；
 * 无 deleted 字段（进度重置即物理删除）。</p>
 */
@Data
@TableName("study_progress")
public class StudyProgress {

    /** 学习进度ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（业务层关联 sys_user.id） */
    @TableField("user_id")
    private Long userId;

    /** 课程ID（业务层关联 course.id，冗余便于按课程统计） */
    @TableField("course_id")
    private Long courseId;

    /** 章节ID（业务层关联 course_chapter.id） */
    @TableField("chapter_id")
    private Long chapterId;

    /** 学习时长（单位：分钟，累计值） */
    @TableField("study_duration")
    private Integer studyDuration;

    /** 完成状态：0-未完成，1-已完成 */
    @TableField("finished")
    private Integer finished;

    /** 更新时间（最近学习时间） */
    @TableField("update_time")
    private Date updateTime;
}
