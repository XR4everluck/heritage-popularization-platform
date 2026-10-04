package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.Notice;
import com.heritage.mapper.NoticeMapper;
import com.heritage.service.NoticeService;
import org.springframework.stereotype.Service;

/**
 * 系统公告业务实现：继承 ServiceImpl 获得完整通用 CRUD 能力，业务方法在接口层阶段补充
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
}
