package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.UserComment;
import com.heritage.mapper.UserCommentMapper;
import com.heritage.service.UserCommentService;
import org.springframework.stereotype.Service;

/**
 * 评论业务实现：继承 ServiceImpl 获得完整通用 CRUD 能力，业务方法在接口层阶段补充
 */
@Service
public class UserCommentServiceImpl extends ServiceImpl<UserCommentMapper, UserComment> implements UserCommentService {
}
