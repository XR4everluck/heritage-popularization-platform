package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.HeritageTip;

import java.util.List;

/**
 * 非遗冷知识业务接口
 */
public interface HeritageTipService extends IService<HeritageTip> {

    /**
     * 保存冷知识信息
     *
     * @param tip 冷知识信息
     * @return 保存后的冷知识信息
     */
    HeritageTip saveTip(HeritageTip tip);

    /**
     * 更新冷知识信息
     *
     * @param tip 冷知识信息
     * @return 更新后的冷知识信息
     */
    HeritageTip updateTip(HeritageTip tip);

    /**
     * 删除冷知识信息
     *
     * @param id 冷知识ID
     * @return 是否删除成功
     */
    boolean deleteTip(Long id);

    /**
     * 启用/禁用冷知识
     *
     * @param id     冷知识ID
     * @param status 状态：0-禁用，1-启用
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 根据非遗项目ID获取冷知识列表
     *
     * @param heritageId 非遗项目ID
     * @return 冷知识列表
     */
    List<HeritageTip> getByHeritageId(Long heritageId);

    /**
     * 随机获取一条冷知识
     *
     * @param heritageId 非遗项目ID（可选）
     * @return 冷知识信息
     */
    HeritageTip randomOne(Long heritageId);
}