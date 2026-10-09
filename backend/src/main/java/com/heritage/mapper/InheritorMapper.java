package com.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.entity.Inheritor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 传承人信息数据访问层
 */
@Mapper
public interface InheritorMapper extends BaseMapper<Inheritor> {
}