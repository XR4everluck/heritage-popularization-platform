package com.heritage.controller.front;

import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.QuizQuestion;
import com.heritage.entity.QuizRecord;
import com.heritage.exception.BusinessException;
import com.heritage.service.QuizQuestionService;
import com.heritage.service.QuizRecordService;
import com.heritage.service.SysUserService;
import com.heritage.util.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 前台测验接口：随机题目、提交答题、查询答题记录与积分
 *
 * <p>答题相关接口一律要求登录，用户身份取自 JWT（AuthContext），
 * 不接受前端传入的 userId，避免伪造身份为他人刷分。</p>
 */
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class FrontQuizController {

    private final QuizQuestionService quizQuestionService;
    private final QuizRecordService quizRecordService;
    private final SysUserService sysUserService;

    /**
     * 根据非遗ID随机抽取5道题（题目内容不含正确答案以外的敏感信息）
     */
    @GetMapping("/random-questions")
    public Result<List<QuizQuestion>> getRandomQuestions(@RequestParam Long heritageId) {
        return Result.ok(quizQuestionService.getRandomQuestions(heritageId, 5));
    }

    /**
     * 提交答题：校验答案、计算得分并累加到用户总积分
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitAnswer(@RequestBody Map<String, Object> params,
                                                    HttpServletRequest request) {
        Long userId = requireUserId(request);
        Object questionIdValue = params.get("questionId");
        Object answerValue = params.get("answer");
        if (questionIdValue == null || answerValue == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "题目ID与答案不能为空");
        }

        Integer score = quizRecordService.saveRecord(userId,
                Long.valueOf(questionIdValue.toString()), answerValue.toString());
        sysUserService.increaseTotalScore(userId, score);

        return Result.ok(Map.of("score", score == null ? 0 : score));
    }

    /**
     * 查询当前登录用户的答题记录
     */
    @GetMapping("/records")
    public Result<List<QuizRecord>> getRecords(HttpServletRequest request) {
        return Result.ok(quizRecordService.listByUserId(requireUserId(request)));
    }

    /**
     * 查询当前登录用户的累计积分
     */
    @GetMapping("/total-score")
    public Result<Integer> getTotalScore(HttpServletRequest request) {
        Integer totalScore = quizRecordService.getTotalScoreByUserId(requireUserId(request));
        return Result.ok(totalScore == null ? 0 : totalScore);
    }

    /** 取当前登录用户ID；未登录（拦截器未放行）时给出明确的 401 提示 */
    private Long requireUserId(HttpServletRequest request) {
        Long userId = AuthContext.getUserId(request);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }
}
