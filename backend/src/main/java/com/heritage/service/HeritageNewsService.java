package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.HeritageNews;

import java.util.List;

/**
 * 非遗科普快讯业务接口
 */
public interface HeritageNewsService extends IService<HeritageNews> {

    /**
     * 保存快讯信息
     *
     * @param news 快讯信息
     * @return 保存后的快讯信息
     */
    HeritageNews saveNews(HeritageNews news);

    /**
     * 更新快讯信息
     *
     * @param news 快讯信息
     * @return 更新后的快讯信息
     */
    HeritageNews updateNews(HeritageNews news);

    /**
     * 删除快讯信息
     *
     * @param id 快讯ID
     * @return 是否删除成功
     */
    boolean deleteNews(Long id);

    /**
     * 启用/禁用快讯
     *
     * @param id     快讯ID
     * @param status 状态：0-禁用，1-启用
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 设置/取消置顶
     *
     * @param id   快讯ID
     * @param isTop 是否置顶：0-不置顶，1-置顶
     * @return 是否更新成功
     */
    boolean updateTop(Long id, Integer isTop);

    /**
     * 根据非遗项目ID获取快讯列表
     *
     * @param heritageId 非遗项目ID
     * @return 快讯列表
     */
    List<HeritageNews> getByHeritageId(Long heritageId);

    /**
     * 获取置顶的快讯列表
     *
     * @return 置顶快讯列表
     */
    List<HeritageNews> getTopNews();

    /**
     * 获取最新的快讯列表
     *
     * @param limit 数量限制
     * @return 最新快讯列表
     */
    List<HeritageNews> getLatestNews(Integer limit);
}