package com.safetycampus.common.exception;

import com.safetycampus.common.result.Result;
import com.safetycampus.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private String getMethodName(HttpServletRequest request) {
        return request.getMethod() + " " + request.getRequestURI();
    }

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", getMethodName(request), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage()).method(getMethodName(request));
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<?> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("认证异常: {} - {}", getMethodName(request), e.getMessage());
        return Result.fail(ResultCode.TOKEN_INVALID).method(getMethodName(request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("权限不足: {} - {}", getMethodName(request), e.getMessage());
        return Result.fail(ResultCode.PERMISSION_OPERATION_DENIED).method(getMethodName(request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常: {} - {}", getMethodName(request), message);
        return Result.fail(ResultCode.PARAM_VALIDATE_ERROR.getCode(), message)
                .method(getMethodName(request));
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定异常: {} - {}", getMethodName(request), message);
        return Result.fail(ResultCode.PARAM_VALIDATE_ERROR.getCode(), message)
                .method(getMethodName(request));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束违反异常: {} - {}", getMethodName(request), message);
        return Result.fail(ResultCode.PARAM_VALIDATE_ERROR.getCode(), message)
                .method(getMethodName(request));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingParamException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("参数缺失: {} - {}", getMethodName(request), e.getParameterName());
        return Result.fail(ResultCode.PARAM_MISSING, e.getParameterName())
                .method(getMethodName(request));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("参数类型错误: {} - {}, 期望类型: {}", getMethodName(request),
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");
        return Result.fail(ResultCode.PARAM_FORMAT_ERROR, e.getName())
                .method(getMethodName(request));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("请求体解析错误: {} - {}", getMethodName(request), e.getMessage());
        String message = e.getMessage() != null && e.getMessage().contains("Required request body is missing")
                ? ResultCode.REQUEST_BODY_EMPTY.getMessage()
                : ResultCode.PARAM_FORMAT_ERROR.formatMessage("请求体");
        Integer code = e.getMessage() != null && e.getMessage().contains("Required request body is missing")
                ? ResultCode.REQUEST_BODY_EMPTY.getCode()
                : ResultCode.PARAM_FORMAT_ERROR.getCode();
        return Result.fail(code, message).method(getMethodName(request));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("不支持的请求方法: {} - {}", getMethodName(request), e.getMethod());
        return Result.fail(ResultCode.PARAM_FORMAT_ERROR, "请求方法")
                .method(getMethodName(request));
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", getMethodName(request), e.getMessage(), e);
        return Result.fail(ResultCode.SYSTEM_INTERNAL_ERROR).method(getMethodName(request));
    }
}
