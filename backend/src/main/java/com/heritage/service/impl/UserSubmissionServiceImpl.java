package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.common.ResultCode;
import com.heritage.dto.SubmissionDTO;
import com.heritage.entity.HeritageInfo;
import com.heritage.entity.HeritageNews;
import com.heritage.entity.SysUser;
import com.heritage.entity.UserSubmission;
import com.heritage.exception.BusinessException;
import com.heritage.mapper.UserSubmissionMapper;
import com.heritage.service.HeritageInfoService;
import com.heritage.service.HeritageNewsService;
import com.heritage.service.SysUserService;
import com.heritage.service.UserSubmissionService;
import com.heritage.vo.SubmissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户内容发布审核业务实现：提交校验、VO 组装（提交人昵称/项目名称）、审核归属与状态校验
 */
@Service
@RequiredArgsConstructor
public class UserSubmissionServiceImpl extends ServiceImpl<UserSubmissionMapper, UserSubmission> implements UserSubmissionService {

    private final SysUserService sysUserService;

    private final HeritageInfoService heritageInfoService;

    private final HeritageNewsService heritageNewsService;

    @Override
    public SubmissionVO submit(Long userId, SubmissionDTO dto) {
        // 入参校验：标题必填；快讯须有正文；视频须有链接；类型白名单
        boolean isNews = "news".equals(dto.getType());
        boolean isCourse = "course".equals(dto.getType());
        if (!isNews && !isCourse) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        if (isNews && (dto.getContent() == null || dto.getContent().trim().isEmpty())) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        if (isCourse && (dto.getVideoUrl() == null || dto.getVideoUrl().trim().isEmpty())) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }

        UserSubmission submission = new UserSubmission();
        submission.setUserId(userId);
        submission.setType(dto.getType());
        submission.setTitle(dto.getTitle().trim());
        submission.setHeritageId(dto.getHeritageId());
        submission.setContent(dto.getContent());
        submission.setVideoUrl(dto.getVideoUrl());
        submission.setStatus(0);
        submission.setSubmitTime(new Date());
        this.save(submission);
        return toVO(submission);
    }

    @Override
    public List<SubmissionVO> listMy(Long userId) {
        LambdaQueryWrapper<UserSubmission> qw = new LambdaQueryWrapper<>();
        qw.eq(UserSubmission::getUserId, userId)
                .orderByDesc(UserSubmission::getSubmitTime);
        return assembleVO(this.list(qw));
    }

    @Override
    public List<SubmissionVO> listForAdmin(Integer status) {
        LambdaQueryWrapper<UserSubmission> qw = new LambdaQueryWrapper<>();
        qw.eq(status != null, UserSubmission::getStatus, status)
                .orderByDesc(UserSubmission::getSubmitTime);
        return assembleVO(this.list(qw));
    }

    @Override
    public SubmissionVO review(Long id, Integer status, String reviewNote) {
        UserSubmission submission = this.getById(id);
        if (submission == null || submission.getStatus() != 0) {
            return null;
        }
        submission.setStatus(status);
        submission.setReviewNote(reviewNote);
        submission.setReviewTime(new Date());
        this.updateById(submission);
        // 通过的快讯自动进入科普快讯表，前台首页/快讯列表即可展示
        if (status == 1 && "news".equals(submission.getType())) {
            HeritageNews news = new HeritageNews();
            news.setTitle(submission.getTitle());
            news.setContent(submission.getContent());
            news.setHeritageId(submission.getHeritageId());
            news.setIsTop(0);
            news.setStatus(1);
            news.setPublishTime(LocalDateTime.now());
            news.setCreateTime(LocalDateTime.now());
            news.setUpdateTime(LocalDateTime.now());
            heritageNewsService.save(news);
        }
        return toVO(submission);
    }

    @Override
    public SubmissionVO offline(Long id, String note) {
        UserSubmission submission = this.getById(id);
        if (submission == null || submission.getStatus() != 1) {
            return null;
        }
        submission.setStatus(0);
        submission.setReviewNote("已下架" + (note == null || note.isEmpty() ? "" : "：" + note));
        submission.setReviewTime(new Date());
        this.updateById(submission);
        return toVO(submission);
    }

    @Override
    public Page<SubmissionVO> pageApproved(Page<UserSubmission> page, String type) {
        // 全民科普固定只查已通过内容，查询条件后端兜底，杜绝未审核/未通过内容外露
        LambdaQueryWrapper<UserSubmission> qw = new LambdaQueryWrapper<>();
        qw.eq(UserSubmission::getStatus, 1)
                .eq(type != null && !type.isEmpty(), UserSubmission::getType, type)
                .orderByDesc(UserSubmission::getReviewTime);
        Page<UserSubmission> result = this.page(page, qw);
        Page<SubmissionVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(assembleVO(result.getRecords()));
        return voPage;
    }

    /** 组装 VO：批量取提交人昵称与关联项目名称，避免 N+1 查询 */
    private List<SubmissionVO> assembleVO(List<UserSubmission> list) {
        List<SubmissionVO> vos = new ArrayList<>();
        for (UserSubmission submission : list) {
            vos.add(toVO(submission));
        }
        return vos;
    }

    private SubmissionVO toVO(UserSubmission submission) {
        SubmissionVO vo = new SubmissionVO();
        vo.setId(submission.getId());
        vo.setUserId(submission.getUserId());
        vo.setType(submission.getType());
        vo.setTitle(submission.getTitle());
        vo.setHeritageId(submission.getHeritageId());
        vo.setContent(submission.getContent());
        vo.setVideoUrl(submission.getVideoUrl());
        vo.setStatus(submission.getStatus());
        vo.setReviewNote(submission.getReviewNote());
        vo.setDeleted(submission.getDeleted());
        vo.setSubmitTime(submission.getSubmitTime());
        vo.setReviewTime(submission.getReviewTime());
        SysUser user = sysUserService.getById(submission.getUserId());
        vo.setSubmitter(user == null ? null : user.getNickname());
        if (submission.getHeritageId() != null) {
            HeritageInfo info = heritageInfoService.getById(submission.getHeritageId());
            vo.setHeritageName(info == null ? null : info.getName());
        }
        return vo;
    }
}
