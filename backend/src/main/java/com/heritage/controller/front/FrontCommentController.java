package com.heritage.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.dto.CommentDTO;
import com.heritage.service.UserCommentService;
import com.heritage.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台评论接口：发布评论、非遗下评论分页、删除自己的评论
 *
 * <p>说明：用户身份暂由前端传 userId 参数（调试模式），后续接入 JWT 后替换。</p>
 */
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class FrontCommentController {

    private final UserCommentService userCommentService;

    /**
     * 发布评论（默认状态正常，进入管理员审核范围）
     *
     * @param userId 用户ID（调试模式）
     * @param dto    非遗项目ID + 评论内容
     */
    @PostMapping
    public Result<Void> post(@RequestParam Long userId, @Validated @RequestBody CommentDTO dto) {
        userCommentService.post(userId, dto);
        return Result.ok();
    }

    /**
     * 分页查询某非遗项目下的评论（仅展示正常状态，按评论时间倒序，含评论人昵称/头像）
     *
     * @param heritageId 非遗项目ID
     * @param page       页码，默认 1
     * @param pageSize   每页条数，默认 10
     */
    @GetMapping("/page")
    public Result<Page<CommentVO>> page(@RequestParam Long heritageId,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(userCommentService.pageVO(new Page<>(page, pageSize), heritageId, 1));
    }

    /**
     * 删除自己的评论（非本人评论返回 403 语义错误，逻辑删除）
     *
     * @param id     评论ID
     * @param userId 用户ID（调试模式）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        userCommentService.deleteOwn(userId, id);
        return Result.ok();
    }
}
