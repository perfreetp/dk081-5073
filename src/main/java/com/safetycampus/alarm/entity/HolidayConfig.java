package com.safetycampus.alarm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("holiday_config")
public class HolidayConfig extends BaseEntity {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate holidayDate;

    private String holidayName;

    private Integer holidayType;

    private Integer strategyType;

    private String notifyRange;
}
