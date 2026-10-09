package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.QuizQuestion;
import com.heritage.mapper.QuizQuestionMapper;
import com.heritage.service.QuizQuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * 非遗小测验题库Service实现
 */
@Service
public class QuizQuestionServiceImpl extends ServiceImpl<QuizQuestionMapper, QuizQuestion> implements QuizQuestionService {

    @Override
    public List<QuizQuestion> listByHeritageId(Long heritageId) {
        return this.lambdaQuery()
                .eq(QuizQuestion::getHeritageId, heritageId)
                .orderByAsc(QuizQuestion::getId)
                .list();
    }

    @Override
    public List<QuizQuestion> getRandomQuestionsByHeritageId(Long heritageId, int count) {
        List<QuizQuestion> allQuestions = this.lambdaQuery()
                .eq(QuizQuestion::getHeritageId, heritageId)
                .list();
        
        if (allQuestions.isEmpty()) {
            return List.of();
        }
        
        // 随机抽取题目
        Random random = new Random();
        int size = Math.min(count, allQuestions.size());
        List<QuizQuestion> result = new java.util.ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            int randomIndex = random.nextInt(allQuestions.size());
            result.add(allQuestions.get(randomIndex));
            allQuestions.remove(randomIndex);
        }
        
        return result;
    }
}