package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.entity.QuizQuestion;

import java.util.List;

/**
 * 非遗小测验题目业务接口
 */
public interface QuizQuestionService extends IService<QuizQuestion> {

    /**
     * 保存题目信息
     *
     * @param question 题目信息
     * @return 保存后的题目信息
     */
    QuizQuestion saveQuestion(QuizQuestion question);

    /**
     * 更新题目信息
     *
     * @param question 题目信息
     * @return 更新后的题目信息
     */
    QuizQuestion updateQuestion(QuizQuestion question);

    /**
     * 删除题目信息
     *
     * @param id 题目ID
     * @return 是否删除成功
     */
    boolean deleteQuestion(Long id);

    /**
     * 启用/禁用题目
     *
     * @param id     题目ID
     * @param status 状态：0-禁用，1-启用
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 根据非遗项目ID获取题目列表
     *
     * @param heritageId 非遗项目ID
     * @return 题目列表
     */
    List<QuizQuestion> getByHeritageId(Long heritageId);

    /**
     * 批量导入题目
     *
     * @param questions 题目列表
     * @return 导入数量
     */
    int batchImport(List<QuizQuestion> questions);

    /**
     * 随机获取指定数量的题目
     *
     * @param heritageId 非遗项目ID（可选）
     * @        count 题目数量
     * @return 题目列表
     */
    List<QuizQuestion> getRandomQuestions(Long heritageId, Integer count);

    /**
     * 根据非遗项目ID获取全部题目（含已禁用，供后台管理列表使用）
     *
     * @param heritageId 非遗项目ID
     * @return 题目列表，按ID升序
     */
    List<QuizQuestion> listByHeritageId(Long heritageId);
}