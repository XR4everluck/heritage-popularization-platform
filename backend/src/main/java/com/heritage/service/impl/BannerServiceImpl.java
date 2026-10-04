package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.Banner;
import com.heritage.mapper.BannerMapper;
import com.heritage.service.BannerService;
import org.springframework.stereotype.Service;

/**
 * 轮播图业务实现：继承 ServiceImpl 获得完整通用 CRUD 能力，业务方法在接口层阶段补充
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {
}
