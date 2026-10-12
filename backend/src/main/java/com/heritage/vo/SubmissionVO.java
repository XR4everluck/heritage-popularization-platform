package com.heritage.vo;

import com.heritage.entity.UserSubmission;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户内容发布视图对象：实体字段 + 提交人昵称 + 关联项目名称
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubmissionVO extends UserSubmission {

    /** 提交人昵称（关联 sys_user.nickname） */
    private String submitter;

    /** 关联非遗项目名称（关联 heritage_info.name） */
    private String heritageName;
}
