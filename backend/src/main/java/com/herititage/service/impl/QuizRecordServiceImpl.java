package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.QuizQuestion;
import com.heritage.entity.QuizRecord;
import com.heritage.mapper.QuizQuestionMapper;
import com.heritage.mapper.QuizRecordMapper;
import com.heritage.service.QuizQuestionService;
import com.heritage.service.QuizRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 非遗小测验答题记录Service实现
 */
@Service
@RequiredArgsConstructor
public class QuizRecordServiceImpl extends ServiceImpl<QuizRecordMapper, QuizRecord> implements QuizRecordService {

    private final QuizQuestionService quizQuestionService;

    @Override
    public Integer saveRecord(Long userId, Long questionId, String answer) {
        // 查询题目信息
        QuizQuestion question = quizQuestionService.getById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 判断答案是否正确
        boolean isCorrect = question.getAnswer().equalsIgnoreCase(answer);
        int score = isCorrect ? question.getScore() : 0;

        // 保存答题记录
        QuizRecord record = new QuizRecord();
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setAnswer(answer);
        record.setIsCorrect(isCorrect ? 1 : 0);
        record.setAnswerTime(LocalDateTime.now());
        this.save(record);

        return score;
    }

    @Override
    public List<QuizRecord> listByUserId(Long userId) {
        return this.lambdaQuery()
                .eq(QuizRecord::getUserId, userId)
                .orderByDesc(QuizRecord::getAnswerTime)
                .list();
    }

    @Override
    public Integer getTotalScoreByUserId(Long userId) {
        return this.lambdaQuery()
                .eq(QuizRecord::getUserId, userId)
                .sum(QuizRecord::getIsCorrect, Integer.class);
    }
}