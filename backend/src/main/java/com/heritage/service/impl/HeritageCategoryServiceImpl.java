package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.HeritageCategory;
import com.heritage.mapper.HeritageCategoryMapper;
import com.heritage.service.HeritageCategoryService;
import org.springframework.stereotype.Service;

/**
 * 非遗分类业务实现：继承 ServiceImpl 获得完整通用 CRUD 能力，业务方法在接口层阶段补充
 */
@Service
public class HeritageCategoryServiceImpl extends ServiceImpl<HeritageCategoryMapper, HeritageCategory> implements HeritageCategoryService {
}
