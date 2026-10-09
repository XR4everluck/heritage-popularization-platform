package com.heritage.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heritage.common.Result;
import com.heritage.entity.Inheritor;
import com.heritage.service.InheritorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台传承人管理接口
 */
@RestController
@RequestMapping("/api/admin/inheritor")
@RequiredArgsConstructor
public class AdminInheritorController {

    private final InheritorService inheritorService;

    /**
     * 查询传承人列表
     */
    @GetMapping("/list")
    public Result<List<Inheritor>> list() {
        return Result.ok(inheritorService.list());
    }

    /**
     * 查询传承人详情（包含关联非遗）
     */
    @GetMapping("/{id}")
    public Result<Inheritor> detail(@PathVariable Long id) {
        return Result.ok(inheritorService.getInheritorWithHeritage(id));
    }

    /**
     * 新增传承人
     */
    @PostMapping
    public Result<Void> add(@RequestBody Inheritor inheritor) {
        inheritorService.save(inheritor);
        return Result.ok();
    }

    /**
     * 修改传承人
     */
    @PutMapping
    public Result<Void> update(@RequestBody Inheritor inheritor) {
        if (inheritor.getId() == null) {
            throw new RuntimeException("传承人ID不能为空");
        }
        inheritorService.updateById(inheritor);
        return Result.ok();
    }

    /**
     * 删除传承人
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        inheritorService.removeById(id);
        return Result.ok();
    }
}