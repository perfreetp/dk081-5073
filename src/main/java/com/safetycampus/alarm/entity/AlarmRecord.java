package com.safetycampus.alarm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("alarm_record")
public class AlarmRecord extends BaseEntity {

    private String alarmNo;

    private Long schoolId;

    private Long deviceId;

    private Integer alarmType;

    private Integer alarmLevel;

    private String alarmTitle;

    private String alarmContent;

    private String reporterName;

    private String reporterPhone;

    private String location;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Long parentId;

    private Integer mergedCount;

    private Integer status;

    private Integer isEscalated;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime escalatedAt;

    private Integer isHoliday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstResponseAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handledAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closedAt;

    private Long handlerId;

    private Long supervisorId;

    private Long planId;
}
