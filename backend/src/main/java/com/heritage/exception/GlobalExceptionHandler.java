package com.heritage.exception;

import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * <p>拦截 Controller 层抛出的各类异常，统一转换为 Result 结构返回：
 * 业务异常返回具体提示；参数校验异常返回字段级错误信息；
 * 未知异常记录日志后返回通用提示，不向前端暴露堆栈等内部信息。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：返回业务提示信息 */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /** @RequestBody + @Valid 参数校验失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + "：" + f.getDefaultMessage())
                .collect(Collectors.joining("；"));
        log.warn("参数校验失败：{}", msg);
        return Result.error(ResultCode.PARAM_ERROR.getCode(), msg);
    }

    /** 表单对象参数绑定 + 校验失败 */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + "：" + f.getDefaultMessage())
                .collect(Collectors.joining("；"));
        log.warn("参数绑定失败：{}", msg);
        return Result.error(ResultCode.PARAM_ERROR.getCode(), msg);
    }

    /** @RequestParam / @PathVariable 上的约束注解校验失败 */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining("；"));
        log.warn("参数校验失败：{}", msg);
        return Result.error(ResultCode.PARAM_ERROR.getCode(), msg);
    }

    /** 上传文件超出大小限制 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("上传文件过大：{}", e.getMessage());
        return Result.error("上传文件过大（图片最大10MB，视频最大500MB）");
    }

    /** 兜底异常：记录完整日志，对外返回通用提示 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error(ResultCode.ERROR);
    }
}
