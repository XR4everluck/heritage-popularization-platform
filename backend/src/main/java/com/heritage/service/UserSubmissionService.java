package com.heritage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.dto.SubmissionDTO;
import com.heritage.entity.UserSubmission;
import com.heritage.vo.SubmissionVO;

import java.util.List;

/**
 * 用户内容发布审核业务：提交、我的发布、管理端列表与审核
 */
public interface UserSubmissionService extends IService<UserSubmission> {

    /**
     * 用户提交内容（默认待审核）
     *
     * @param userId 提交用户ID
     * @param dto    发布内容
     * @return 保存后的 VO（含ID）
     */
    SubmissionVO submit(Long userId, SubmissionDTO dto);

    /**
     * 我的发布列表（按提交时间倒序）
     */
    List<SubmissionVO> listMy(Long userId);

    /**
     * 管理端列表（status 为空时查询全部，按提交时间倒序）
     */
    List<SubmissionVO> listForAdmin(Integer status);

    /**
     * 审核：仅待审核记录可操作
     *
     * @param id         发布ID
     * @param status     1-通过，2-驳回
     * @param reviewNote 审核意见（驳回原因）
     * @return 审核后的实体；记录不存在或已审核时返回 null
     */
    SubmissionVO review(Long id, Integer status, String reviewNote);

    /**
     * 下架：仅已通过记录可操作（全民科普/首页随之隐藏）
     *
     * @param id   发布ID
     * @param note 下架原因（可空）
     * @return 下架后的 VO；记录不存在或非已通过状态时返回 null
     */
    SubmissionVO offline(Long id, String note);

    /**
     * 全民科普分页（公开接口）：仅已通过内容，按审核时间倒序
     *
     * @param type     类型筛选：news/course（可空=全部）
     * @param page     页码
     * @param pageSize 每页条数
     */
    Page<SubmissionVO> pageApproved(Page<UserSubmission> page, String type);
}
