package com.heritage.controller.admin;

import com.heritage.common.Result;
import com.heritage.service.UserSubmissionService;
import com.heritage.util.AuthContext;
import com.heritage.vo.SubmissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 后台内容审核接口：列表查询（状态筛选）、审核通过/驳回（仅管理员）
 */
@RestController
@RequestMapping("/api/admin/submission")
@RequiredArgsConstructor
public class AdminSubmissionController {

    private final UserSubmissionService userSubmissionService;

    /**
     * 发布列表（含提交人昵称/关联项目名称）
     *
     * @param status 状态筛选：0-待审核，1-已通过，2-未通过（可空=全部）
     */
    @GetMapping("/list")
    public Result<List<SubmissionVO>> list(@RequestParam(required = false) Integer status) {
        return Result.ok(userSubmissionService.listForAdmin(status));
    }

    /**
     * 审核：通过（status=1）或驳回（status=2，需附原因）
     *
     * @param id   发布ID
     * @param body { status: 1|2, reviewNote: "审核意见" }
     */
    @PostMapping("/{id}/review")
    public Result<SubmissionVO> review(@PathVariable Long id,
                                       @RequestBody Map<String, Object> body,
                                       HttpServletRequest request) {
        // 角色校验：仅管理员可审核
        if (!"admin".equals(AuthContext.getRole(request))) {
            return Result.error(403, "仅管理员可审核");
        }
        Integer status = body.get("status") == null ? null : Integer.valueOf(String.valueOf(body.get("status")));
        if (status == null || (status != 1 && status != 2)) {
            return Result.error(400, "审核状态非法");
        }
        String reviewNote = body.get("reviewNote") == null ? null : String.valueOf(body.get("reviewNote"));
        SubmissionVO reviewed = userSubmissionService.review(id, status, reviewNote);
        if (reviewed == null) {
            return Result.error(404, "记录不存在或已审核");
        }
        return Result.ok(reviewed);
    }

    /**
     * 下架已通过内容（全民科普/首页随之隐藏）
     *
     * @param id   发布ID
     * @param body { note: "下架原因（可空）" }
     */
    @PostMapping("/{id}/offline")
    public Result<SubmissionVO> offline(@PathVariable Long id,
                                        @RequestBody Map<String, Object> body,
                                        HttpServletRequest request) {
        if (!"admin".equals(AuthContext.getRole(request))) {
            return Result.error(403, "仅管理员可操作");
        }
        String note = body.get("note") == null ? null : String.valueOf(body.get("note"));
        SubmissionVO offlined = userSubmissionService.offline(id, note);
        if (offlined == null) {
            return Result.error(404, "记录不存在或非已通过状态");
        }
        return Result.ok(offlined);
    }
}
