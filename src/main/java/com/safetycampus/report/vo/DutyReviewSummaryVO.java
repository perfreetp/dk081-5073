package com.safetycampus.report.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "值守复盘汇总")
public class DutyReviewSummaryVO implements Serializable {

    @Schema(description = "查询开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate queryStartDate;

    @Schema(description = "查询结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate queryEndDate;

    @Schema(description = "总班次数")
    private Long totalShiftCount;

    @Schema(description = "总接警量")
    private Long totalAlarmCount;

    @Schema(description = "总重大警情数")
    private Long totalCriticalCount;

    @Schema(description = "总超时数")
    private Long totalTimeoutCount;

    @Schema(description = "整体响应超时率(%)")
    private BigDecimal overallResponseRate;

    @Schema(description = "整体平均响应时间(秒)")
    private BigDecimal overallResponseAvgTime;

    @Schema(description = "整体派出所反馈及时率(%)")
    private BigDecimal overallPoliceFeedbackRate;

    @Schema(description = "整体处置完成率(%)")
    private BigDecimal overallCompletionRate;

    @Schema(description = "班次复盘列表")
    private List<DutyReviewShiftVO> shiftReviewList;

    @Schema(description = "学校维度复盘列表")
    private List<DutyReviewSchoolVO> schoolReviewList;

    @Schema(description = "TOP5高风险学校")
    private List<DutyReviewSchoolVO> top5HighRiskSchools;
}
