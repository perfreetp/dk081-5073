package com.safetycampus.report.enums;

import lombok.Getter;

@Getter
public enum AssessGradeEnum {

    A("A", "优秀"),
    B("B", "良好"),
    C("C", "合格"),
    D("D", "不合格");

    private final String code;
    private final String desc;

    AssessGradeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(String code) {
        for (AssessGradeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
