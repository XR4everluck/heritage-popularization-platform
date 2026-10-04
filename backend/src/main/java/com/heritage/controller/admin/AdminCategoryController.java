package com.heritage.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.HeritageCategory;
import com.heritage.exception.BusinessException;
import com.heritage.service.HeritageCategoryService;
import cn.hutool.core.util.StrUtil;
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
 * 后台非遗分类管理接口：分页查询、新增、修改、删除（逻辑删除）
 */
@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final HeritageCategoryService heritageCategoryService;

    /**
     * 分页查询分类列表
     *
     * @param keyword  分类名称关键词（可空，模糊匹配）
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 10
     */
    @GetMapping("/page")
    public Result<Page<HeritageCategory>> page(@RequestParam(required = false) String keyword,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<HeritageCategory> qw = new LambdaQueryWrapper<>();
        qw.like(StrUtil.isNotBlank(keyword), HeritageCategory::getName, keyword)
                .orderByAsc(HeritageCategory::getSort)
                .orderByAsc(HeritageCategory::getId);
        return Result.ok(heritageCategoryService.page(new Page<>(page, pageSize), qw));
    }

    /**
     * 新增分类
     */
    @PostMapping
    public Result<Void> add(@RequestBody HeritageCategory category) {
        heritageCategoryService.save(category);
        return Result.ok();
    }

    /**
     * 修改分类（请求体需携带 id；仅更新传入字段）
     */
    @PutMapping
    public Result<Void> update(@RequestBody HeritageCategory category) {
        if (category.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "分类ID不能为空");
        }
        heritageCategoryService.updateById(category);
        return Result.ok();
    }

    /**
     * 删除分类（逻辑删除；建议先确认分类下无非遗项目）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        heritageCategoryService.removeById(id);
        return Result.ok();
    }
}
