package com.safetycampus.common.exception;

import com.safetycampus.common.result.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;
    private final Object[] args;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
        this.args = null;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.args = null;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.args = null;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.args = null;
    }

    public BusinessException(ResultCode resultCode, Object... args) {
        super(resultCode.formatMessage(args));
        this.code = resultCode.getCode();
        this.args = args;
    }

    public static BusinessException of(ResultCode resultCode) {
        return new BusinessException(resultCode);
    }

    public static BusinessException of(ResultCode resultCode, Object... args) {
        return new BusinessException(resultCode, args);
    }

    public static BusinessException of(String message) {
        return new BusinessException(message);
    }

    public static BusinessException of(Integer code, String message) {
        return new BusinessException(code, message);
    }
}
