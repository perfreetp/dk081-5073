package com.safetycampus.supervise.enums;

import lombok.Getter;

@Getter
public enum FlowTypeEnum {

    ALARM_RECEIVE(1, "报警接收"),
    ALARM_MERGE(2, "警情合并"),
    ALARM_ESCALATE(3, "警情上推"),
    SUPERVISE_CREATE(4, "创建督办"),
    HANDLE_FEEDBACK(5, "处置反馈"),
    ALARM_TRANSFER(6, "警情转派"),
    ALARM_CLOSE(7, "关闭警情"),
    REMIND_TIMEOUT(8, "超时催办"),
    REMIND_MANUAL(9, "手动催办");

    private final Integer code;
    private final String desc;

    FlowTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (FlowTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
