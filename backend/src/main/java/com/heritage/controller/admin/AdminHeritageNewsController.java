package com.heritage.controller.admin;

import com.heritage.common.Result;
import com.heritage.entity.HeritageNews;
import com.heritage.service.HeritageNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 非遗科普快讯管理控制器
 */
@RestController
@RequestMapping("/api/admin/heritage-news")
@RequiredArgsConstructor
public class AdminHeritageNewsController {

    private final HeritageNewsService heritageNewsService;

    /**
     * 分页查询快讯列表
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param keyword  搜索关键词
     * @param heritageId 非遗项目ID
     * @param isTop    是否置顶：0-不置顶，1-置顶
     * @param status   状态：0-禁用，1-启用
     * @return 快讯列表
     */
    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<HeritageNews>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long heritageId,
            @RequestParam(required = false) Integer isTop,
            @RequestParam(required = false) Integer status) {
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<HeritageNews> pageInfo = heritageNewsService.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HeritageNews>()
                        .like(keyword != null, HeritageNews::getTitle, keyword)
                        .eq(heritageId != null, HeritageNews::getHeritageId, heritageId)
                        .eq(isTop != null, HeritageNews::getIsTop, isTop)
                        .eq(status != null, HeritageNews::getStatus, status)
                        .eq(HeritageNews::getDeleted, 0)
                        // 置顶(1) 优先于 非置顶(0)
                        .orderByDesc(HeritageNews::getIsTop)
                        .orderByDesc(HeritageNews::getPublishTime)
        );
        
        return Result.ok(pageInfo);
    }

    /**
     * 获取快讯详情
     *
     * @param id 快讯ID
     * @return 快讯详情
     */
    @GetMapping("/{id}")
    public Result<HeritageNews> get(@PathVariable Long id) {
        HeritageNews news = heritageNewsService.getById(id);
        if (news == null) {
            return Result.error("快讯不存在");
        }
        return Result.ok(news);
    }

    /**
     * 保存快讯信息
     *
     * @param news 快讯信息
     * @return 保存结果
     */
    @PostMapping
    public Result<HeritageNews> save(@RequestBody HeritageNews news) {
        try {
            news = heritageNewsService.saveNews(news);
            return Result.ok(news);
        } catch (Exception e) {
            return Result.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 更新快讯信息
     *
     * @param news 快讯信息
     * @return 更新结果
     */
    @PutMapping
    public Result<HeritageNews> update(@RequestBody HeritageNews news) {
        try {
            news = heritageNewsService.updateNews(news);
            return Result.ok(news);
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除快讯信息
     *
     * @param id 快讯ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            boolean success = heritageNewsService.deleteNews(id);
            if (success) {
                return Result.ok();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 启用/禁用快讯
     *
     * @param id     快讯ID
     * @param status 状态：0-禁用，1-启用
     * @return 更新结果
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = heritageNewsService.updateStatus(id, status);
            if (success) {
                return Result.ok();
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 设置/取消置顶
     *
     * @param id   快讯ID
     * @param isTop 是否置顶：0-不置顶，1-置顶
     * @return 更新结果
     */
    @PutMapping("/top/{id}")
    public Result<Void> updateTop(@PathVariable Long id, @RequestParam Integer isTop) {
        try {
            boolean success = heritageNewsService.updateTop(id, isTop);
            if (success) {
                return Result.ok();
            } else {
                return Result.error("置顶状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("置顶状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 根据非遗项目ID获取快讯列表
     *
     * @param heritageId 非遗项目ID
     * @return 快讯列表
     */
    @GetMapping("/heritage/{heritageId}")
    public Result<List<HeritageNews>> getByHeritageId(@PathVariable Long heritageId) {
        List<HeritageNews> newsList = heritageNewsService.getByHeritageId(heritageId);
        return Result.ok(newsList);
    }

    /**
     * 获取置顶的快讯列表
     *
     * @return 置顶快讯列表
     */
    @GetMapping("/top")
    public Result<List<HeritageNews>> getTopNews() {
        List<HeritageNews> newsList = heritageNewsService.getTopNews();
        return Result.ok(newsList);
    }

    /**
     * 获取最新的快讯列表
     *
     * @param limit 数量限制
     * @return 最新快讯列表
     */
    @GetMapping("/latest")
    public Result<List<HeritageNews>> getLatestNews(@RequestParam(defaultValue = "10") Integer limit) {
        List<HeritageNews> newsList = heritageNewsService.getLatestNews(limit);
        return Result.ok(newsList);
    }
}