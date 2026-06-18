package com.safetycampus.plan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlanMatchStatusEnum {

    MATCHED(1, "已匹配"),
    STARTED(2, "已启动"),
    EXECUTING(3, "执行中"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消");

    private final Integer code;
    private final String desc;

    public static PlanMatchStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PlanMatchStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
