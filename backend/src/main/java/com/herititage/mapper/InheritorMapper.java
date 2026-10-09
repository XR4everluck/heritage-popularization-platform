package com.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.entity.Inheritor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 非遗传承人Mapper
 */
@Mapper
public interface InheritorMapper extends BaseMapper<Inheritor> {
}