package com.safetycampus.alarm.enums;

import lombok.Getter;

@Getter
public enum AlarmLevelEnum {

    CRITICAL(1, "重大"),
    MAJOR(2, "较大"),
    GENERAL(3, "一般");

    private final Integer code;
    private final String desc;

    AlarmLevelEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (AlarmLevelEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
