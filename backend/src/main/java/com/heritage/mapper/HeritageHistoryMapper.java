package com.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.entity.HeritageHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 非遗历史节点 Mapper：继承 BaseMapper，已内置单表 CRUD（无需 XML）
 */
@Mapper
public interface HeritageHistoryMapper extends BaseMapper<HeritageHistory> {
}
