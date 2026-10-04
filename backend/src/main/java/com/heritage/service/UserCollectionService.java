package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.UserCollection;
import com.heritage.vo.CollectionVO;

import java.util.List;

/**
 * 用户收藏业务接口：继承 IService 获得通用 CRUD，另含收藏/取消/我的收藏业务方法
 */
public interface UserCollectionService extends IService<UserCollection> {

    /**
     * 添加收藏：防重复（联合唯一索引兜底），成功后非遗项目收藏数 +1
     */
    void add(Long userId, Long heritageId);

    /**
     * 取消收藏：物理删除收藏记录，非遗项目收藏数 -1
     */
    void remove(Long userId, Long heritageId);

    /**
     * 我的收藏列表：组装非遗名称/封面/级别/地区，已删除的非遗项目自动跳过
     */
    List<CollectionVO> listMy(Long userId);
}
