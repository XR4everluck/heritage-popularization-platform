package com.heritage.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.dto.SubmissionDTO;
import com.heritage.service.UserSubmissionService;
import com.heritage.util.AuthContext;
import com.heritage.vo.SubmissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 前台用户内容发布接口：提交科普快讯/科普视频、查看我的发布（均需登录，用户ID取自 token）
 */
@RestController
@RequestMapping("/api/submission")
@RequiredArgsConstructor
public class FrontSubmissionController {

    private final UserSubmissionService userSubmissionService;

    /**
     * 提交内容（默认待审核，管理员通过后前台展示）
     *
     * @param dto 类型 + 标题 + 关联项目（可选）+ 正文/简介 + 视频链接
     */
    @PostMapping
    public Result<SubmissionVO> submit(@Validated @RequestBody SubmissionDTO dto, HttpServletRequest request) {
        return Result.ok(userSubmissionService.submit(AuthContext.getUserId(request), dto));
    }

    /**
     * 我的发布列表（含审核状态与驳回原因）
     */
    @GetMapping("/my")
    public Result<List<SubmissionVO>> my(HttpServletRequest request) {
        return Result.ok(userSubmissionService.listMy(AuthContext.getUserId(request)));
    }

    /**
     * 全民科普分页（公开接口，游客可浏览）：仅已通过内容，按审核时间倒序
     *
     * @param type     类型筛选：news/course（可空=全部）
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 9
     */
    @GetMapping("/approved")
    public Result<Page<SubmissionVO>> approved(@RequestParam(required = false) String type,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "9") Integer pageSize) {
        return Result.ok(userSubmissionService.pageApproved(new Page<>(page, pageSize), type));
    }
}
