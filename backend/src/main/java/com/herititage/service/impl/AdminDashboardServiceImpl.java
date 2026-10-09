package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heritage.entity.HeritageInfo;
import com.heritage.entity.Inheritor;
import com.heritage.entity.HeritageTip;
import com.heritage.entity.QuizRecord;
import com.heritage.entity.SysUser;
import com.heritage.mapper.HeritageInfoMapper;
import com.heritage.mapper.InheritorMapper;
import com.heritage.mapper.HeritageTipMapper;
import com.heritage.mapper.QuizRecordMapper;
import com.heritage.mapper.SysUserMapper;
import com.heritage.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台数据看板统计服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final HeritageInfoMapper heritageInfoMapper;
    private final InheritorMapper inheritorMapper;
    private final HeritageTipMapper heritageTipMapper;
    private final QuizRecordMapper quizRecordMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public Map<String, Object> getBasicStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 非遗项目总数
        Long heritageCount = heritageInfoMapper.selectCount(
                new LambdaQueryWrapper<HeritageInfo>()
                        .eq(HeritageInfo::getDeleted, 0)
        );
        stats.put("heritageCount", heritageCount);
        
        // 用户总数
        Long userCount = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
        );
        stats.put("userCount", userCount);
        
        // 课程总数
        Long courseCount = heritageInfoMapper.selectCount(
                new LambdaQueryWrapper<HeritageInfo>()
                        .eq(HeritageInfo::getDeleted, 0)
                        .isNotNull(HeritageInfo::getPublishTime)
        );
        stats.put("courseCount", courseCount);
        
        // 测验题目总数
        Long questionCount = heritageInfoMapper.selectCount(
                new LambdaQueryWrapper<HeritageInfo>()
                        .eq(HeritageInfo::getDeleted, 0)
        );
        stats.put("questionCount", questionCount);
        
        return stats;
    }

    @Override
    public Long getQuizParticipationCount() {
        return quizRecordMapper.selectCount(
                new LambdaQueryWrapper<QuizRecord>()
                        .ge(QuizRecord::getCreateTime, LocalDateTime.now().minusDays(30))
        );
    }

    @Override
    public Long getTotalScoreCount() {
        // 计算所有用户的总积分
        List<SysUser> users = sysUserMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .select(SysUser::getTotalScore)
                        .gt(SysUser::getTotalScore, 0)
        );
        
        return users.stream()
                .mapToLong(SysUser::getTotalScore)
                .sum();
    }

    @Override
    public Long getInheritorCount() {
        return inheritorMapper.selectCount(
                new LambdaQueryWrapper<Inheritor>()
                        .eq(Inheritor::getDeleted, 0)
                        .eq(Inheritor::getStatus, 1)
        );
    }

    @Override
    public Long getTipCount() {
        return heritageTipMapper.selectCount(
                new LambdaQueryWrapper<HeritageTip>()
                        .eq(HeritageTip::getDeleted, 0)
                        .eq(HeritageTip::getStatus, 1)
        );
    }

    @Override
    public Map<String, Long> getRegionDistribution() {
        List<Map<String, Object>> regionStats = heritageInfoMapper.selectMaps(
                new LambdaQueryWrapper<HeritageInfo>()
                        .select(HeritageInfo::getRegion)
                        .groupBy(HeritageInfo::getRegion)
                        .eq(HeritageInfo::getDeleted, 0)
        );
        
        Map<String, Long> distribution = new HashMap<>();
        for (Map<String, Object> stat : regionStats) {
            String region = (String) stat.get("region");
            Long count = (Long) stat.get("count");
            distribution.put(region, count);
        }
        
        return distribution;
    }

    @Override
    public Map<String, Long> getLevelDistribution() {
        List<Map<String, Object>> levelStats = heritageInfoMapper.selectMaps(
                new LambdaQueryWrapper<HeritageInfo>()
                        .select(HeritageInfo::getLevel)
                        .groupBy(HeritageInfo::getLevel)
                        .eq(HeritageInfo::getDeleted, 0)
        );
        
        Map<String, Long> distribution = new HashMap<>();
        for (Map<String, Object> stat : levelStats) {
            String level = (String) stat.get("level");
            Long count = (Long) stat.get("count");
            distribution.put(level, count);
        }
        
        return distribution;
    }

    @Override
    public Map<String, Long> getQuizTrend() {
        Map<String, Long> trend = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        // 获取最近7天的数据
        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            String dateStr = date.format(formatter);
            
            Long count = quizRecordMapper.selectCount(
                    new LambdaQueryWrapper<QuizRecord>()
                            .ge(QuizRecord::getCreateTime, date.withHour(0).withMinute(0).withSecond(0))
                            .lt(QuizRecord::getCreateTime, date.plusDays(1).withHour(0).withMinute(0).withSecond(0))
            );
            
            trend.put(dateStr, count);
        }
        
        return trend;
    }

    @Override
    public Map<String, Object> getPopularHeritage(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        List<HeritageInfo> popularHeritage = heritageInfoMapper.selectList(
                new LambdaQueryWrapper<HeritageInfo>()
                        .eq(HeritageInfo::getDeleted, 0)
                        .orderByDesc(HeritageInfo::getViewCount)
                        .last("LIMIT " + limit)
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", popularHeritage);
        result.put("total", popularHeritage.size());
        
        return result;
    }
}