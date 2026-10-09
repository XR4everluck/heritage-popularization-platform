package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.entity.Course;
import com.heritage.entity.HeritageInfo;
import com.heritage.entity.HeritageTip;
import com.heritage.entity.Inheritor;
import com.heritage.entity.QuizQuestion;
import com.heritage.entity.QuizRecord;
import com.heritage.entity.SysUser;
import com.heritage.mapper.CourseMapper;
import com.heritage.mapper.HeritageInfoMapper;
import com.heritage.mapper.HeritageTipMapper;
import com.heritage.mapper.InheritorMapper;
import com.heritage.mapper.QuizQuestionMapper;
import com.heritage.mapper.QuizRecordMapper;
import com.heritage.mapper.SysUserMapper;
import com.heritage.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台数据看板统计服务实现
 *
 * <p>各统计项均直接走对应表的 count/聚合，避免跨表误统计。</p>
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final HeritageInfoMapper heritageInfoMapper;
    private final InheritorMapper inheritorMapper;
    private final HeritageTipMapper heritageTipMapper;
    private final QuizRecordMapper quizRecordMapper;
    private final QuizQuestionMapper quizQuestionMapper;
    private final CourseMapper courseMapper;
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

        // 科普专题（课程）总数
        Long courseCount = courseMapper.selectCount(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getDeleted, 0)
        );
        stats.put("courseCount", courseCount);

        // 测验题目总数
        Long questionCount = quizQuestionMapper.selectCount(
                new LambdaQueryWrapper<QuizQuestion>()
                        .eq(QuizQuestion::getDeleted, 0)
        );
        stats.put("questionCount", questionCount);

        return stats;
    }

    @Override
    public Long getQuizParticipationCount() {
        // 答题人次：累计答题记录条数（同一次测验的每道题各计一次）
        return quizRecordMapper.selectCount(null);
    }

    @Override
    public Long getTotalScoreCount() {
        // 累计积分总数：汇总所有用户的 total_score（答题得分实时累加在该字段上）
        List<SysUser> users = sysUserMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .select(SysUser::getTotalScore)
                        .gt(SysUser::getTotalScore, 0)
        );

        return users.stream()
                .mapToLong(u -> u.getTotalScore() == null ? 0L : u.getTotalScore())
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
        );
    }

    @Override
    public Map<String, Long> getRegionDistribution() {
        return countGroupBy("region", heritageInfoMapper);
    }

    @Override
    public Map<String, Long> getLevelDistribution() {
        return countGroupBy("level", heritageInfoMapper);
    }

    /**
     * 按指定列分组统计非遗数量
     *
     * <p>注意：selectMaps 只会返回 SELECT 中显式列出的列，因此数量必须用
     * COUNT(*) 显式取别名，否则结果里根本没有该键。</p>
     */
    private Map<String, Long> countGroupBy(String column, HeritageInfoMapper mapper) {
        List<Map<String, Object>> rows = mapper.selectMaps(
                new QueryWrapper<HeritageInfo>()
                        .select(column, "COUNT(*) AS cnt")
                        .groupBy(column)
        );

        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Object key = row.get(column);
            Object cnt = row.get("cnt");
            distribution.put(key == null ? "未知" : key.toString(),
                    cnt == null ? 0L : ((Number) cnt).longValue());
        }
        return distribution;
    }

    @Override
    public Map<String, Long> getQuizTrend() {
        Map<String, Long> trend = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        // 最近 7 天（含今天）每天的答题记录数
        for (int i = 6; i >= 0; i--) {
            LocalDateTime dayStart = LocalDateTime.now().minusDays(i)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);

            Long count = quizRecordMapper.selectCount(
                    new LambdaQueryWrapper<QuizRecord>()
                            .ge(QuizRecord::getCreateTime, dayStart)
                            .lt(QuizRecord::getCreateTime, dayStart.plusDays(1))
            );

            trend.put(dayStart.format(formatter), count);
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
