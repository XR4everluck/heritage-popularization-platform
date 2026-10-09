package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.entity.QuizQuestion;
import com.heritage.mapper.QuizQuestionMapper;
import com.heritage.service.QuizQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 非遗小测验题库Service实现
 */
@Service
public class QuizQuestionServiceImpl extends ServiceImpl<QuizQuestionMapper, QuizQuestion> implements QuizQuestionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuizQuestion saveQuestion(QuizQuestion question) {
        if (question == null) {
            throw new IllegalArgumentException("题目信息不能为空");
        }

        // 设置默认值
        if (question.getStatus() == null) {
            question.setStatus(1); // 默认启用
        }
        if (question.getSort() == null) {
            question.setSort(0); // 默认排序
        }
        if (question.getDeleted() == null) {
            question.setDeleted(0); // 默认未删除
        }
        if (question.getDifficulty() == null) {
            question.setDifficulty(1); // 默认简单难度
        }
        if (question.getQuestionType() == null) {
            question.setQuestionType(1); // 默认单选题
        }

        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());

        save(question);
        return question;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuizQuestion updateQuestion(QuizQuestion question) {
        if (question == null || question.getId() == null) {
            throw new IllegalArgumentException("题目信息不能为空");
        }

        // 检查题目是否存在
        QuizQuestion existing = getById(question.getId());
        if (existing == null) {
            throw new RuntimeException("题目不存在");
        }

        question.setUpdateTime(LocalDateTime.now());
        updateById(question);
        return question;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestion(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("题目ID不能为空");
        }

        QuizQuestion question = getById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 逻辑删除
        question.setDeleted(1);
        question.setUpdateTime(LocalDateTime.now());
        return updateById(question);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new IllegalArgumentException("题目ID和状态不能为空");
        }

        QuizQuestion question = getById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        question.setStatus(status);
        question.setUpdateTime(LocalDateTime.now());
        return updateById(question);
    }

    @Override
    public List<QuizQuestion> getByHeritageId(Long heritageId) {
        if (heritageId == null) {
            throw new IllegalArgumentException("非遗项目ID不能为空");
        }

        return list(new LambdaQueryWrapper<QuizQuestion>()
                .eq(QuizQuestion::getHeritageId, heritageId)
                .eq(QuizQuestion::getStatus, 1)
                .eq(QuizQuestion::getDeleted, 0)
                .orderByAsc(QuizQuestion::getSort)
                .orderByDesc(QuizQuestion::getCreateTime)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchImport(List<QuizQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("题目列表不能为空");
        }

        int successCount = 0;
        for (QuizQuestion question : questions) {
            try {
                saveQuestion(question);
                successCount++;
            } catch (Exception e) {
                // 记录失败但继续导入其他题目
                System.err.println("导入题目失败：" + e.getMessage());
            }
        }

        return successCount;
    }

    @Override
    public List<QuizQuestion> getRandomQuestions(Long heritageId, Integer count) {
        if (count == null || count <= 0) {
            count = 5; // 默认5题
        }
        if (count > 50) {
            count = 50; // 最多50题
        }

        LambdaQueryWrapper<QuizQuestion> queryWrapper = new LambdaQueryWrapper<QuizQuestion>()
                .eq(QuizQuestion::getStatus, 1)
                .eq(QuizQuestion::getDeleted, 0);

        if (heritageId != null) {
            queryWrapper.eq(QuizQuestion::getHeritageId, heritageId);
        }

        // 获取所有符合条件的题目
        List<QuizQuestion> allQuestions = list(queryWrapper);
        
        // 随机选择指定数量的题目
        if (allQuestions.size() <= count) {
            return allQuestions;
        }

        // 随机抽样
        Random random = new Random();
        List<QuizQuestion> result = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(allQuestions.size());
            result.add(allQuestions.get(index));
            allQuestions.remove(index);
        }

        return result;
    }

    @Override
    public List<QuizQuestion> listByHeritageId(Long heritageId) {
        return this.lambdaQuery()
                .eq(QuizQuestion::getHeritageId, heritageId)
                .orderByAsc(QuizQuestion::getId)
                .list();
    }
}