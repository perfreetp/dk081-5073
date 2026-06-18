package com.safetycampus.alarm.dto;

import com.safetycampus.common.entity.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlarmQueryDTO extends PageQuery implements Serializable {

    private String alarmNo;

    private Long schoolId;

    private Long groupId;

    private Integer alarmType;

    private Integer alarmLevel;

    private Integer status;

    private Integer isEscalated;

    private Integer isHoliday;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String keyword;
}
