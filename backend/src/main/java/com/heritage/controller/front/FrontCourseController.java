package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.Course;
import com.heritage.entity.CourseChapter;
import com.heritage.exception.BusinessException;
import com.heritage.service.CourseChapterService;
import com.heritage.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台课程接口：按非遗项目查课程列表、课程详情、章节列表
 */
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class FrontCourseController {

    private final CourseService courseService;

    private final CourseChapterService courseChapterService;

    /**
     * 根据非遗项目ID查询课程列表（仅已发布，按发布时间倒序）
     *
     * @param heritageId 非遗项目ID
     */
    @GetMapping("/list")
    public Result<List<Course>> listByHeritage(@RequestParam Long heritageId) {
        return Result.ok(courseService.lambdaQuery()
                .eq(Course::getHeritageId, heritageId)
                .isNotNull(Course::getPublishTime)
                .orderByDesc(Course::getPublishTime)
                .orderByDesc(Course::getId)
                .list());
    }

    /**
     * 查询课程详情
     */
    @GetMapping("/{id}")
    public Result<Course> detail(@PathVariable Long id) {
        Course course = courseService.getById(id);
        if (course == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return Result.ok(course);
    }

    /**
     * 课程浏览量自增（进入课程学习页时由前端调用一次）
     */
    @PostMapping("/{id}/view")
    public Result<Void> view(@PathVariable Long id) {
        courseService.increaseViewCount(id);
        return Result.ok();
    }

    /**
     * 查询课程章节列表（按 sort 升序，即学习顺序）
     *
     * @param id 课程ID
     */
    @GetMapping("/{id}/chapters")
    public Result<List<CourseChapter>> chapters(@PathVariable Long id) {
        return Result.ok(courseChapterService.lambdaQuery()
                .eq(CourseChapter::getCourseId, id)
                .orderByAsc(CourseChapter::getSort)
                .orderByAsc(CourseChapter::getId)
                .list());
    }
}
