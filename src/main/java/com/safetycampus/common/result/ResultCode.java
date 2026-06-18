package com.safetycampus.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    TOKEN_EXPIRED(40101, "登录已过期，请重新登录"),
    TOKEN_INVALID(40102, "Token无效"),
    TOKEN_EMPTY(40103, "Token为空"),
    ACCOUNT_LOCKED(40104, "账号已锁定"),
    ACCOUNT_EXPIRED(40105, "账号已过期"),
    PASSWORD_ERROR(40106, "密码错误"),
    PASSWORD_EXPIRED(40107, "密码已过期"),

    PERMISSION_OPERATION_DENIED(40301, "无操作权限"),
    PERMISSION_DATA_DENIED(40302, "无数据权限"),
    PERMISSION_API_DENIED(40303, "无接口权限"),
    PERMISSION_ROLE_INSUFFICIENT(40304, "角色权限不足"),

    PARAM_MISSING(40001, "参数缺失: {0}"),
    PARAM_FORMAT_ERROR(40002, "参数格式错误: {0}"),
    PARAM_RANGE_ERROR(40003, "参数范围错误: {0}"),
    PARAM_VALIDATE_ERROR(40004, "参数校验失败"),
    REQUEST_BODY_EMPTY(40005, "请求体为空"),

    ALARM_ALREADY_HANDLED(30001, "警情已处置"),
    ALARM_ALREADY_CLOSED(30002, "警情已关闭"),
    ALARM_STATUS_NOT_ALLOWED(30003, "警情状态不允许"),
    ALARM_NOT_FOUND(30004, "警情不存在"),
    ALARM_CANNOT_MERGE_SELF(30005, "不能合并自己"),

    SCHOOL_NOT_FOUND(20001, "学校不存在"),
    SCHOOL_DISABLED(20002, "学校已停用"),
    DEVICE_NOT_FOUND(20003, "设备不存在"),
    DEVICE_OFFLINE(20004, "设备已离线"),
    DEVICE_CODE_EXISTS(20005, "设备编码已存在"),

    RULE_NAME_EXISTS(60001, "规则名称已存在"),
    RULE_ALREADY_ENABLED(60002, "规则已启用"),
    RULE_CONDITION_CONFLICT(60003, "规则条件冲突"),

    EXPORT_FAILED(70001, "文件导出失败"),
    FILE_SIZE_EXCEEDED(70002, "文件大小超限"),
    FILE_FORMAT_UNSUPPORTED(70003, "文件格式不支持"),

    DATABASE_ERROR(80001, "数据库操作失败"),
    CACHE_ERROR(80002, "缓存操作失败"),
    MQ_ERROR(80003, "消息队列操作失败"),
    THIRD_PARTY_ERROR(80004, "第三方接口调用失败"),
    SYSTEM_INTERNAL_ERROR(80005, "系统内部错误"),

    @Deprecated
    PARAM_ERROR(400, "参数错误"),
    @Deprecated
    UNAUTHORIZED(401, "未授权"),
    @Deprecated
    FORBIDDEN(403, "禁止访问"),
    @Deprecated
    NOT_FOUND(404, "资源不存在"),
    @Deprecated
    LOGIN_ERROR(1001, "用户名或密码错误"),
    @Deprecated
    USER_DISABLED(1002, "用户已被禁用"),
    @Deprecated
    USER_NOT_EXIST(1005, "用户不存在"),
    @Deprecated
    USER_ALREADY_EXIST(1006, "用户已存在"),
    @Deprecated
    SCHOOL_NOT_EXIST(2001, "学校不存在"),
    @Deprecated
    SCHOOL_CODE_EXIST(2002, "学校编码已存在"),
    @Deprecated
    DEVICE_NOT_EXIST(2101, "设备不存在"),
    @Deprecated
    DEVICE_CODE_EXIST(2102, "设备编码已存在"),
    @Deprecated
    ALARM_NOT_EXIST(3001, "警情不存在"),
    @Deprecated
    ALARM_STATUS_ERROR(3002, "警情状态错误"),
    @Deprecated
    PERMISSION_DENIED(9001, "无操作权限"),
    @Deprecated
    DATA_PERMISSION_DENIED(9002, "无数据权限");

    private final Integer code;
    private final String message;

    public String formatMessage(Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        String formatted = message;
        for (int i = 0; i < args.length; i++) {
            formatted = formatted.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return formatted;
    }
}
