package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.common.ResultCode;
import com.heritage.dto.ProgressDTO;
import com.heritage.entity.Course;
import com.heritage.entity.CourseChapter;
import com.heritage.entity.StudyProgress;
import com.heritage.exception.BusinessException;
import com.heritage.mapper.StudyProgressMapper;
import com.heritage.service.CourseChapterService;
import com.heritage.service.CourseService;
import com.heritage.service.StudyProgressService;
import com.heritage.vo.ProgressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学习进度业务实现：按"用户+章节"维度 upsert，进度查询组装课程/章节信息
 */
@Service
@RequiredArgsConstructor
public class StudyProgressServiceImpl extends ServiceImpl<StudyProgressMapper, StudyProgress> implements StudyProgressService {

    private final CourseService courseService;

    private final CourseChapterService courseChapterService;

    @Override
    public void upsert(Long userId, ProgressDTO dto) {
        CourseChapter chapter = courseChapterService.getById(dto.getChapterId());
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }
        // 课程ID以章节记录为准，客户端传入仅做一致性校验，防止进度挂在错误课程下
        if (dto.getCourseId() != null && !dto.getCourseId().equals(chapter.getCourseId())) {
            throw new BusinessException("章节与课程不匹配");
        }
        int addDuration = dto.getStudyDuration() == null || dto.getStudyDuration() < 0 ? 0 : dto.getStudyDuration();
        int finished = dto.getFinished() == null ? 0 : dto.getFinished();

        StudyProgress progress = this.lambdaQuery().eq(StudyProgress::getUserId, userId)
                .eq(StudyProgress::getChapterId, dto.getChapterId()).one();
        if (progress == null) {
            progress = new StudyProgress();
            progress.setUserId(userId);
            progress.setCourseId(chapter.getCourseId());
            progress.setChapterId(dto.getChapterId());
            progress.setStudyDuration(addDuration);
            progress.setFinished(finished);
            this.save(progress);
        } else {
            // 学习时长累计；完成状态只进不退（一旦完成保持完成）
            progress.setStudyDuration(progress.getStudyDuration() + addDuration);
            if (finished == 1) {
                progress.setFinished(1);
            }
            this.updateById(progress);
        }
    }

    @Override
    public List<ProgressVO> listMy(Long userId, Long courseId) {
        LambdaQueryWrapper<StudyProgress> qw = new LambdaQueryWrapper<>();
        qw.eq(StudyProgress::getUserId, userId)
                .eq(courseId != null, StudyProgress::getCourseId, courseId);
        List<StudyProgress> list = this.list(qw);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, CourseChapter> chapters = courseChapterService.listByIds(
                        list.stream().map(StudyProgress::getChapterId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(CourseChapter::getId, c -> c));
        Map<Long, Course> courses = courseService.listByIds(
                        list.stream().map(StudyProgress::getCourseId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Course::getId, c -> c));
        Map<Long, Integer> chapterSort = chapters.values().stream()
                .collect(Collectors.toMap(CourseChapter::getId, CourseChapter::getSort));

        List<ProgressVO> vos = new ArrayList<>();
        for (StudyProgress progress : list) {
            ProgressVO vo = new ProgressVO();
            vo.setId(progress.getId());
            vo.setCourseId(progress.getCourseId());
            vo.setChapterId(progress.getChapterId());
            vo.setStudyDuration(progress.getStudyDuration());
            vo.setFinished(progress.getFinished());
            vo.setUpdateTime(progress.getUpdateTime());
            Course course = courses.get(progress.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getName());
            }
            CourseChapter chapter = chapters.get(progress.getChapterId());
            if (chapter != null) {
                vo.setChapterTitle(chapter.getTitle());
            }
            vos.add(vo);
        }
        // 按课程ID、章节排序号排序，还原学习顺序
        vos.sort(Comparator.comparing(ProgressVO::getCourseId)
                .thenComparing(vo -> chapterSort.getOrDefault(vo.getChapterId(), Integer.MAX_VALUE)));
        return vos;
    }
}
