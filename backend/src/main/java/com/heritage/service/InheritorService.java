package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.Inheritor;

/**
 * 传承人信息业务接口
 */
public interface InheritorService extends IService<Inheritor> {

    /**
     * 保存传承人信息
     *
     * @param inheritor 传承人信息
     * @return 保存后的传承人信息
     */
    Inheritor saveInheritor(Inheritor inheritor);

    /**
     * 更新传承人信息
     *
     * @param inheritor 传承人信息
     * @return 更新后的传承人信息
     */
    Inheritor updateInheritor(Inheritor inheritor);

    /**
     * 删除传承人信息
     *
     * @param id 传承人ID
     * @return 是否删除成功
     */
    boolean deleteInheritor(Long id);

    /**
     * 启用/禁用传承人
     *
     * @param id     传承人ID
     * @param status 状态：0-禁用，1-启用
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);
}