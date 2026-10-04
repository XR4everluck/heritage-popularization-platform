package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.CourseChapter;
import com.heritage.mapper.CourseChapterMapper;
import com.heritage.service.CourseChapterService;
import org.springframework.stereotype.Service;

/**
 * 课程章节业务实现：继承 ServiceImpl 获得完整通用 CRUD 能力，业务方法在接口层阶段补充
 */
@Service
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMapper, CourseChapter> implements CourseChapterService {
}
