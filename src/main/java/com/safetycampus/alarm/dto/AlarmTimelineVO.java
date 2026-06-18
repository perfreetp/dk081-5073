package com.safetycampus.alarm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlarmTimelineVO {

    private Long id;

    private Integer flowType;

    private String flowTypeName;

    private Integer partyType;

    private String partyTypeName;

    private String partyName;

    private Long operatorId;

    private String operatorName;

    private String operatorRole;

    private String remark;

    private String attachUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Long timestamp;

    private Integer durationSeconds;

    private String durationText;

    private Boolean isKeyNode;

    private String statusIcon;
}
