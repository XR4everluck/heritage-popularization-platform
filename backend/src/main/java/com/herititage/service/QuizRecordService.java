package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.QuizRecord;

/**
 * 非遗小测验答题记录Service
 */
public interface QuizRecordService extends IService<QuizRecord> {
    /**
     * 保存答题记录并计算积分
     */
    Integer saveRecord(Long userId, Long questionId, String answer);

    /**
     * 查询用户答题记录
     */
    List<QuizRecord> listByUserId(Long userId);

    /**
     * 查询用户总积分
     */
    Integer getTotalScoreByUserId(Long userId);
}