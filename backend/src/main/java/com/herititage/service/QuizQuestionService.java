package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.QuizQuestion;

import java.util.List;

/**
 * 非遗小测验题库Service
 */
public interface QuizQuestionService extends IService<QuizQuestion> {
    /**
     * 根据非遗ID查询题库列表
     */
    List<QuizQuestion> listByHeritageId(Long heritageId);

    /**
     * 根据非遗ID随机抽取指定数量的题目
     */
    List<QuizQuestion> getRandomQuestionsByHeritageId(Long heritageId, int count);
}