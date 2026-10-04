package com.heritage.controller.admin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.CourseChapter;
import com.heritage.exception.BusinessException;
import com.heritage.service.CourseChapterService;
import com.heritage.service.StudyProgressService;
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

import java.util.List;

/**
 * 后台课程章节管理接口：按课程查章节列表、新增、修改、删除（物理删除）
 */
@RestController
@RequestMapping("/api/admin/chapter")
@RequiredArgsConstructor
public class AdminChapterController {

    private final CourseChapterService courseChapterService;

    private final StudyProgressService studyProgressService;

    /**
     * 查询课程下的章节列表（按 sort 升序）
     *
     * @param courseId 课程ID
     */
    @GetMapping("/list")
    public Result<List<CourseChapter>> list(@RequestParam Long courseId) {
        return Result.ok(courseChapterService.lambdaQuery()
                .eq(CourseChapter::getCourseId, courseId)
                .orderByAsc(CourseChapter::getSort)
                .orderByAsc(CourseChapter::getId)
                .list());
    }

    /**
     * 新增章节（courseId/title/sort 必填；视频地址由文件上传后回填）
     */
    @PostMapping
    public Result<Void> add(@RequestBody CourseChapter chapter) {
        courseChapterService.save(chapter);
        return Result.ok();
    }

    /**
     * 修改章节（请求体需携带 id；支持改标题/视频地址/讲义内容/排序）
     */
    @PutMapping
    public Result<Void> update(@RequestBody CourseChapter chapter) {
        if (chapter.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "章节ID不能为空");
        }
        courseChapterService.updateById(chapter);
        return Result.ok();
    }

    /**
     * 删除章节（章节为课程从属数据，物理删除；同时清理该章节的学习进度，避免产生孤儿进度数据）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        studyProgressService.remove(Wrappers.<com.heritage.entity.StudyProgress>lambdaQuery()
                .eq(com.heritage.entity.StudyProgress::getChapterId, id));
        courseChapterService.removeById(id);
        return Result.ok();
    }
}
