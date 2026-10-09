package com.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.entity.QuizQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 非遗小测验题目数据访问层
 */
@Mapper
public interface QuizQuestionMapper extends BaseMapper<QuizQuestion> {
}