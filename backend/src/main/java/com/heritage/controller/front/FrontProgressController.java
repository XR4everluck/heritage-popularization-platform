package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.dto.ProgressDTO;
import com.heritage.service.StudyProgressService;
import com.heritage.util.AuthContext;
import com.heritage.vo.ProgressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 前台学习进度接口：更新进度（upsert）、查询我的进度（均需登录，用户ID取自 token）
 */
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class FrontProgressController {

    private final StudyProgressService studyProgressService;

    /**
     * 更新学习进度：按"用户+章节"维度 upsert（存在则累计时长，不存在则新建）
     *
     * @param dto 章节ID + 本次学习时长增量（分钟）+ 完成状态
     */
    @PutMapping
    public Result<Void> update(@Validated @RequestBody ProgressDTO dto, HttpServletRequest request) {
        studyProgressService.upsert(AuthContext.getUserId(request), dto);
        return Result.ok();
    }

    /**
     * 查询我的学习进度（含课程名称/章节标题，按课程与章节顺序排序）
     *
     * @param courseId 课程ID（可空，不传查全部课程）
     */
    @GetMapping("/my")
    public Result<List<ProgressVO>> my(@RequestParam(required = false) Long courseId,
                                       HttpServletRequest request) {
        return Result.ok(studyProgressService.listMy(AuthContext.getUserId(request), courseId));
    }
}
