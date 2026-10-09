package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 课程章节实体（对应表 course_chapter）
 *
 * <p>课程的视频章节，作为课程从属数据采用物理删除（无 deleted 字段）；
 * courseId 为业务层关联 course.id 的逻辑外键。</p>
 */
@Data
@TableName("course_chapter")
public class CourseChapter {

    /** 章节ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 课程ID（业务层关联 course.id） */
    @TableField("course_id")
    private Long courseId;

    /** 章节标题 */
    @TableField("title")
    private String title;

    /** 视频地址（文件上传后的访问路径） */
    @TableField("video_url")
    private String videoUrl;

    /** 章节内容（图文讲义） */
    @TableField("content")
    private String content;

    /** 排序号（数字越小越靠前） */
    @TableField("sort")
    private Integer sort;

    /** 内容形态：video(视频)、article(图文)、audio(音频) */
    @TableField("content_type")
    private String contentType;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;
}
