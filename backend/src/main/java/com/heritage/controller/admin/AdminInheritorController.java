package com.heritage.controller.admin;

import com.heritage.common.Result;
import com.heritage.entity.Inheritor;
import com.heritage.service.InheritorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 传承人信息管理控制器
 */
@RestController
@RequestMapping("/api/admin/inheritor")
@RequiredArgsConstructor
public class AdminInheritorController {

    private final InheritorService inheritorService;

    /**
     * 分页查询传承人列表
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param keyword  搜索关键词
     * @param status   状态：0-禁用，1-启用
     * @return 传承人列表
     */
    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<Inheritor>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Inheritor> pageInfo = inheritorService.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Inheritor>()
                        .like(keyword != null, Inheritor::getName, keyword)
                        .eq(status != null, Inheritor::getStatus, status)
                        .eq(Inheritor::getDeleted, 0)
                        .orderByAsc(Inheritor::getSort)
                        .orderByDesc(Inheritor::getCreateTime)
        );
        
        return Result.ok(pageInfo);
    }

    /**
     * 获取传承人详情
     *
     * @param id 传承人ID
     * @return 传承人详情
     */
    @GetMapping("/{id}")
    public Result<Inheritor> get(@PathVariable Long id) {
        Inheritor inheritor = inheritorService.getById(id);
        if (inheritor == null) {
            return Result.error("传承人不存在");
        }
        return Result.ok(inheritor);
    }

    /**
     * 保存传承人信息
     *
     * @param inheritor 传承人信息
     * @return 保存结果
     */
    @PostMapping
    public Result<Inheritor> save(@RequestBody Inheritor inheritor) {
        try {
            inheritor = inheritorService.saveInheritor(inheritor);
            return Result.ok(inheritor);
        } catch (Exception e) {
            return Result.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 更新传承人信息
     *
     * @param inheritor 传承人信息
     * @return 更新结果
     */
    @PutMapping
    public Result<Inheritor> update(@RequestBody Inheritor inheritor) {
        try {
            inheritor = inheritorService.updateInheritor(inheritor);
            return Result.ok(inheritor);
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除传承人信息
     *
     * @param id 传承人ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            boolean success = inheritorService.deleteInheritor(id);
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
     * 启用/禁用传承人
     *
     * @param id     传承人ID
     * @param status 状态：0-禁用，1-启用
     * @return 更新结果
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = inheritorService.updateStatus(id, status);
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
     * 获取所有传承人列表（用于下拉选择）
     *
     * @return 传承人列表
     */
    @GetMapping("/list")
    public Result<List<Inheritor>> list() {
        List<Inheritor> list = inheritorService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Inheritor>()
                        .eq(Inheritor::getStatus, 1)
                        .eq(Inheritor::getDeleted, 0)
                        .orderByAsc(Inheritor::getSort)
        );
        return Result.ok(list);
    }
}