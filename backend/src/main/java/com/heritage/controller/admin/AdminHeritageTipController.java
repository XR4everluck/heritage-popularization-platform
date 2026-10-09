package com.heritage.controller.admin;

import com.heritage.common.Result;
import com.heritage.entity.HeritageTip;
import com.heritage.service.HeritageTipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 非遗冷知识管理控制器
 */
@RestController
@RequestMapping("/api/admin/heritage-tip")
@RequiredArgsConstructor
public class AdminHeritageTipController {

    private final HeritageTipService heritageTipService;

    /**
     * 分页查询冷知识列表
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param keyword  搜索关键词
     * @param heritageId 非遗项目ID
     * @param status   状态：0-禁用，1-启用
     * @return 冷知识列表
     */
    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<HeritageTip>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long heritageId,
            @RequestParam(required = false) Integer status) {
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<HeritageTip> pageInfo = heritageTipService.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HeritageTip>()
                        .like(keyword != null, HeritageTip::getTitle, keyword)
                        .eq(heritageId != null, HeritageTip::getHeritageId, heritageId)
                        .eq(status != null, HeritageTip::getStatus, status)
                        .eq(HeritageTip::getDeleted, 0)
                        .orderByAsc(HeritageTip::getSort)
                        .orderByDesc(HeritageTip::getCreateTime)
        );
        
        return Result.ok(pageInfo);
    }

    /**
     * 获取冷知识详情
     *
     * @param id 冷知识ID
     * @return 冷知识详情
     */
    @GetMapping("/{id}")
    public Result<HeritageTip> get(@PathVariable Long id) {
        HeritageTip tip = heritageTipService.getById(id);
        if (tip == null) {
            return Result.error("冷知识不存在");
        }
        return Result.ok(tip);
    }

    /**
     * 保存冷知识信息
     *
     * @param tip 冷知识信息
     * @return 保存结果
     */
    @PostMapping
    public Result<HeritageTip> save(@RequestBody HeritageTip tip) {
        try {
            tip = heritageTipService.saveTip(tip);
            return Result.ok(tip);
        } catch (Exception e) {
            return Result.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 更新冷知识信息
     *
     * @param tip 冷知识信息
     * @return 更新结果
     */
    @PutMapping
    public Result<HeritageTip> update(@RequestBody HeritageTip tip) {
        try {
            tip = heritageTipService.updateTip(tip);
            return Result.ok(tip);
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除冷知识信息
     *
     * @param id 冷知识ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            boolean success = heritageTipService.deleteTip(id);
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
     * 启用/禁用冷知识
     *
     * @param id     冷知识ID
     * @param status 状态：0-禁用，1-启用
     * @return 更新结果
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = heritageTipService.updateStatus(id, status);
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
     * 根据非遗项目ID获取冷知识列表
     *
     * @param heritageId 非遗项目ID
     * @return 冷知识列表
     */
    @GetMapping("/heritage/{heritageId}")
    public Result<List<HeritageTip>> getByHeritageId(@PathVariable Long heritageId) {
        List<HeritageTip> tips = heritageTipService.getByHeritageId(heritageId);
        return Result.ok(tips);
    }

    /**
     * 随机获取一条冷知识
     *
     * @param heritageId 非遗项目ID（可选）
     * @return 冷知识信息
     */
    @GetMapping("/random")
    public Result<HeritageTip> randomOne(@RequestParam(required = false) Long heritageId) {
        HeritageTip tip = heritageTipService.randomOne(heritageId);
        if (tip == null) {
            return Result.error("暂无冷知识数据");
        }
        return Result.ok(tip);
    }
}