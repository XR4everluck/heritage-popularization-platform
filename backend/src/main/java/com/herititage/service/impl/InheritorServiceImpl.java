package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.HeritageInfo;
import com.heritage.entity.Inheritor;
import com.heritage.mapper.InheritorMapper;
import com.heritage.service.HeritageInfoService;
import com.heritage.service.InheritorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 非遗传承人Service实现
 */
@Service
@RequiredArgsConstructor
public class InheritorServiceImpl extends ServiceImpl<InheritorMapper, Inheritor> implements InheritorService {

    private final HeritageInfoService heritageInfoService;

    @Override
    public List<HeritageInfo> getHeritageByInheritorId(Long inheritorId) {
        return heritageInfoService.lambdaQuery()
                .eq(HeritageInfo::getInheritorId, inheritorId)
                .list();
    }

    @Override
    public Inheritor getInheritorWithHeritage(Long inheritorId) {
        Inheritor inheritor = this.getById(inheritorId);
        if (inheritor != null) {
            inheritor.setHeritageList(getHeritageByInheritorId(inheritorId));
        }
        return inheritor;
    }
}