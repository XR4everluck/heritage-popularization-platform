package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.HeritageTip;
import com.heritage.exception.BusinessException;
import com.heritage.mapper.HeritageTipMapper;
import com.heritage.service.HeritageTipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 非遗冷知识业务实现：通用 CRUD + 后台管理 + 随机获取
 */
@Service
public class HeritageTipServiceImpl extends ServiceImpl<HeritageTipMapper, HeritageTip> implements HeritageTipService {

    @Override
    public HeritageTip randomOne(Long heritageId) {
        return this.lambdaQuery()
                .eq(heritageId != null, HeritageTip::getHeritageId, heritageId)
                .eq(HeritageTip::getStatus, 1)
                .last("ORDER BY RAND() LIMIT 1")
                .one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HeritageTip saveTip(HeritageTip tip) {
        if (tip == null || tip.getContent() == null || tip.getContent().trim().isEmpty()) {
            throw new BusinessException("冷知识内容不能为空");
        }
        if (tip.getStatus() == null) {
            tip.setStatus(1);
        }
        if (tip.getSort() == null) {
            tip.setSort(0);
        }
        Date now = new Date();
        tip.setCreateTime(now);
        tip.setUpdateTime(now);
        this.save(tip);
        return tip;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HeritageTip updateTip(HeritageTip tip) {
        if (tip == null || tip.getId() == null) {
            throw new BusinessException("冷知识ID不能为空");
        }
        if (this.getById(tip.getId()) == null) {
            throw new BusinessException("冷知识不存在");
        }
        tip.setUpdateTime(new Date());
        this.updateById(tip);
        return tip;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTip(Long id) {
        if (id == null) {
            throw new BusinessException("冷知识ID不能为空");
        }
        // removeById 走全局逻辑删除配置，仅将 deleted 标记为 1
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new BusinessException("冷知识ID和状态不能为空");
        }
        HeritageTip update = new HeritageTip();
        update.setId(id);
        update.setStatus(status);
        update.setUpdateTime(new Date());
        return this.updateById(update);
    }

    @Override
    public List<HeritageTip> getByHeritageId(Long heritageId) {
        return this.lambdaQuery()
                .eq(heritageId != null, HeritageTip::getHeritageId, heritageId)
                .orderByAsc(HeritageTip::getSort)
                .orderByDesc(HeritageTip::getCreateTime)
                .list();
    }
}
