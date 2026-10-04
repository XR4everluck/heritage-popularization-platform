package com.heritage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.dto.CommentDTO;
import com.heritage.entity.UserComment;
import com.heritage.vo.CommentVO;

/**
 * 评论业务接口：继承 IService 获得通用 CRUD，另含发布/分页组装/删除本人评论方法
 */
public interface UserCommentService extends IService<UserComment> {

    /**
     * 发布评论：校验非遗项目存在，默认状态正常
     */
    void post(Long userId, CommentDTO dto);

    /**
     * 分页查询评论并组装评论人昵称/头像与非遗名称
     *
     * @param status      null 不过滤（后台管理用）；传 1 仅查正常评论（前台用）
     * @param heritageId  null 查全部非遗（后台用）
     */
    Page<CommentVO> pageVO(Page<UserComment> page, Long heritageId, Integer status);

    /**
     * 删除本人评论：校验归属，逻辑删除
     */
    void deleteOwn(Long userId, Long commentId);
}
