package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.HeritageHistory;
import com.heritage.mapper.HeritageHistoryMapper;
import com.heritage.service.HeritageHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 非遗历史节点业务实现：纯 MyBatis-Plus 内置方法，无自定义 SQL
 */
@Service
@RequiredArgsConstructor
public class HeritageHistoryServiceImpl extends ServiceImpl<HeritageHistoryMapper, HeritageHistory>
        implements HeritageHistoryService {
}
