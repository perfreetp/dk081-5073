package com.safetycampus.notifyrule.enums;

import lombok.Getter;

@Getter
public enum TargetTypeEnum {

    EDUCATION_DUTY(1, "教育局值班"),
    SCHOOL_SECURITY(2, "学校保卫"),
    POLICE_STATION(3, "派出所"),
    DESIGNATED_PERSON(4, "指定人员");

    private final Integer code;
    private final String desc;

    TargetTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (TargetTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
