package com.safetycampus.notifyrule.enums;

import lombok.Getter;

@Getter
public enum NotifyChannelEnum {

    SMS(1, "短信"),
    APP(2, "APP"),
    CALL(3, "电话"),
    EMAIL(4, "邮件");

    private final Integer code;
    private final String desc;

    NotifyChannelEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (NotifyChannelEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
