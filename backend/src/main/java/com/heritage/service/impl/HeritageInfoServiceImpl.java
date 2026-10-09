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
