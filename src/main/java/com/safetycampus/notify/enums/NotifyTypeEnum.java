package com.safetycampus.notify.enums;

import lombok.Getter;

@Getter
public enum NotifyTypeEnum {

    SMS(1, "短信"),
    APP_PUSH(2, "APP推送"),
    CALL(3, "电话"),
    EMAIL(4, "邮件");

    private final Integer code;
    private final String desc;

    NotifyTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (NotifyTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
