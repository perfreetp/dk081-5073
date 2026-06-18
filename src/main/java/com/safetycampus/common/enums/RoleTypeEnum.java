package com.safetycampus.common.enums;

import lombok.Getter;

@Getter
public enum RoleTypeEnum {

    SCHOOL_SECURITY(1, "学校保卫"),
    EDUCATION_BUREAU(2, "教育局"),
    ADMIN(3, "系统管理员");

    private final Integer code;
    private final String desc;

    RoleTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoleTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RoleTypeEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}
