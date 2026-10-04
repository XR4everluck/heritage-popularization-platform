package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.entity.HeritageCategory;
import com.heritage.service.HeritageCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台非遗分类接口：分类导航列表（无需登录）
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class FrontCategoryController {

    private final HeritageCategoryService heritageCategoryService;

    /**
     * 查询全部分类列表（按 sort 升序，用于前台分类导航）
     */
    @GetMapping("/list")
    public Result<List<HeritageCategory>> list() {
        return Result.ok(heritageCategoryService.lambdaQuery()
                .orderByAsc(HeritageCategory::getSort)
                .orderByAsc(HeritageCategory::getId)
                .list());
    }
}
