package com.heritage.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 发布评论请求参数
 */
@Data
public class CommentDTO {

    /** 非遗项目ID */
    @NotNull(message = "非遗项目ID不能为空")
    private Long heritageId;

    /** 评论内容 */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容最长1000个字符")
    private String content;
}
