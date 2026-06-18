package com.safetycampus.alarm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlarmTimelineQueryDTO {

    @NotNull(message = "警情ID不能为空")
    private Long alarmId;

    private List<Integer> partyTypes;

    private List<Integer> flowTypes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
