package com.heritage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.dto.ProgressDTO;
import com.heritage.entity.StudyProgress;
import com.heritage.vo.ProgressVO;

import java.util.List;

/**
 * 学习进度业务接口：继承 IService 获得通用 CRUD，另含进度更新与查询方法
 */
public interface StudyProgressService extends IService<StudyProgress> {

    /**
     * 更新学习进度（按"用户+章节"维度 upsert）：
     * 记录不存在则新建；存在则累计学习时长并按需更新完成状态
     */
    void upsert(Long userId, ProgressDTO dto);

    /**
     * 查询我的学习进度并组装课程名称/章节标题，按课程与章节顺序排序
     *
     * @param courseId null 查全部课程
     */
    List<ProgressVO> listMy(Long userId, Long courseId);
}
