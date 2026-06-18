package com.safetycampus.alarm.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AlarmMergeDTO implements Serializable {

    @NotNull(message = "主警情ID不能为空")
    private Long parentId;

    @NotEmpty(message = "合并警情ID列表不能为空")
    private List<Long> alarmIds;

    private String remark;

    private Long operatorId;

    private String operatorName;
}
