package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.Course;

/**
 * 课程业务接口：继承 IService 获得通用 CRUD 与批量操作，另含浏览量统计
 */
public interface CourseService extends IService<Course> {

    /**
     * 课程浏览量自增（view_count = view_count + 1，数据库原子操作）
     */
    void increaseViewCount(Long id);
}
