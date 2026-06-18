package com.safetycampus.plan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StepExecStatusEnum {

    NOT_STARTED(1, "未开始"),
    IN_PROGRESS(2, "进行中"),
    COMPLETED(3, "已完成"),
    SKIPPED(4, "已跳过");

    private final Integer code;
    private final String desc;

    public static StepExecStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (StepExecStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
