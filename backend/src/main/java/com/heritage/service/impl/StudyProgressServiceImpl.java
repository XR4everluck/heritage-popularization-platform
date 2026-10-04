package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.StudyProgress;
import com.heritage.mapper.StudyProgressMapper;
import com.heritage.service.StudyProgressService;
import org.springframework.stereotype.Service;

/**
 * 学习进度业务实现：继承 ServiceImpl 获得完整通用 CRUD 能力，业务方法在接口层阶段补充
 */
@Service
public class StudyProgressServiceImpl extends ServiceImpl<StudyProgressMapper, StudyProgress> implements StudyProgressService {
}
