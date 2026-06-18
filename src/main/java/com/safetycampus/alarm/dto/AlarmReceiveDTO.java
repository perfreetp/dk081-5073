package com.safetycampus.alarm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AlarmReceiveDTO implements Serializable {

    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @NotNull(message = "报警类型不能为空")
    private Integer alarmType;

    @NotNull(message = "警情级别不能为空")
    private Integer alarmLevel;

    @NotBlank(message = "警情标题不能为空")
    private String alarmTitle;

    private String alarmContent;

    private String reporterName;

    private String reporterPhone;

    private String location;

    private BigDecimal longitude;

    private BigDecimal latitude;
}
