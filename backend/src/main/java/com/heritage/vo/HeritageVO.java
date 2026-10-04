package com.heritage.vo;

import com.heritage.entity.HeritageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 非遗项目视图对象：在实体基础上补充分类名称，供前端列表/详情展示
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HeritageVO extends HeritageInfo {

    /** 分类名称（业务层关联查询组装） */
    private String categoryName;
}
