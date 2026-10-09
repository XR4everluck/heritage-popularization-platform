package com.heritage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.QuizQuestion;
import com.heritage.entity.QuizRecord;
import com.heritage.exception.BusinessException;
import com.heritage.mapper.QuizRecordMapper;
import com.heritage.service.QuizQuestionService;
import com.heritage.service.QuizRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 非遗小测验答题记录实现：判题、计分、积分汇总
 */
@Service
@RequiredArgsConstructor
public class QuizRecordServiceImpl extends ServiceImpl<QuizRecordMapper, QuizRecord> implements QuizRecordService {

    /** 题目未配置分值时使用的默认分值 */
    private static final int DEFAULT_SCORE = 20;

    private final QuizQuestionService quizQuestionService;

    @Override
    public Integer saveRecord(Long userId, Long questionId, String answer) {
        QuizQuestion question = quizQuestionService.getById(questionId);
        if (question == null) {
            throw new BusinessException("题目不存在");
        }

        // 答案忽略大小写比较，避免前端传小写字母时误判为答错
        boolean correct = question.getCorrectAnswer() != null
                && question.getCorrectAnswer().equalsIgnoreCase(answer);
        int score = correct ? (question.getScore() == null ? DEFAULT_SCORE : question.getScore()) : 0;

        QuizRecord record = new QuizRecord();
        record.setUserId(userId);
        record.setHeritageId(question.getHeritageId());
        record.setQuestionId(questionId);
        record.setUserAnswer(answer);
        record.setIsCorrect(correct);
        record.setScore(score);
        record.setCreateTime(LocalDateTime.now());
        this.save(record);

        return score;
    }

    @Override
    public List<QuizRecord> listByUserId(Long userId) {
        return this.lambdaQuery()
                .eq(QuizRecord::getUserId, userId)
                .orderByDesc(QuizRecord::getCreateTime)
                .list();
    }

    @Override
    public Integer getTotalScoreByUserId(Long userId) {
        // 逐条累加得分：答错记 0 分，未记录得分的旧数据按 0 处理
        return this.lambdaQuery()
                .eq(QuizRecord::getUserId, userId)
                .list()
                .stream()
                .mapToInt(r -> r.getScore() == null ? 0 : r.getScore())
                .sum();
    }
}
