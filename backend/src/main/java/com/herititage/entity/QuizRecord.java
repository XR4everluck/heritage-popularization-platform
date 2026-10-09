package com.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 非遗小测验答题记录实体
 */
@Data
@TableName("quiz_record")
public class QuizRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long questionId;

    private Integer isCorrect;

    private String answer;

    private LocalDateTime answerTime;

    @TableField(exist = false)
    private QuizQuestion question;
}