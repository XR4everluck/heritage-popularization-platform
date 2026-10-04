package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.common.ResultCode;
import com.heritage.entity.HeritageInfo;
import com.heritage.entity.UserCollection;
import com.heritage.exception.BusinessException;
import com.heritage.mapper.UserCollectionMapper;
import com.heritage.service.HeritageInfoService;
import com.heritage.service.UserCollectionService;
import com.heritage.vo.CollectionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户收藏业务实现：收藏/取消与非遗项目收藏数联动维护（冗余统计字段）
 */
@Service
@RequiredArgsConstructor
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService {

    private final HeritageInfoService heritageInfoService;

    @Override
    public void add(Long userId, Long heritageId) {
        boolean exists = this.lambdaQuery().eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getHeritageId, heritageId).exists();
        if (exists) {
            throw new BusinessException("已收藏过该项目，请勿重复收藏");
        }
        if (heritageInfoService.getById(heritageId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        UserCollection collection = new UserCollection();
        collection.setUserId(userId);
        collection.setHeritageId(heritageId);
        this.save(collection);
        // 收藏数冗余字段联动 +1（数据库原子自增）
        heritageInfoService.lambdaUpdate().eq(HeritageInfo::getId, heritageId)
                .setSql("collection_count = collection_count + 1").update();
    }

    @Override
    public void remove(Long userId, Long heritageId) {
        UserCollection record = this.lambdaQuery().eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getHeritageId, heritageId).one();
        if (record == null) {
            throw new BusinessException("尚未收藏该项目");
        }
        // 收藏记录无保留价值，物理删除
        this.removeById(record.getId());
        // GREATEST 兜底：数据不一致导致计数为0时不再减成负数
        heritageInfoService.lambdaUpdate().eq(HeritageInfo::getId, heritageId)
                .setSql("collection_count = GREATEST(collection_count - 1, 0)").update();
    }

    @Override
    public List<CollectionVO> listMy(Long userId) {
        List<UserCollection> list = this.lambdaQuery().eq(UserCollection::getUserId, userId)
                .orderByDesc(UserCollection::getCreateTime).list();
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> heritageIds = list.stream().map(UserCollection::getHeritageId).distinct().collect(Collectors.toList());
        // listByIds 对逻辑删除实体自动拼接 deleted=0，已删除的非遗项目不会出现在结果中
        Map<Long, HeritageInfo> heritages = heritageInfoService.listByIds(heritageIds).stream()
                .collect(Collectors.toMap(HeritageInfo::getId, h -> h));
        List<CollectionVO> vos = new ArrayList<>();
        for (UserCollection collection : list) {
            HeritageInfo info = heritages.get(collection.getHeritageId());
            if (info == null) {
                continue;
            }
            CollectionVO vo = new CollectionVO();
            vo.setId(collection.getId());
            vo.setHeritageId(info.getId());
            vo.setName(info.getName());
            vo.setCoverImage(info.getCoverImage());
            vo.setLevel(info.getLevel());
            vo.setRegion(info.getRegion());
            vo.setCollectionTime(collection.getCreateTime());
            vos.add(vo);
        }
        return vos;
    }
}
