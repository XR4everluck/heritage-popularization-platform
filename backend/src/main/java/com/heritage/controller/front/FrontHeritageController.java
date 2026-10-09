package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.HeritageHistory;
import com.heritage.entity.HeritageInfo;
import com.heritage.entity.HeritageTip;
import com.heritage.service.HeritageHistoryService;
import com.heritage.service.HeritageInfoService;
import com.heritage.service.HeritageTipService;
import com.heritage.vo.HeritageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 前台非遗项目接口：分页检索（分类/关键词/级别/地区/快讯）、详情、历史节点、浏览量自增、同类随机推荐、冷知识
 */
@RestController
@RequestMapping("/api/heritage")
@RequiredArgsConstructor
public class FrontHeritageController {

    private final HeritageInfoService heritageInfoService;

    private final HeritageHistoryService heritageHistoryService;

    private final HeritageTipService heritageTipService;

    /**
     * 分页查询非遗列表（仅已发布），支持分类/关键词/级别/地区/快讯筛选
     *
     * @param categoryId 分类ID（可空）
     * @param keyword    名称关键词（可空，模糊匹配）
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
                categoryId, keyword, level, region, isNews, true));
    }

    /**
     * 查询非遗详情（仅已发布；不存在或未发布返回 404 语义错误）
     */
    @GetMapping("/{id}")
    public Result<HeritageVO> detail(@PathVariable Long id) {
        return Result.ok(heritageInfoService.getDetail(id, true));
    }

    /**
     * 查询非遗项目的历史发展节点（按 id 升序，即种子数据的年代顺序）
     *
     * @param id 非遗项目ID
     */
    @GetMapping("/{id}/history")
    public Result<List<HeritageHistory>> history(@PathVariable Long id) {
        return Result.ok(heritageHistoryService.lambdaQuery()
                .eq(HeritageHistory::getHeritageId, id)
                .orderByAsc(HeritageHistory::getId)
                .list());
    }

    /**
     * 浏览量自增（进入详情页时由前端调用一次）
     */
    @PostMapping("/{id}/view")
    public Result<Void> view(@PathVariable Long id) {
        heritageInfoService.increaseViewCount(id);
        return Result.ok();
    }

    /**
     * 猜你喜欢：按分类随机推荐非遗项目（仅已发布）。
     * 传 categoryId 时在同分类内随机（详情页推荐传 excludeId 排除当前项目）；
     * 不传 categoryId 时从全部项目中随机（首页底部推荐）。
     *
     * @param categoryId 分类ID（可空）
     * @param excludeId  排除的项目ID（可空，详情页传当前项目避免推荐自己）
     * @param limit      推荐条数，默认 4，限制在 1-8 之间
     */
    @GetMapping("/recommend")
    public Result<List<HeritageInfo>> recommend(@RequestParam(required = false) Long categoryId,
                                                @RequestParam(required = false) Long excludeId,
                                                @RequestParam(defaultValue = "4") Integer limit) {
        int safeLimit = Math.max(1, Math.min(limit, 8));
        List<HeritageInfo> list = heritageInfoService.lambdaQuery()
                .isNotNull(HeritageInfo::getPublishTime)
                .eq(categoryId != null, HeritageInfo::getCategoryId, categoryId)
                .ne(excludeId != null, HeritageInfo::getId, excludeId)
                .last("ORDER BY RAND() LIMIT " + safeLimit)
                .list();
        return Result.ok(list);
    }

    /**
     * 随机获取一条非遗冷知识（首页「今日非遗」板块使用）
     *
     * @param heritageId 非遗项目ID（可空，空时从全部冷知识中随机）
     */
    @GetMapping("/tip")
    public Result<HeritageTip> randomTip(@RequestParam(required = false) Long heritageId) {
        return Result.ok(heritageTipService.randomOne(heritageId));
    }
}
