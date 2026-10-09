package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.Inheritor;

import java.util.List;

/**
 * 非遗传承人Service
 */
public interface InheritorService extends IService<Inheritor> {
    /**
     * 根据传承人ID查询关联的非遗项目
     */
    List<HeritageInfo> getHeritageByInheritorId(Long inheritorId);

    /**
     * 根据传承人ID查询传承人详情
     */
    Inheritor getInheritorWithHeritage(Long inheritorId);
}