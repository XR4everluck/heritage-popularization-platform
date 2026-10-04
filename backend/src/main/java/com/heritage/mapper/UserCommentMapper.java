package com.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.entity.UserComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论 Mapper：继承 BaseMapper，已内置单表 CRUD 与条件查询（无需 XML）
 */
@Mapper
public interface UserCommentMapper extends BaseMapper<UserComment> {
}
