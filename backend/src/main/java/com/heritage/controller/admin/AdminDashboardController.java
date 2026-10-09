package com.heritage.controller.admin;

import com.heritage.common.Result;
import com.heritage.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台数据看板控制器
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    /**
     * 获取基础统计数据
     *
     * @return 基础统计数据
     */
    @GetMapping("/basic")
    public Result<Map<String, Object>> getBasicStats() {
        Map<String, Object> stats = adminDashboardService.getBasicStats();
        return Result.ok(stats);
    }

    /**
     * 获取答题人次统计
     *
     * @return 答题人次
     */
    @GetMapping("/quiz-participation")
    public Result<Long> getQuizParticipationCount() {
        Long count = adminDashboardService.getQuizParticipationCount();
        return Result.ok(count);
    }

    /**
     * 获取累计积分总数
     *
     * @return 累计积分总数
     */
    @GetMapping("/total-score")
    public Result<Long> getTotalScoreCount() {
        Long count = adminDashboardService.getTotalScoreCount();
        return Result.ok(count);
    }

    /**
     * 获取传承人数量
     *
     * @return 传承人数量
     */
    @GetMapping("/inheritor-count")
    public Result<Long> getInheritorCount() {
        Long count = adminDashboardService.getInheritorCount();
        return Result.ok(count);
    }

    /**
     * 获取冷知识总数
     *
     * @return 冷知识总数
     */
    @GetMapping("/tip-count")
    public Result<Long> getTipCount() {
        Long count = adminDashboardService.getTipCount();
        return Result.ok(count);
    }

    /**
     * 获取地区分布统计
     *
     * @return 地区分布统计
     */
    @GetMapping("/region-distribution")
    public Result<Map<String, Long>> getRegionDistribution() {
        Map<String, Long> distribution = adminDashboardService.getRegionDistribution();
        return Result.ok(distribution);
    }

    /**
     * 获取非遗级别分布统计
     *
     * @return 非遗级别分布统计
     */
    @GetMapping("/level-distribution")
    public Result<Map<String, Long>> getLevelDistribution() {
        Map<String, Long> distribution = adminDashboardService.getLevelDistribution();
        return Result.ok(distribution);
    }

    /**
     * 获取最近7天的答题趋势
     *
     * @return 最近7天的答题趋势
     */
    @GetMapping("/quiz-trend")
    public Result<Map<String, Long>> getQuizTrend() {
        Map<String, Long> trend = adminDashboardService.getQuizTrend();
        return Result.ok(trend);
    }

    /**
     * 获取热门非遗项目排行
     *
     * @param limit 数量限制
     * @return 热门非遗项目排行
     */
    @GetMapping("/popular-heritage")
    public Result<Map<String, Object>> getPopularHeritage(@RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> popular = adminDashboardService.getPopularHeritage(limit);
        return Result.ok(popular);
    }

    /**
     * 获取完整的数据看板信息
     *
     * @return 完整的数据看板信息
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new java.util.HashMap<>();
        
        // 基础统计数据
        overview.put("basicStats", adminDashboardService.getBasicStats());
        
        // 新增统计指标
        overview.put("quizParticipationCount", adminDashboardService.getQuizParticipationCount());
        overview.put("totalScoreCount", adminDashboardService.getTotalScoreCount());
        overview.put("inheritorCount", adminDashboardService.getInheritorCount());
        overview.put("tipCount", adminDashboardService.getTipCount());
        
        // 分布统计
        overview.put("regionDistribution", adminDashboardService.getRegionDistribution());
        overview.put("levelDistribution", adminDashboardService.getLevelDistribution());
        
        // 趋势数据
        overview.put("quizTrend", adminDashboardService.getQuizTrend());
        
        // 热门项目
        overview.put("popularHeritage", adminDashboardService.getPopularHeritage(10));
        
        return Result.ok(overview);
    }
}