package com.heritage.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.HeritageTip;
import com.heritage.exception.BusinessException;
import com.heritage.service.HeritageTipService;
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
 * 后台冷知识管理接口：分页查询、新增、修改、删除
 */
@RestController
@RequestMapping("/api/admin/tip")
@RequiredArgsConstructor
public class AdminTipController {

    private final HeritageTipService heritageTipService;

    /**
     * 分页查询冷知识列表
     */
    @GetMapping("/page")
    public Result<Page<HeritageTip>> page(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(heritageTipService.lambdaQuery()
                .orderByDesc(HeritageTip::getId)
                .page(new Page<>(page, pageSize)));
    }

    /**
     * 新增冷知识
     */
    @PostMapping
    public Result<Void> add(@RequestBody HeritageTip tip) {
        heritageTipService.save(tip);
        return Result.ok();
    }

    /**
     * 修改冷知识
     */
    @PutMapping
    public Result<Void> update(@RequestBody HeritageTip tip) {
        if (tip.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "冷知识ID不能为空");
        }
        heritageTipService.updateById(tip);
        return Result.ok();
    }

    /**
     * 删除冷知识
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        heritageTipService.removeById(id);
        return Result.ok();
    }
}
