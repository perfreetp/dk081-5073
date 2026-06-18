package com.safetycampus.common.enums;

import lombok.Getter;

@Getter
public enum PartyTypeEnum {

    EDUCATION_BUREAU(1, "教育局"),
    SCHOOL(2, "学校"),
    POLICE_STATION(3, "派出所"),
    SYSTEM(4, "系统");

    private final Integer code;
    private final String desc;

    PartyTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PartyTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }

    public static PartyTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PartyTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
