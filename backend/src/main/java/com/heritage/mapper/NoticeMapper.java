package com.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统公告 Mapper：继承 BaseMapper，已内置单表 CRUD 与条件查询（无需 XML）
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
