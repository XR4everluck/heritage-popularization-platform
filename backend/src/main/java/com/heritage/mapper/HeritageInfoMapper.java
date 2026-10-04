package com.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.entity.HeritageInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 非遗项目 Mapper：继承 BaseMapper，已内置单表 CRUD 与条件查询（无需 XML）
 */
@Mapper
public interface HeritageInfoMapper extends BaseMapper<HeritageInfo> {
}
