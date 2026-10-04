package com.heritage.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.Course;
import com.heritage.exception.BusinessException;
import com.heritage.service.CourseService;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台课程管理接口：分页查询、新增、修改、删除（逻辑删除）
 */
@RestController
@RequestMapping("/api/admin/course")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    /**
     * 分页查询课程列表
     *
     * @param heritageId 非遗项目ID（可空）
     * @param keyword    课程名称关键词（可空）
     * @param page       页码，默认 1
     * @param pageSize   每页条数，默认 10
     */
    @GetMapping("/page")
    public Result<Page<Course>> page(@RequestParam(required = false) Long heritageId,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Course> qw = new LambdaQueryWrapper<>();
        qw.eq(heritageId != null, Course::getHeritageId, heritageId)
                .like(StrUtil.isNotBlank(keyword), Course::getName, keyword)
                .orderByDesc(Course::getCreateTime);
        return Result.ok(courseService.page(new Page<>(page, pageSize), qw));
    }

    /**
     * 新增课程（heritageId 必填；发布时传入 publishTime）
     */
    @PostMapping
    public Result<Void> add(@RequestBody Course course) {
        courseService.save(course);
        return Result.ok();
    }

    /**
     * 修改课程（请求体需携带 id；仅更新传入字段）
     */
    @PutMapping
    public Result<Void> update(@RequestBody Course course) {
        if (course.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "课程ID不能为空");
        }
        courseService.updateById(course);
        return Result.ok();
    }

    /**
     * 删除课程（逻辑删除；其下章节保留，前台自动不可见）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.removeById(id);
        return Result.ok();
    }
}
