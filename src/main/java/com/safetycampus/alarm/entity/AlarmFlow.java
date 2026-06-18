package com.safetycampus.alarm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("alarm_flow")
public class AlarmFlow extends BaseEntity {

    private Long alarmId;

    private Integer flowType;

    private Long operatorId;

    private String operatorName;

    private String operatorRole;

    private String remark;

    private String attachUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
