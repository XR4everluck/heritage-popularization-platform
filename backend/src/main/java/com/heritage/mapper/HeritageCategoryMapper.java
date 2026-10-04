package com.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.entity.HeritageCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 非遗分类 Mapper：继承 BaseMapper，已内置单表 CRUD 与条件查询（无需 XML）
 */
@Mapper
public interface HeritageCategoryMapper extends BaseMapper<HeritageCategory> {
}
