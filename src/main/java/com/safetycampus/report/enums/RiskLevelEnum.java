package com.safetycampus.report.enums;

import lombok.Getter;

@Getter
public enum RiskLevelEnum {

    HIGH(1, "高风险"),
    MEDIUM(2, "中风险"),
    LOW(3, "低风险");

    private final Integer code;
    private final String desc;

    RiskLevelEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (RiskLevelEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
