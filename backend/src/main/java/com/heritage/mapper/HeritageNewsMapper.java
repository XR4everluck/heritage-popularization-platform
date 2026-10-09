package com.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.entity.HeritageNews;
import org.apache.ibatis.annotations.Mapper;

/**
 * 非遗科普快讯数据访问层
 */
@Mapper
public interface HeritageNewsMapper extends BaseMapper<HeritageNews> {
}