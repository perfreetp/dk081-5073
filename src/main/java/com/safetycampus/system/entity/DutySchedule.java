package com.safetycampus.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("duty_schedule")
public class DutySchedule extends BaseEntity {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dutyDate;

    private Integer dutyType;

    private Long userId;

    private String userName;

    private Integer shiftType;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private Integer isStandby;
}
