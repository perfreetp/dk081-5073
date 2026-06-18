package com.safetycampus.alarm.enums;

import lombok.Getter;

@Getter
public enum AlarmStatusEnum {

    PENDING(1, "待处置"),
    HANDLING(2, "处置中"),
    SUPERVISING(3, "已督办"),
    HANDLED(4, "已处置"),
    CLOSED(5, "已关闭");

    private final Integer code;
    private final String desc;

    AlarmStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (AlarmStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
