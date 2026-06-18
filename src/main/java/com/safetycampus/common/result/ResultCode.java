package com.safetycampus.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    LOGIN_ERROR(1001, "用户名或密码错误"),
    USER_DISABLED(1002, "用户已被禁用"),
    TOKEN_INVALID(1003, "Token无效"),
    TOKEN_EXPIRED(1004, "Token已过期"),
    USER_NOT_EXIST(1005, "用户不存在"),
    USER_ALREADY_EXIST(1006, "用户已存在"),

    SCHOOL_NOT_EXIST(2001, "学校不存在"),
    SCHOOL_CODE_EXIST(2002, "学校编码已存在"),
    DEVICE_NOT_EXIST(2101, "设备不存在"),
    DEVICE_CODE_EXIST(2102, "设备编码已存在"),
    DEVICE_OFFLINE(2103, "设备已离线"),

    ALARM_NOT_EXIST(3001, "警情不存在"),
    ALARM_STATUS_ERROR(3002, "警情状态错误"),
    ALARM_ALREADY_HANDLED(3003, "警情已处置"),

    PERMISSION_DENIED(9001, "无操作权限"),
    DATA_PERMISSION_DENIED(9002, "无数据权限"),
    ;

    private final Integer code;
    private final String message;
}
