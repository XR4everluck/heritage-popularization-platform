package com.heritage.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.Banner;
import com.heritage.entity.Notice;
import com.heritage.exception.BusinessException;
import com.heritage.service.BannerService;
import com.heritage.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台门户内容接口：首页轮播图、公告列表与详情（无需登录）
 */
@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
public class FrontPortalController {

    private final BannerService bannerService;

    private final NoticeService noticeService;

    /**
     * 查询前台展示的轮播图列表（仅启用状态，按 sort 升序）
     */
    @GetMapping("/banners")
    public Result<List<Banner>> banners() {
        return Result.ok(bannerService.lambdaQuery()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort)
                .orderByAsc(Banner::getId)
                .list());
    }

    /**
     * 分页查询公告列表（仅已发布，按发布时间倒序）
     *
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 10
     */
    @GetMapping("/notices")
    public Result<Page<Notice>> notices(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(noticeService.lambdaQuery()
                .eq(Notice::getStatus, 1)
                .isNotNull(Notice::getPublishTime)
                .orderByDesc(Notice::getPublishTime)
                .orderByDesc(Notice::getId)
                .page(new Page<>(page, pageSize)));
    }

    /**
     * 查询公告详情（仅已发布；不存在或未发布返回 404 语义错误）
     */
    @GetMapping("/notices/{id}")
    public Result<Notice> noticeDetail(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        if (notice == null || notice.getStatus() == null || notice.getStatus() != 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return Result.ok(notice);
    }
}
