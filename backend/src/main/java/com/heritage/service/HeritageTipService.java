package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.HeritageTip;

/**
 * 非遗冷知识业务接口：继承 IService 获得通用 CRUD，另含随机获取方法
 */
public interface HeritageTipService extends IService<HeritageTip> {

    /**
     * 随机获取一条冷知识
     *
     * @param heritageId 非遗项目ID（可空，空时从全部冷知识中随机）
     * @return 随机一条冷知识，无数据时返回 null
     */
    HeritageTip randomOne(Long heritageId);
}
