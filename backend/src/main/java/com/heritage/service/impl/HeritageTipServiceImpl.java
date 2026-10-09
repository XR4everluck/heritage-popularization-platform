package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.HeritageTip;
import com.heritage.mapper.HeritageTipMapper;
import com.heritage.service.HeritageTipService;
import org.springframework.stereotype.Service;

/**
 * 非遗冷知识业务实现：通用 CRUD + 随机获取
 */
@Service
public class HeritageTipServiceImpl extends ServiceImpl<HeritageTipMapper, HeritageTip> implements HeritageTipService {

    @Override
    public HeritageTip randomOne(Long heritageId) {
        return this.lambdaQuery()
                .eq(heritageId != null, HeritageTip::getHeritageId, heritageId)
                .last("ORDER BY RAND() LIMIT 1")
                .one();
    }
}
