package com.heritage.controller.admin;

import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.HeritageHistory;
import com.heritage.exception.BusinessException;
import com.heritage.service.HeritageHistoryService;
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

import java.util.List;

/**
 * 后台非遗历史节点管理接口：按项目查询、新增、修改、删除（物理删除）
 */
@RestController
@RequestMapping("/api/admin/history")
@RequiredArgsConstructor
public class AdminHistoryController {

    private final HeritageHistoryService heritageHistoryService;

    /**
     * 按非遗项目查询历史节点（按 id 升序，即年代顺序）
     *
     * @param heritageId 非遗项目ID
     */
    @GetMapping("/list")
    public Result<List<HeritageHistory>> listByHeritage(@RequestParam Long heritageId) {
        return Result.ok(heritageHistoryService.lambdaQuery()
                .eq(HeritageHistory::getHeritageId, heritageId)
                .orderByAsc(HeritageHistory::getId)
                .list());
    }

    /**
     * 新增历史节点（heritageId/year/event 必填）
     */
    @PostMapping
    public Result<Void> add(@RequestBody HeritageHistory history) {
        if (history.getHeritageId() == null || history.getEvent() == null || history.getEvent().isBlank()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "项目ID与事件标题不能为空");
        }
        heritageHistoryService.save(history);
        return Result.ok();
    }

    /**
     * 修改历史节点（请求体需携带 id）
     */
    @PutMapping
    public Result<Void> update(@RequestBody HeritageHistory history) {
        if (history.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "历史节点ID不能为空");
        }
        heritageHistoryService.updateById(history);
        return Result.ok();
    }

    /**
     * 删除历史节点（物理删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        heritageHistoryService.removeById(id);
        return Result.ok();
    }
}
