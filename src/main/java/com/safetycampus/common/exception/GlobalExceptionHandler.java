package com.safetycampus.common.exception;

import com.safetycampus.common.result.Result;
import com.safetycampus.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常: {}", message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定异常: {}", message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束违反异常: {}", message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParamException(MissingServletRequestParameterException e) {
        String message = "缺少必要参数: " + e.getParameterName();
        log.warn(message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = "参数类型错误: " + e.getName();
        log.warn(message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体解析错误: {}", e.getMessage());
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), "请求体格式错误");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = "不支持的请求方法: " + e.getMethod();
        log.warn(message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.fail(ResultCode.FAIL.getCode(), "系统异常，请联系管理员");
    }
}
