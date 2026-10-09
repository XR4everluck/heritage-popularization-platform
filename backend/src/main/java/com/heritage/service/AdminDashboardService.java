package com.heritage.service;

import java.util.Map;

/**
 * 后台数据看板统计服务
 */
public interface AdminDashboardService {

    /**
     * 获取基础统计数据
     *
     * @return 基础统计数据
     */
    Map<String, Object> getBasicStats();

    /**
     * 获取答题人次统计
     *
     * @return 答题人次
     */
    Long getQuizParticipationCount();

    /**
     * 获取累计积分总数
     *
     * @return 累计积分总数
     */
    Long getTotalScoreCount();

    /**
     * 获取传承人数量
     *
     * @return 传承人数量
     */
    Long getInheritorCount();

    /**
     * 获取冷知识总数
     *
     * @return 冷知识总数
     */
    Long getTipCount();

    /**
     * 获取地区分布统计
     *
     * @return 地区分布统计
     */
    Map<String, Long> getRegionDistribution();

    /**
     * 获取非遗级别分布统计
     *
     * @return 非遗级别分布统计
     */
    Map<String, Long> getLevelDistribution();

    /**
     * 获取最近7天的答题趋势
     *
     * @return 最近7天的答题趋势
     */
    Map<String, Long> getQuizTrend();

    /**
     * 获取热门非遗项目排行
     *
     * @param limit 数量限制
     * @return 热门非遗项目排行
     */
    Map<String, Object> getPopularHeritage(Integer limit);
}