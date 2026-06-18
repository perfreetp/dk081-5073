package com.safetycampus.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "对比查询参数")
public class CompareQueryDTO implements Serializable {

    @Schema(description = "对比类型: month-月度, quarter-季度")
    private String compareType;

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "分组ID")
    private Long groupId;

    @Schema(description = "街镇ID")
    private Long townId;

    @Schema(description = "当前期: YYYY-MM 或 YYYY-QN")
    private String currentPeriod;

    @Schema(description = "对比期: YYYY-MM 或 YYYY-QN")
    private String comparePeriod;
}
