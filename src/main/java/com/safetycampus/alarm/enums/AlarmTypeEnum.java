package com.safetycampus.alarm.enums;

import lombok.Getter;

@Getter
public enum AlarmTypeEnum {

    EMERGENCY(1, "紧急求助"),
    FIRE(2, "火灾"),
    SECURITY(3, "治安"),
    BULLYING(4, "校园欺凌"),
    FOOD_POISONING(5, "食物中毒"),
    NATURAL_DISASTER(6, "自然灾害"),
    OTHER(7, "其他");

    private final Integer code;
    private final String desc;

    AlarmTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (AlarmTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
