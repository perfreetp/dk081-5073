package com.safetycampus.school.enums;

import lombok.Getter;

@Getter
public enum SchoolTypeEnum {

    KINDERGARTEN(1, "幼儿园"),
    PRIMARY_SCHOOL(2, "小学"),
    JUNIOR_HIGH(3, "初中"),
    HIGH_SCHOOL(4, "高中"),
    VOCATIONAL_SCHOOL(5, "中职"),
    UNIVERSITY(6, "高校");

    private final Integer code;
    private final String desc;

    SchoolTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (SchoolTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
