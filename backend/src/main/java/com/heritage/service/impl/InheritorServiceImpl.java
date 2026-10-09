package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.Inheritor;
import com.heritage.mapper.InheritorMapper;
import com.heritage.service.InheritorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 传承人信息业务实现
 */
@Service
public class InheritorServiceImpl extends ServiceImpl<InheritorMapper, Inheritor> implements InheritorService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Inheritor saveInheritor(Inheritor inheritor) {
        if (inheritor == null) {
            throw new IllegalArgumentException("传承人信息不能为空");
        }

        // 设置默认值
        if (inheritor.getStatus() == null) {
            inheritor.setStatus(1); // 默认启用
        }
        if (inheritor.getSort() == null) {
            inheritor.setSort(0); // 默认排序
        }
        if (inheritor.getDeleted() == null) {
            inheritor.setDeleted(0); // 默认未删除
        }

        inheritor.setCreateTime(LocalDateTime.now());
        inheritor.setUpdateTime(LocalDateTime.now());

        save(inheritor);
        return inheritor;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Inheritor updateInheritor(Inheritor inheritor) {
        if (inheritor == null || inheritor.getId() == null) {
            throw new IllegalArgumentException("传承人信息不能为空");
        }

        // 检查传承人是否存在
        Inheritor existing = getById(inheritor.getId());
        if (existing == null) {
            throw new RuntimeException("传承人不存在");
        }

        inheritor.setUpdateTime(LocalDateTime.now());
        updateById(inheritor);
        return inheritor;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteInheritor(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("传承人ID不能为空");
        }

        Inheritor inheritor = getById(id);
        if (inheritor == null) {
            throw new RuntimeException("传承人不存在");
        }

        // 逻辑删除
        inheritor.setDeleted(1);
        inheritor.setUpdateTime(LocalDateTime.now());
        return updateById(inheritor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new IllegalArgumentException("传承人ID和状态不能为空");
        }

        Inheritor inheritor = getById(id);
        if (inheritor == null) {
            throw new RuntimeException("传承人不存在");
        }

        inheritor.setStatus(status);
        inheritor.setUpdateTime(LocalDateTime.now());
        return updateById(inheritor);
    }
}