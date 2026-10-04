package com.heritage.vo;

import lombok.Data;

import java.util.Date;

/**
 * 我的收藏视图对象：收藏记录 + 非遗项目展示信息
 */
@Data
public class CollectionVO {

    /** 收藏ID */
    private Long id;

    /** 非遗项目ID */
    private Long heritageId;

    /** 非遗名称 */
    private String name;

    /** 非遗封面 */
    private String coverImage;

    /** 非遗级别：国家级/省级/市级 */
    private String level;

    /** 所属地区 */
    private String region;

    /** 收藏时间 */
    private Date collectionTime;
}
