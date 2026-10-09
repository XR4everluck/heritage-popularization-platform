package com.heritage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.common.ResultCode;
import com.heritage.entity.HeritageCategory;
import com.heritage.entity.HeritageInfo;
import com.heritage.exception.BusinessException;
import com.heritage.mapper.HeritageInfoMapper;
import com.heritage.service.HeritageCategoryService;
import com.heritage.service.HeritageInfoService;
import com.heritage.vo.HeritageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 非遗项目业务实现：分页/详情组装分类名称（业务层关联，无物理外键），浏览量原子自增
 */
@Service
@RequiredArgsConstructor
public class HeritageInfoServiceImpl extends ServiceImpl<HeritageInfoMapper, HeritageInfo> implements HeritageInfoService {

    private final HeritageCategoryService heritageCategoryService;

    @Override
    public Page<HeritageVO> pageWithCategory(Page<HeritageInfo> page, Long categoryId, String keyword,
                                              String level, String region, Integer isNews, boolean onlyPublished) {
        LambdaQueryWrapper<HeritageInfo> qw = new LambdaQueryWrapper<>();
        qw.eq(categoryId != null, HeritageInfo::getCategoryId, categoryId)
                .like(StrUtil.isNotBlank(keyword), HeritageInfo::getName, keyword)
                .eq(StrUtil.isNotBlank(level), HeritageInfo::getLevel, level)
                .like(StrUtil.isNotBlank(region), HeritageInfo::getRegion, region)
                .eq(isNews != null, HeritageInfo::getIsNews, isNews)
                .isNotNull(onlyPublished, HeritageInfo::getPublishTime)
                .orderByDesc(HeritageInfo::getPublishTime)
                .orderByDesc(HeritageInfo::getId);
        Page<HeritageInfo> result = this.page(page, qw);
        Map<Long, String> categoryNames = categoryNameMap(result.getRecords());
        Page<HeritageVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(h -> toVO(h, categoryNames)).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public HeritageVO getDetail(Long id, boolean onlyPublished) {
        HeritageInfo info = this.getById(id);
        if (info == null || (onlyPublished && info.getPublishTime() == null)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        Map<Long, String> categoryNames = categoryNameMap(Collections.singletonList(info));
        return toVO(info, categoryNames);
    }

    @Override
    public void increaseViewCount(Long id) {
        if (this.getById(id) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        // 数据库原子自增，避免并发场景下的"读-改-写"丢失更新
        this.lambdaUpdate().eq(HeritageInfo::getId, id).setSql("view_count = view_count + 1").update();
    }

    @Override
    public List<HeritageInfo> recommendByMultiDimension(Long heritageId, Integer limit) {
        int safeLimit = Math.max(1, Math.min(limit != null ? limit : 4, 8));
        
        // 获取当前非遗项目信息
        HeritageInfo current = this.getById(heritageId);
        if (current == null) {
            return Collections.emptyList();
        }

        // 构建多维度查询条件
        LambdaQueryWrapper<HeritageInfo> qw = new LambdaQueryWrapper<>();
        qw.ne(HeritageInfo::getId, heritageId) // 排除当前项目
          .isNotNull(HeritageInfo::getPublishTime); // 只返回已发布的项目

        // 获取所有符合条件的非遗项目
        List<HeritageInfo> allCandidates = this.list(qw);
        
        // 如果没有候选项目，返回空列表
        if (allCandidates.isEmpty()) {
            return Collections.emptyList();
        }

        // 计算每个候选项目的权重分数
        List<HeritageInfo> scoredCandidates = allCandidates.stream()
                .map(candidate -> {
                    double score = 0.0;
                    
                    // 1. 同分类权重（40%）
                    if (current.getCategoryId() != null && current.getCategoryId().equals(candidate.getCategoryId())) {
                        score += 0.4;
                    }
                    
                    // 2. 同地区权重（30%）
                    if (current.getRegion() != null && current.getRegion().equals(candidate.getRegion())) {
                        score += 0.3;
                    }
                    
                    // 3. 同级别权重（20%）
                    if (current.getLevel() != null && current.getLevel().equals(candidate.getLevel())) {
                        score += 0.2;
                    }
                    
                    // 4. 同一传承人权重（10%）
                    if (current.getInheritorId() != null && current.getInheritorId().equals(candidate.getInheritorId())) {
                        score += 0.1;
                    }
                    
                    // 添加浏览量权重作为次要因素
                    score += (candidate.getViewCount() != null ? candidate.getViewCount() : 0) * 0.0001;
                    
                    return candidate;
                })
                .sorted((a, b) -> {
                    // 重新计算分数并排序
                    double scoreA = calculateScore(current, a);
                    double scoreB = calculateScore(current, b);
                    return Double.compare(scoreB, scoreA); // 降序排序
                })
                .limit(safeLimit)
                .collect(Collectors.toList());

        return scoredCandidates;
    }

    /**
     * 计算候选项目相对于当前项目的权重分数
     */
    private double calculateScore(HeritageInfo current, HeritageInfo candidate) {
        double score = 0.0;
        
        // 同分类权重（40%）
        if (current.getCategoryId() != null && current.getCategoryId().equals(candidate.getCategoryId())) {
            score += 0.4;
        }
        
        // 同地区权重（30%）
        if (current.getRegion() != null && current.getRegion().equals(candidate.getRegion())) {
            score += 0.3;
        }
        
        // 同级别权重（20%）
        if (current.getLevel() != null && current.getLevel().equals(candidate.getLevel())) {
            score += 0.2;
        }
        
        // 同一传承人权重（10%）
        if (current.getInheritorId() != null && current.getInheritorId().equals(candidate.getInheritorId())) {
            score += 0.1;
        }
        
        // 浏览量权重（作为次要因素）
        score += (candidate.getViewCount() != null ? candidate.getViewCount() : 0) * 0.0001;
        
        return score;
    }

    @Override
    public Map<String, Long> countByRegion() {
        List<HeritageInfo> heritageList = this.lambdaQuery()
                .isNotNull(HeritageInfo::getPublishTime)
                .isNotNull(HeritageInfo::getRegion)
                .list();

        return heritageList.stream()
                .collect(Collectors.groupingBy(
                        heritage -> heritage.getRegion() != null ? heritage.getRegion() : "未知地区",
                        Collectors.counting()
                ));
    }

    /** 批量查询分类名称映射（一次查询，避免循环单查） */
    private Map<Long, String> categoryNameMap(List<HeritageInfo> records) {
        if (records.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> categoryIds = records.stream().map(HeritageInfo::getCategoryId).distinct().collect(Collectors.toList());
        return heritageCategoryService.listByIds(categoryIds).stream()
                .collect(Collectors.toMap(HeritageCategory::getId, HeritageCategory::getName));
    }

    private HeritageVO toVO(HeritageInfo info, Map<Long, String> categoryNames) {
        HeritageVO vo = new HeritageVO();
        BeanUtils.copyProperties(info, vo);
        vo.setCategoryName(categoryNames.get(info.getCategoryId()));
        return vo;
    }
}
