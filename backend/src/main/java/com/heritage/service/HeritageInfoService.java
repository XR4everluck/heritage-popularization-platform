package com.heritage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.HeritageInfo;
import com.heritage.vo.HeritageVO;

/**
 * 非遗项目业务接口：继承 IService 获得通用 CRUD，另含分类名组装与浏览量统计方法
 */
public interface HeritageInfoService extends IService<HeritageInfo> {

    /**
     * 分页查询非遗列表并组装分类名称
     *
     * @param onlyPublished true 时仅返回已发布（publish_time 非空）的项目，前台用；false 全量，后台用
     */
    Page<HeritageVO> pageWithCategory(Page<HeritageInfo> page, Long categoryId, String keyword,
                                       String level, String region, Integer isNews, boolean onlyPublished);

    /**
     * 查询非遗详情并组装分类名称；不存在或（仅前台时）未发布均视为资源不存在
     */
    HeritageVO getDetail(Long id, boolean onlyPublished);

    /**
     * 浏览量自增（view_count = view_count + 1，数据库原子操作）
     */
    void increaseViewCount(Long id);

    /**
     * 多维度关联推荐：基于同分类、同地区、同级别、同一传承人的加权推荐
     *
     * @param heritageId 当前非遗项目ID（用于排除自身）
     * @param limit 推荐条数，默认 4
     * @return 推荐的非遗项目列表
     */
    List<HeritageInfo> recommendByMultiDimension(Long heritageId, Integer limit);

    /**
     * 按地区分组统计非遗数量
     *
     * @return 地区名称到非遗数量的映射
     */
    Map<String, Long> countByRegion();
}
