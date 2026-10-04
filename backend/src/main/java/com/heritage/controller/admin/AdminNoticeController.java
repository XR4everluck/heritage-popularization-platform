package com.heritage.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.Notice;
import com.heritage.exception.BusinessException;
import com.heritage.service.NoticeService;
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
 * 后台公告管理接口：分页查询、新增、修改、删除（逻辑删除）
 */
@RestController
@RequestMapping("/api/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;

    /**
     * 分页查询公告列表（含草稿/下架，支持标题关键词与状态筛选）
     *
     * @param keyword  标题关键词（可空）
     * @param status   状态筛选：1-已发布，0-下架/草稿（可空）
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 10
     */
    @GetMapping("/page")
    public Result<Page<Notice>> page(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer status,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Notice> qw = new LambdaQueryWrapper<>();
        qw.like(StrUtil.isNotBlank(keyword), Notice::getTitle, keyword)
                .eq(status != null, Notice::getStatus, status)
                .orderByDesc(Notice::getCreateTime);
        return Result.ok(noticeService.page(new Page<>(page, pageSize), qw));
    }

    /**
     * 新增公告（发布时 status=1 并传入 publishTime，否则为草稿）
     */
    @PostMapping
    public Result<Void> add(@RequestBody Notice notice) {
        noticeService.save(notice);
        return Result.ok();
    }

    /**
     * 修改公告（请求体需携带 id；支持改标题/内容/状态/发布时间）
     */
    @PutMapping
    public Result<Void> update(@RequestBody Notice notice) {
        if (notice.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "公告ID不能为空");
        }
        noticeService.updateById(notice);
        return Result.ok();
    }

    /**
     * 删除公告（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.ok();
    }
}
