package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.UserCollection;
import com.heritage.mapper.UserCollectionMapper;
import com.heritage.service.UserCollectionService;
import org.springframework.stereotype.Service;

/**
 * 用户收藏业务实现：继承 ServiceImpl 获得完整通用 CRUD 能力，业务方法在接口层阶段补充
 */
@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService {
}
