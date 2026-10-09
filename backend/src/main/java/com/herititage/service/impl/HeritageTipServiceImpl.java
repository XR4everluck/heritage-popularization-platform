package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.HeritageTip;
import com.heritage.mapper.HeritageTipMapper;
import com.heritage.service.HeritageTipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 非遗冷知识业务实现
 */
@Service
public class HeritageTipServiceImpl extends ServiceImpl<HeritageTipMapper, HeritageTip> implements HeritageTipService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HeritageTip saveTip(HeritageTip tip) {
        if (tip == null) {
            throw new IllegalArgumentException("冷知识信息不能为空");
        }

        // 设置默认值
        if (tip.getStatus() == null) {
            tip.setStatus(1); // 默认启用
        }
        if (tip.getSort() == null) {
            tip.setSort(0); // 默认排序
        }
        if (tip.getDeleted() == null) {
            tip.setDeleted(0); // 默认未删除
        }

        tip.setCreateTime(LocalDateTime.now());
        tip.setUpdateTime(LocalDateTime.now());

        save(tip);
        return tip;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HeritageTip updateTip(HeritageTip tip) {
        if (tip == null || tip.getId() == null) {
            throw new IllegalArgumentException("冷知识信息不能为空");
        }

        // 检查冷知识是否存在
        HeritageTip existing = getById(tip.getId());
        if (existing == null) {
            throw new RuntimeException("冷知识不存在");
        }

        tip.setUpdateTime(LocalDateTime.now());
        updateById(tip);
        return tip;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTip(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("冷知识ID不能为空");
        }

        HeritageTip tip = getById(id);
        if (tip == null) {
            throw new RuntimeException("冷知识不存在");
        }

        // 逻辑删除
        tip.setDeleted(1);
        tip.setUpdateTime(LocalDateTime.now());
        return updateById(tip);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new IllegalArgumentException("冷知识ID和状态不能为空");
        }

        HeritageTip tip = getById(id);
        if (tip == null) {
            throw new RuntimeException("冷知识不存在");
        }

        tip.setStatus(status);
        tip.setUpdateTime(LocalDateTime.now());
        return updateById(tip);
    }

    @Override
    public List<HeritageTip> getByHeritageId(Long heritageId) {
        if (heritageId == null) {
            throw new IllegalArgumentException("非遗项目ID不能为空");
        }

        return list(new LambdaQueryWrapper<HeritageTip>()
                .eq(HeritageTip::getHeritageId, heritageId)
                .eq(HeritageTip::getStatus, 1)
                .eq(HeritageTip::getDeleted, 0)
                .orderByAsc(HeritageTip::getSort)
                .orderByDesc(HeritageTip::getCreateTime)
        );
    }

    @Override
    public HeritageTip randomOne(Long heritageId) {
        LambdaQueryWrapper<HeritageTip> queryWrapper = new LambdaQueryWrapper<HeritageTip>()
                .eq(HeritageTip::getStatus, 1)
                .eq(HeritageTip::getDeleted, 0);

        if (heritageId != null) {
            queryWrapper.eq(HeritageTip::getHeritageId, heritageId);
        }

        List<HeritageTip> tips = list(queryWrapper);
        
        if (tips.isEmpty()) {
            return null;
        }

        // 随机选择一条
        Random random = new Random();
        int index = random.nextInt(tips.size());
        return tips.get(index);
    }
}