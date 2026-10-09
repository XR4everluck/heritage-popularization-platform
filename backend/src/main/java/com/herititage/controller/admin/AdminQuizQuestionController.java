package com.heritage.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.entity.QuizQuestion;
import com.heritage.exception.BusinessException;
import com.heritage.service.QuizQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台题库管理接口
 */
@RestController
@RequestMapping("/api/admin/quiz-question")
@RequiredArgsConstructor
public class AdminQuizQuestionController {

    private final QuizQuestionService quizQuestionService;

    /**
     * 查询题库列表（按非遗ID）
     */
    @GetMapping("/list")
    public Result<List<QuizQuestion>> list(@RequestParam Long heritageId) {
        return Result.ok(quizQuestionService.listByHeritageId(heritageId));
    }

    /**
     * 新增题目
     */
    @PostMapping
    public Result<Void> add(@RequestBody QuizQuestion question) {
        quizQuestionService.save(question);
        return Result.ok();
    }

    /**
     * 修改题目
     */
    @PutMapping
    public Result<Void> update(@RequestBody QuizQuestion question) {
        if (question.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "题目ID不能为空");
        }
        quizQuestionService.updateById(question);
        return Result.ok();
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        quizQuestionService.removeById(id);
        return Result.ok();
    }
}