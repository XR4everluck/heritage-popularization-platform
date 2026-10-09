package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 非遗小测验题目表
 */
@Data
@TableName("quiz_question")
public class QuizQuestion {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目内容
     */
    private String question;

    /**
     * 选项A
     */
    private String optionA;

    /**
     * 选项B
     */
    private String optionB;

    /**
     * 选项C
     */
    private String optionC;

    /**
     * 选项D
     */
    private String optionD;

    /**
     * 正确答案：A/B/C/D
     */
    private String correctAnswer;

    /**
     * 题目解析
     */
    private String analysis;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 关联的非遗项目ID（外键关联heritage_info表）
     */
    private Long heritageId;

    /**
     * 非遗项目名称（冗余字段，用于列表展示）
     */
    private String heritageName;

    /**
     * 题目类型：1-单选题，2-多选题，3-判断题
     */
    private Integer questionType;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序序号
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    private Integer deleted;
}