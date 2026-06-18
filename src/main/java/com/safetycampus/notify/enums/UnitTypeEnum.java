package com.safetycampus.notify.enums;

import lombok.Getter;

@Getter
public enum UnitTypeEnum {

    EDUCATION_BUREAU(1, "教育局"),
    SCHOOL(2, "学校"),
    POLICE_STATION(3, "派出所"),
    HOSPITAL(4, "医院"),
    FIRE_DEPARTMENT(5, "消防"),
    OTHER(6, "其他");

    private final Integer code;
    private final String desc;

    UnitTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (UnitTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
