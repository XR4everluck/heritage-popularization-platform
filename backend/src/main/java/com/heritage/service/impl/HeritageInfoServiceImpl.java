package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.HeritageInfo;
import com.heritage.mapper.HeritageInfoMapper;
import com.heritage.service.HeritageInfoService;
import org.springframework.stereotype.Service;

/**
 * 非遗项目业务实现：继承 ServiceImpl 获得完整通用 CRUD 能力，业务方法在接口层阶段补充
 */
@Service
public class HeritageInfoServiceImpl extends ServiceImpl<HeritageInfoMapper, HeritageInfo> implements HeritageInfoService {
}
