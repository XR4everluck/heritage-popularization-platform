package com.heritage.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.Banner;
import com.heritage.exception.BusinessException;
import com.heritage.service.BannerService;
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
 * 后台轮播图管理接口：分页查询、新增、修改、删除（物理删除）
 */
@RestController
@RequestMapping("/api/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    /**
     * 分页查询轮播图列表（按 sort 升序）
     *
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 10
     */
    @GetMapping("/page")
    public Result<Page<Banner>> page(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(bannerService.lambdaQuery()
                .orderByAsc(Banner::getSort)
                .orderByAsc(Banner::getId)
                .page(new Page<>(page, pageSize)));
    }

    /**
     * 新增轮播图（image 必填；sort 越小越靠前，status 1-启用 0-停用）
     */
    @PostMapping
    public Result<Void> add(@RequestBody Banner banner) {
        bannerService.save(banner);
        return Result.ok();
    }

    /**
     * 修改轮播图（请求体需携带 id）
     */
    @PutMapping
    public Result<Void> update(@RequestBody Banner banner) {
        if (banner.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "轮播图ID不能为空");
        }
        bannerService.updateById(banner);
        return Result.ok();
    }

    /**
     * 删除轮播图（物理删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.removeById(id);
        return Result.ok();
    }
}
