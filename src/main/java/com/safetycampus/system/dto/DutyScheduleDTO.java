package com.safetycampus.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "值班排班DTO")
public class DutyScheduleDTO implements Serializable {

    @Schema(description = "ID")
    private Long id;

    @NotNull(message = "值班日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "值班日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dutyDate;

    @NotNull(message = "值班类型不能为空")
    @Schema(description = "值班类型:1-工作日,2-周末,3-节假日", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dutyType;

    @NotNull(message = "值班用户不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "用户姓名")
    private String userName;

    @Schema(description = "班次:1-白班,2-夜班")
    private Integer shiftType;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalTime endTime;

    @Schema(description = "是否备班:0-否,1-是")
    private Integer isStandby;
}
