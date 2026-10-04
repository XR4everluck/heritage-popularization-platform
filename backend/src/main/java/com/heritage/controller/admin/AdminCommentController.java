package com.heritage.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.entity.UserComment;
import com.heritage.service.UserCommentService;
import com.heritage.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台评论管理接口：分页查询、屏蔽/恢复、删除（逻辑删除）
 */
@RestController
@RequestMapping("/api/admin/comment")
@RequiredArgsConstructor
public class AdminCommentController {

    private final UserCommentService userCommentService;

    /**
     * 分页查询评论列表（含评论人昵称/头像与非遗名称）
     *
     * @param heritageId 非遗项目ID筛选（可空）
     * @param status     状态筛选：1-正常，0-屏蔽（可空）
     * @param page       页码，默认 1
     * @param pageSize   每页条数，默认 10
     */
    @GetMapping("/page")
    public Result<Page<CommentVO>> page(@RequestParam(required = false) Long heritageId,
                                        @RequestParam(required = false) Integer status,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(userCommentService.pageVO(new Page<>(page, pageSize), heritageId, status));
    }

    /**
     * 屏蔽/恢复评论（屏蔽后前台不展示）
     *
     * @param id     评论ID
     * @param status 目标状态：0-屏蔽，1-恢复
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        UserComment update = new UserComment();
        update.setId(id);
        update.setStatus(status);
        userCommentService.updateById(update);
        return Result.ok();
    }

    /**
     * 删除评论（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userCommentService.removeById(id);
        return Result.ok();
    }
}
