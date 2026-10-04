package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.service.HeritageInfoService;
import com.heritage.vo.HeritageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 前台非遗项目接口：分页检索（分类/关键词/级别）、详情、浏览量自增
 */
@RestController
@RequestMapping("/api/heritage")
@RequiredArgsConstructor
public class FrontHeritageController {

    private final HeritageInfoService heritageInfoService;

    /**
     * 分页查询非遗列表（仅已发布），支持分类筛选、关键词搜索、级别筛选
     *
     * @param categoryId 分类ID（可空）
     * @param keyword    名称关键词（可空，模糊匹配）
     * @param level      非遗级别：国家级/省级/市级（可空）
     * @param page       页码，默认 1
     * @param pageSize   每页条数，默认 10
     */
    @GetMapping("/page")
    public Result<Page<HeritageVO>> page(@RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String level,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(heritageInfoService.pageWithCategory(new Page<>(page, pageSize),
                categoryId, keyword, level, true));
    }

    /**
     * 查询非遗详情（仅已发布；不存在或未发布返回 404 语义错误）
     */
    @GetMapping("/{id}")
    public Result<HeritageVO> detail(@PathVariable Long id) {
        return Result.ok(heritageInfoService.getDetail(id, true));
    }

    /**
     * 浏览量自增（进入详情页时由前端调用一次）
     */
    @PostMapping("/{id}/view")
    public Result<Void> view(@PathVariable Long id) {
        heritageInfoService.increaseViewCount(id);
        return Result.ok();
    }
}
