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
     * 分页查询题目列表
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param keyword  搜索关键词
     * @param heritageId 非遗项目ID
     * @param difficulty 难度等级
     * @param status   状态：0-禁用，1-启用
     * @return 题目列表
     */
    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<QuizQuestion>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long heritageId,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) Integer status) {
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<QuizQuestion> pageInfo = quizQuestionService.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize),
                new LambdaQueryWrapper<QuizQuestion>()
                        .like(keyword != null, QuizQuestion::getQuestion, keyword)
                        .eq(heritageId != null, QuizQuestion::getHeritageId, heritageId)
                        .eq(difficulty != null, QuizQuestion::getDifficulty, difficulty)
                        .eq(status != null, QuizQuestion::getStatus, status)
                        .orderByAsc(QuizQuestion::getSort)
                        .orderByDesc(QuizQuestion::getCreateTime)
        );
        
        return Result.ok(pageInfo);
    }

    /**
     * 查询题库列表（按非遗ID）
     */
    @GetMapping("/list")
    public Result<List<QuizQuestion>> list(@RequestParam Long heritageId) {
        return Result.ok(quizQuestionService.listByHeritageId(heritageId));
    }

    /**
     * 获取题目详情
     */
    @GetMapping("/{id}")
    public Result<QuizQuestion> get(@PathVariable Long id) {
        QuizQuestion question = quizQuestionService.getById(id);
        if (question == null) {
            return Result.error("题目不存在");
        }
        return Result.ok(question);
    }

    /**
     * 新增题目
     */
    @PostMapping
    public Result<QuizQuestion> add(@RequestBody QuizQuestion question) {
        try {
            question = quizQuestionService.saveQuestion(question);
            return Result.ok(question);
        } catch (Exception e) {
            return Result.error("新增失败：" + e.getMessage());
        }
    }

    /**
     * 修改题目
     */
    @PutMapping
    public Result<QuizQuestion> update(@RequestBody QuizQuestion question) {
        try {
            question = quizQuestionService.updateQuestion(question);
            return Result.ok(question);
        } catch (Exception e) {
            return Result.error("修改失败：" + e.getMessage());
        }
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            boolean success = quizQuestionService.deleteQuestion(id);
            if (success) {
                return Result.ok();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 启用/禁用题目
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = quizQuestionService.updateStatus(id, status);
            if (success) {
                return Result.ok();
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 批量导入题目
     */
    @PostMapping("/batch-import")
    public Result<Integer> batchImport(@RequestBody List<QuizQuestion> questions) {
        try {
            int count = quizQuestionService.batchImport(questions);
            return Result.ok(count);
        } catch (Exception e) {
            return Result.error("批量导入失败：" + e.getMessage());
        }
    }

    /**
     * 随机获取指定数量的题目
     */
    @GetMapping("/random")
    public Result<List<QuizQuestion>> getRandomQuestions(
            @RequestParam(required = false) Long heritageId,
            @RequestParam(defaultValue = "5") Integer count) {
        List<QuizQuestion> questions = quizQuestionService.getRandomQuestions(heritageId, count);
        return Result.ok(questions);
    }
}