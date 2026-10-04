package com.heritage.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局状态码枚举
 *
 * <p>200 表示成功；4xx 表示客户端问题；5xx 表示服务端问题。
 * 后续业务模块可按需在 1xxx 段扩展业务错误码（如 1001-用户名已存在）。</p>
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "操作成功"),

    /** 请求参数错误 */
    PARAM_ERROR(400, "请求参数错误"),

    /** 未登录或登录已过期（阶段3 JWT 鉴权使用） */
    UNAUTHORIZED(401, "未登录或登录已过期"),

    /** 无权限访问 */
    FORBIDDEN(403, "无权限访问"),

    /** 资源不存在 */
    NOT_FOUND(404, "请求的资源不存在"),

    /** 系统内部错误 */
    ERROR(500, "系统繁忙，请稍后重试");

    private final Integer code;
    private final String msg;
}
