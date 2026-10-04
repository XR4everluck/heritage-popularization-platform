package com.heritage.exception;

import com.heritage.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 *
 * <p>业务逻辑校验失败时手动抛出（如"用户名已存在"、"账号已被禁用"），
 * 由 GlobalExceptionHandler 统一捕获并转换为 Result 返回，避免在
 * Controller 层到处 try-catch。</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String msg) {
        this(ResultCode.ERROR.getCode(), msg);
    }

    public BusinessException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMsg());
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
