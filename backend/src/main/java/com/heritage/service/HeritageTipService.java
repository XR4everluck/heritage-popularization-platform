package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.HeritageTip;

import java.util.List;

/**
 * 非遗冷知识业务接口：继承 IService 获得通用 CRUD，另含后台管理与随机获取方法
 */
public interface HeritageTipService extends IService<HeritageTip> {

    /**
     * 随机获取一条冷知识
     *
     * @param heritageId 非遗项目ID（可空，空时从全部冷知识中随机）
     * @return 随机一条启用的冷知识，无数据时返回 null
     */
    HeritageTip randomOne(Long heritageId);

    /**
     * 新增冷知识：校验内容非空并补齐状态/排序默认值
     */
    HeritageTip saveTip(HeritageTip tip);

    /**
     * 更新冷知识：要求 ID 存在
     */
    HeritageTip updateTip(HeritageTip tip);

    /**
     * 删除冷知识（逻辑删除）
     */
    boolean deleteTip(Long id);

    /**
     * 启用/禁用冷知识
     *
     * @param status 状态：0-禁用，1-启用
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 按非遗项目ID查询冷知识（含已禁用，供后台管理使用）
     */
    List<HeritageTip> getByHeritageId(Long heritageId);
}
