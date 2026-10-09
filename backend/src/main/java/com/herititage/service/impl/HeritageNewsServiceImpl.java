package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.HeritageNews;
import com.heritage.mapper.HeritageNewsMapper;
import com.heritage.service.HeritageNewsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 非遗科普快讯业务实现
 */
@Service
public class HeritageNewsServiceImpl extends ServiceImpl<HeritageNewsMapper, HeritageNews> implements HeritageNewsService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HeritageNews saveNews(HeritageNews news) {
        if (news == null) {
            throw new IllegalArgumentException("快讯信息不能为空");
        }

        // 设置默认值
        if (news.getStatus() == null) {
            news.setStatus(1); // 默认启用
        }
        if (news.getIsTop() == null) {
            news.setIsTop(0); // 默认不置顶
        }
        if (news.getSort() == null) {
            news.setSort(0); // 默认排序
        }
        if (news.getDeleted() == null) {
            news.setDeleted(0); // 默认未删除
        }
        if (news.getPublishTime() == null) {
            news.setPublishTime(LocalDateTime.now()); // 默认当前时间发布
        }

        news.setCreateTime(LocalDateTime.now());
        news.setUpdateTime(LocalDateTime.now());

        save(news);
        return news;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HeritageNews updateNews(HeritageNews news) {
        if (news == null || news.getId() == null) {
            throw new IllegalArgumentException("快讯信息不能为空");
        }

        // 检查快讯是否存在
        HeritageNews existing = getById(news.getId());
        if (existing == null) {
            throw new RuntimeException("快讯不存在");
        }

        news.setUpdateTime(LocalDateTime.now());
        updateById(news);
        return news;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNews(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("快讯ID不能为空");
        }

        HeritageNews news = getById(id);
        if (news == null) {
            throw new RuntimeException("快讯不存在");
        }

        // 逻辑删除
        news.setDeleted(1);
        news.setUpdateTime(LocalDateTime.now());
        return updateById(news);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new IllegalArgumentException("快讯ID和状态不能为空");
        }

        HeritageNews news = getById(id);
        if (news == null) {
            throw new RuntimeException("快讯不存在");
        }

        news.setStatus(status);
        news.setUpdateTime(LocalDateTime.now());
        return updateById(news);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTop(Long id, Integer isTop) {
        if (id == null || isTop == null) {
            throw new IllegalArgumentException("快讯ID和置顶状态不能为空");
        }

        HeritageNews news = getById(id);
        if (news == null) {
            throw new RuntimeException("快讯不存在");
        }

        news.setIsTop(isTop);
        news.setUpdateTime(LocalDateTime.now());
        return updateById(news);
    }

    @Override
    public List<HeritageNews> getByHeritageId(Long heritageId) {
        if (heritageId == null) {
            throw new IllegalArgumentException("非遗项目ID不能为空");
        }

        return list(new LambdaQueryWrapper<HeritageNews>()
                .eq(HeritageNews::getHeritageId, heritageId)
                .eq(HeritageNews::getStatus, 1)
                .eq(HeritageNews::getDeleted, 0)
                .orderByAsc(HeritageNews::getIsTop)
                .orderByDesc(HeritageNews::getPublishTime)
        );
    }

    @Override
    public List<HeritageNews> getTopNews() {
        return list(new LambdaQueryWrapper<HeritageNews>()
                .eq(HeritageNews::getIsTop, 1)
                .eq(HeritageNews::getStatus, 1)
                .eq(HeritageNews::getDeleted, 0)
                .orderByDesc(HeritageNews::getSort)
                .orderByDesc(HeritageNews::getPublishTime)
                .last("LIMIT 10") // 最多返回10条置顶快讯
        );
    }

    @Override
    public List<HeritageNews> getLatestNews(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认10条
        }
        if (limit > 50) {
            limit = 50; // 最多50条
        }

        return list(new LambdaQueryWrapper<HeritageNews>()
                .eq(HeritageNews::getStatus, 1)
                .eq(HeritageNews::getDeleted, 0)
                .orderByDesc(HeritageNews::getPublishTime)
                .last("LIMIT " + limit)
        );
    }
}