package com.safetycampus.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "值守复盘查询参数")
public class DutyReviewQueryDTO implements Serializable {

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "值班班次:1-白班 2-夜班")
    private Integer dutyShift;

    @Schema(description = "值班人ID")
    private Long userId;

    @Schema(description = "街镇ID")
    private Long townId;

    @Schema(description = "学校分组ID")
    private Long groupId;

    @Schema(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校")
    private Integer schoolType;
}
