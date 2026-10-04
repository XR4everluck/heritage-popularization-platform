package com.heritage.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 全局统一返回结果封装
 *
 * <p>所有 Controller 接口统一返回该结构，前端按 code 判断业务是否成功：
 * <pre>{ "code": 200, "msg": "操作成功", "data": {} }</pre></p>
 *
 * @param <T> 返回数据类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码：200-成功，其他为失败（见 ResultCode） */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 返回数据 */
    private T data;

    private Result() {
    }

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 成功，不携带数据 */
    public static <T> Result<T> ok() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    /** 成功，携带数据 */
    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    /** 失败，使用默认错误码 500 */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ResultCode.ERROR.getCode(), msg, null);
    }

    /** 失败，自定义错误码与提示 */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /** 失败，使用状态码枚举 */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null);
    }
}
