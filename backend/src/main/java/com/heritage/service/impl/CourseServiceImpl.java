package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.common.ResultCode;
import com.heritage.entity.Course;
import com.heritage.exception.BusinessException;
import com.heritage.mapper.CourseMapper;
import com.heritage.service.CourseService;
import org.springframework.stereotype.Service;

/**
 * 课程业务实现：继承 ServiceImpl 获得完整通用 CRUD 能力，业务方法在接口层阶段补充
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Override
    public void increaseViewCount(Long id) {
        if (this.getById(id) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        // 数据库原子自增，避免并发丢失更新
        this.lambdaUpdate().eq(Course::getId, id).setSql("view_count = view_count + 1").update();
    }
}
