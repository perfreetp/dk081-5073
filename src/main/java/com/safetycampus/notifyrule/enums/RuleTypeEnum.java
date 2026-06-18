package com.safetycampus.notifyrule.enums;

import lombok.Getter;

@Getter
public enum RuleTypeEnum {

    DEFAULT(1, "默认规则"),
    HOLIDAY(2, "节假日规则"),
    NIGHT(3, "夜间规则"),
    CUSTOM(4, "自定义规则");

    private final Integer code;
    private final String desc;

    RuleTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (RuleTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
