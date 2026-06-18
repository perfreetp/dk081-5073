package com.safetycampus.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "对比结果")
public class CompareResultVO implements Serializable {

    @Schema(description = "当前期")
    private String currentPeriod;

    @Schema(description = "对比期")
    private String comparePeriod;

    @Schema(description = "对比类型: month-月度, quarter-季度")
    private String compareType;

    @Schema(description = "学校对比列表")
    private List<SchoolCompareItemVO> schoolCompareList;

    @Schema(description = "整体汇总")
    private String overallSummary;

    @Schema(description = "导出URL(可选)")
    private String exportUrl;
}
