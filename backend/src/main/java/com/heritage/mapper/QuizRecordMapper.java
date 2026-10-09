package com.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.entity.QuizRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 非遗小测验答题记录Mapper
 */
@Mapper
public interface QuizRecordMapper extends BaseMapper<QuizRecord> {
}