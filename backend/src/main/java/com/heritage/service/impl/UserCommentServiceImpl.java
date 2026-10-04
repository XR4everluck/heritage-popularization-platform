package com.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.common.ResultCode;
import com.heritage.dto.CommentDTO;
import com.heritage.entity.HeritageInfo;
import com.heritage.entity.SysUser;
import com.heritage.entity.UserComment;
import com.heritage.exception.BusinessException;
import com.heritage.mapper.UserCommentMapper;
import com.heritage.service.HeritageInfoService;
import com.heritage.service.SysUserService;
import com.heritage.service.UserCommentService;
import com.heritage.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论业务实现：发布校验、分页组装评论人/非遗信息、本人评论归属校验
 */
@Service
@RequiredArgsConstructor
public class UserCommentServiceImpl extends ServiceImpl<UserCommentMapper, UserComment> implements UserCommentService {

    private final SysUserService sysUserService;

    private final HeritageInfoService heritageInfoService;

    @Override
    public void post(Long userId, CommentDTO dto) {
        if (heritageInfoService.getById(dto.getHeritageId()) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        UserComment comment = new UserComment();
        comment.setUserId(userId);
        comment.setHeritageId(dto.getHeritageId());
        comment.setContent(dto.getContent());
        comment.setLikeCount(0);
        comment.setStatus(1);
        this.save(comment);
    }

    @Override
    public Page<CommentVO> pageVO(Page<UserComment> page, Long heritageId, Integer status) {
        LambdaQueryWrapper<UserComment> qw = new LambdaQueryWrapper<>();
        qw.eq(heritageId != null, UserComment::getHeritageId, heritageId)
                .eq(status != null, UserComment::getStatus, status)
                .orderByDesc(UserComment::getCreateTime);
        Page<UserComment> result = this.page(page, qw);
        Page<CommentVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<UserComment> records = result.getRecords();
        if (records.isEmpty()) {
            voPage.setRecords(new ArrayList<>());
            return voPage;
        }
        // 批量查询评论人与非遗信息，避免逐条查询
        Map<Long, SysUser> users = sysUserService.listByIds(
                        records.stream().map(UserComment::getUserId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        Map<Long, HeritageInfo> heritages = heritageInfoService.listByIds(
                        records.stream().map(UserComment::getHeritageId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(HeritageInfo::getId, h -> h));
        List<CommentVO> vos = new ArrayList<>();
        for (UserComment comment : records) {
            CommentVO vo = new CommentVO();
            vo.setId(comment.getId());
            vo.setHeritageId(comment.getHeritageId());
            vo.setUserId(comment.getUserId());
            vo.setContent(comment.getContent());
            vo.setLikeCount(comment.getLikeCount());
            vo.setStatus(comment.getStatus());
            vo.setCreateTime(comment.getCreateTime());
            SysUser user = users.get(comment.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }
            HeritageInfo info = heritages.get(comment.getHeritageId());
            if (info != null) {
                vo.setHeritageName(info.getName());
            }
            vos.add(vo);
        }
        voPage.setRecords(vos);
        return voPage;
    }

    @Override
    public void deleteOwn(Long userId, Long commentId) {
        UserComment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "只能删除自己的评论");
        }
        this.removeById(commentId);
    }
}
