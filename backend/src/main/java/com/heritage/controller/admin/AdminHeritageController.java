package com.heritage.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.HeritageHistory;
import com.heritage.entity.HeritageInfo;
import com.heritage.exception.BusinessException;
import com.heritage.service.HeritageHistoryService;
import com.heritage.service.HeritageInfoService;
import com.heritage.vo.HeritageVO;
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
 * 后台非遗项目管理接口：分页查询（全量含未发布）、新增、修改、删除（逻辑删除）
 */
@RestController
@RequestMapping("/api/admin/heritage")
@RequiredArgsConstructor
public class AdminHeritageController {

    private final HeritageInfoService heritageInfoService;

    private final HeritageHistoryService heritageHistoryService;

    /**
     * 分页查询非遗项目列表（含未发布，支持分类/关键词/级别/地区/快讯筛选）
     *
     * @param categoryId 分类ID（可空）
     * @param keyword    名称关键词（可空）
     * @param level      非遗级别：国家级/省级/市级（可空）
     * @param region     所属地区（可空，模糊匹配）
     * @param isNews     是否为科普快讯：0/1（可空）
     * @param page       页码，默认 1
     * @param pageSize   每页条数，默认 10
     */
    @GetMapping("/page")
    public Result<Page<HeritageVO>> page(@RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String level,
                                         @RequestParam(required = false) String region,
                                         @RequestParam(required = false) Integer isNews,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(heritageInfoService.pageWithCategory(new Page<>(page, pageSize),
                categoryId, keyword, level, region, isNews, false));
    }

    /**
     * 新增非遗项目（categoryId/name/level 必填由前端保证；发布时传入 publishTime）
     */
    @PostMapping
    public Result<Void> add(@RequestBody HeritageInfo heritageInfo) {
        heritageInfoService.save(heritageInfo);
        return Result.ok();
    }

    /**
     * 修改非遗项目（请求体需携带 id；仅更新传入字段，富文本详情/封面一并支持）
     */
    @PutMapping
    public Result<Void> update(@RequestBody HeritageInfo heritageInfo) {
        if (heritageInfo.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "非遗项目ID不能为空");
        }
        heritageInfoService.updateById(heritageInfo);
        return Result.ok();
    }

    /**
     * 删除非遗项目（逻辑删除；其下课程/收藏/评论数据保留，前台自动不可见）。
     * 历史节点为从属内容数据且无逻辑删除标记，随项目一并物理清理，避免遗留孤儿数据。
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        heritageInfoService.removeById(id);
        heritageHistoryService.lambdaUpdate()
                .eq(HeritageHistory::getHeritageId, id)
                .remove();
        return Result.ok();
    }
}
