package com.heritage.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heritage.common.Result;
import com.heritage.entity.QuizQuestion;
import com.heritage.entity.QuizRecord;
import com.heritage.service.QuizQuestionService;
import com.heritage.service.QuizRecordService;
import com.heritage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 前台测验接口：随机题目、提交答题、查询积分
 */
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class FrontQuizController {

    private final QuizQuestionService quizQuestionService;
    private final QuizRecordService quizRecordService;
    private final UserService userService;

    /**
     * 根据非遗ID随机抽取5道题
     */
    @GetMapping("/random-questions")
    public Result<List<QuizQuestion>> getRandomQuestions(@RequestParam Long heritageId) {
        List<QuizQuestion> questions = quizQuestionService.getRandomQuestionsByHeritageId(heritageId, 5);
        return Result.ok(questions);
    }

    /**
     * 提交答题并计算积分
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitAnswer(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long questionId = Long.valueOf(params.get("questionId").toString());
        String answer = params.get("answer").toString();

        Integer score = quizRecordService.saveRecord(userId, questionId, answer);
        
        // 更新用户总积分
        userService.increaseTotalScore(userId, score);

        return Result.ok(Map.of("score", score));
    }

    /**
     * 查询用户答题记录
     */
    @GetMapping("/records")
    public Result<List<QuizRecord>> getRecords(@RequestParam Long userId) {
        List<QuizRecord> records = quizRecordService.listByUserId(userId);
        return Result.ok(records);
    }

    /**
     * 查询用户总积分
     */
    @GetMapping("/total-score")
    public Result<Integer> getTotalScore(@RequestParam Long userId) {
        Integer totalScore = quizRecordService.getTotalScoreByUserId(userId);
        return Result.ok(totalScore != null ? totalScore : 0);
    }
}