package com.heritage.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heritage.common.Result;
import com.heritage.entity.Course;
import com.heritage.entity.HeritageInfo;
import com.heritage.entity.Inheritor;
import com.heritage.service.CourseService;
import com.heritage.service.HeritageInfoService;
import com.heritage.service.InheritorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 前台传承人专题接口：列表、详情（含关联非遗项目与相关科普视频）
 */
@RestController
@RequestMapping("/api/inheritor")
@RequiredArgsConstructor
public class FrontInheritorController {

    /** 详情页「相关科普视频」最多返回条数 */
    private static final int RELATED_COURSE_LIMIT = 6;

    private final InheritorService inheritorService;

    private final HeritageInfoService heritageInfoService;

    private final CourseService courseService;

    /**
     * 传承人列表（仅启用的，按排序号升序）
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        List<Inheritor> inheritors = inheritorService.lambdaQuery()
                .eq(Inheritor::getStatus, 1)
                .orderByAsc(Inheritor::getSort)
                .orderByDesc(Inheritor::getCreateTime)
                .list();

        List<Map<String, Object>> result = inheritors.stream()
                .map(this::toListVO)
                .collect(Collectors.toList());
        return Result.ok(result);
    }

    /**
     * 传承人详情：实体字段 + 标签数组 + 关联非遗项目 + 相关科普视频
     *
     * @param id 传承人ID
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Inheritor inheritor = inheritorService.getById(id);
        if (inheritor == null || Integer.valueOf(1).equals(inheritor.getDeleted())
                || !Integer.valueOf(1).equals(inheritor.getStatus())) {
            return Result.error("传承人不存在或已下架");
        }

        List<HeritageInfo> heritages = listRelatedHeritage(inheritor);

        Map<String, Object> vo = toListVO(inheritor);
        vo.put("experience", inheritor.getExperience());
        vo.put("achievements", inheritor.getAchievements());
        vo.put("heritageId", inheritor.getHeritageId());
        vo.put("heritageName", inheritor.getHeritageName());
        vo.put("heritageList", heritages.stream().map(this::toHeritageVO).collect(Collectors.toList()));
        vo.put("videos", listRelatedCourses(heritages));
        // 代表作品当前无对应数据结构，返回空数组以保证前端 tab 正常渲染
        vo.put("works", new ArrayList<>());
        return Result.ok(vo);
    }

    /**
     * 关联非遗项目：优先按 heritage_info.inheritor_id 反查，
     * 其次按项目名称与传承人表中的冗余名称匹配，最后回退到该传承人自身的 heritageId。
     */
    private List<HeritageInfo> listRelatedHeritage(Inheritor inheritor) {
        List<HeritageInfo> heritages = heritageInfoService.lambdaQuery()
                .eq(HeritageInfo::getInheritorId, inheritor.getId())
                .isNotNull(HeritageInfo::getPublishTime)
                .orderByDesc(HeritageInfo::getViewCount)
                .list();

        if (!heritages.isEmpty()) {
            return heritages;
        }

        if (inheritor.getHeritageId() != null) {
            HeritageInfo heritage = heritageInfoService.getById(inheritor.getHeritageId());
            if (heritage != null && heritage.getPublishTime() != null) {
                return Collections.singletonList(heritage);
            }
        }

        if (inheritor.getHeritageName() != null && !inheritor.getHeritageName().isBlank()) {
            return heritageInfoService.lambdaQuery()
                    .eq(HeritageInfo::getName, inheritor.getHeritageName())
                    .isNotNull(HeritageInfo::getPublishTime)
                    .list();
        }

        return Collections.emptyList();
    }

    /**
     * 相关科普视频：取关联非遗项目下的科普专题，按浏览量降序
     */
    private List<Map<String, Object>> listRelatedCourses(List<HeritageInfo> heritages) {
        List<Long> heritageIds = heritages.stream()
                .map(HeritageInfo::getId)
                .collect(Collectors.toList());
        if (heritageIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Course> courses = courseService.list(new LambdaQueryWrapper<Course>()
                .in(Course::getHeritageId, heritageIds)
                .isNotNull(Course::getPublishTime)
                .orderByDesc(Course::getViewCount)
                .last("LIMIT " + RELATED_COURSE_LIMIT));

        return courses.stream().map(course -> {
            Map<String, Object> vo = new LinkedHashMap<>();
            vo.put("id", course.getId());
            vo.put("title", course.getName());
            vo.put("coverImage", course.getCover());
            vo.put("description", course.getSummary());
            vo.put("duration", course.getDuration());
            vo.put("viewCount", course.getViewCount());
            return vo;
        }).collect(Collectors.toList());
    }

    private Map<String, Object> toListVO(Inheritor inheritor) {
        Map<String, Object> vo = new LinkedHashMap<>();
        vo.put("id", inheritor.getId());
        vo.put("name", inheritor.getName());
        vo.put("title", inheritor.getTitle());
        vo.put("avatar", inheritor.getAvatar());
        vo.put("introduction", inheritor.getIntroduction());
        vo.put("tags", splitTags(inheritor.getTags()));
        vo.put("sort", inheritor.getSort());
        return vo;
    }

    private Map<String, Object> toHeritageVO(HeritageInfo heritage) {
        Map<String, Object> vo = new LinkedHashMap<>();
        vo.put("id", heritage.getId());
        vo.put("name", heritage.getName());
        vo.put("coverImage", heritage.getCoverImage());
        vo.put("description", heritage.getSummary());
        vo.put("region", heritage.getRegion());
        vo.put("level", heritage.getLevel());
        return vo;
    }

    /** 标签在库中以逗号分隔存储，这里拆成数组供前端 v-for 直接使用 */
    private List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (String tag : tags.split("[,，]")) {
            String trimmed = tag.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}
